package ua.toptar.toptarfuelstationpos.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.toptar.toptarfuelstationpos.dto.TransactionDto;
import ua.toptar.toptarfuelstationpos.model.FuelType;
import ua.toptar.toptarfuelstationpos.model.Transaction;
import ua.toptar.toptarfuelstationpos.model.User;
import ua.toptar.toptarfuelstationpos.repository.FuelTypeRepository;
import ua.toptar.toptarfuelstationpos.repository.TransactionRepository;
import ua.toptar.toptarfuelstationpos.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Service class for managing transactions.
 * Handles transaction creation, retrieval, and analytics such as sales and revenue by fuel type and period.
 */
@Service
public class TransactionService {

    private static final Logger logger = Logger.getLogger(TransactionService.class.getName());
    private static final BigDecimal DISCOUNT_PER_LITER = BigDecimal.valueOf(2);

    private static final String DAILY_PERIOD = "daily";
    private static final String WEEKLY_PERIOD = "weekly";
    private static final String MONTHLY_PERIOD = "monthly";

    private static final String DAILY_FORMAT = "yyyy-MM-dd";
    private static final String WEEKLY_FORMAT = "yyyy-'W'ww";
    private static final String MONTHLY_FORMAT = "yyyy-MM";

    private static final String INVALID_PERIOD_MESSAGE = "Invalid period: ";

    private final TransactionRepository transactionRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code TransactionService} with the specified dependencies.
     *
     * @param transactionRepository the repository for accessing transaction data
     * @param fuelTypeRepository the repository for accessing fuel type data
     * @param userRepository the repository for accessing user data
     */
    public TransactionService(TransactionRepository transactionRepository,
        FuelTypeRepository fuelTypeRepository,
        UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.fuelTypeRepository = fuelTypeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new transaction based on the provided transaction data.
     * Applies a discount for authenticated users and associates the transaction with the user.
     *
     * @param dto the transaction data to create
     * @return the created transaction as a DTO
     * @throws IllegalArgumentException if the fuel type ID is null, invalid, or if volume/total amount is not provided correctly
     * @throws IllegalStateException if the authenticated user or guest user is not found
     */
    public TransactionDto createTransaction(TransactionDto dto) {
        logger.info("Creating transaction with fuelTypeId: " + dto.getFuelTypeId());
        if (dto.getFuelTypeId() == null) {
            throw new IllegalArgumentException("Fuel type ID cannot be null");
        }

        FuelType fuelType = fuelTypeRepository.findById(dto.getFuelTypeId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid fuel type ID: " + dto.getFuelTypeId()));
        logger.info("Fuel type found: " + fuelType.getName());

        Transaction transaction = new Transaction();
        transaction.setFuelType(fuelType);
        transaction.setFuelTypeName(fuelType.getName());
        transaction.setTransactionDate(LocalDateTime.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getPrincipal());
        BigDecimal pricePerLiter = fuelType.getPricePerLiter();

        if (isAuthenticated) {
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
            transaction.setUser(currentUser);

            pricePerLiter = pricePerLiter.subtract(DISCOUNT_PER_LITER);
            if (pricePerLiter.compareTo(BigDecimal.ZERO) < 0) {
                pricePerLiter = BigDecimal.ZERO;
            }
        } else {
            User guestUser = userRepository.findByUsername("guest")
                .orElseThrow(() -> new IllegalStateException("Guest user not found"));
            transaction.setUser(guestUser);
        }

        transaction.setPricePerLiter(pricePerLiter);

        if (dto.getTotalAmount() != null && dto.getVolume() == null) {
            transaction.setTotalAmount(dto.getTotalAmount());
            transaction.setVolume(dto.getTotalAmount().divide(pricePerLiter, 2, BigDecimal.ROUND_HALF_UP));
        } else if (dto.getVolume() != null) {
            transaction.setVolume(dto.getVolume());
            transaction.setTotalAmount(dto.getVolume().multiply(pricePerLiter));
        } else {
            throw new IllegalArgumentException("Either volume or totalAmount must be provided");
        }

        if (transaction.getVolume().compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("Volume must be at least 1 liter");
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToDto(savedTransaction);
    }

    /**
     * Retrieves a paginated list of all transactions.
     *
     * @param page the page number to retrieve
     * @param size the number of transactions per page
     * @return a paginated list of transactions as DTOs
     */
    public Page<TransactionDto> getAllTransactions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAll(pageable)
            .map(this::mapToDto);
    }

    /**
     * Retrieves the most recent transactions, limited to 5 entries, sorted by transaction date in ascending order.
     *
     * @return a paginated list of recent transactions as DTOs
     */
    public Page<TransactionDto> getRecentTransactions() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("transactionDate").ascending());
        return transactionRepository.findAll(pageable)
            .map(this::mapToDto);
    }

    /**
     * Retrieves a paginated list of transactions for a specific user.
     *
     * @param userId the ID of the user
     * @param page the page number to retrieve
     * @param size the number of transactions per page
     * @return a paginated list of transactions for the specified user as DTOs
     */
    public Page<TransactionDto> getUserTransactions(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByUserId(userId, pageable)
            .map(this::mapToDto);
    }

    /**
     * Retrieves a paginated list of transactions for a specific user with optional filtering by fuel type and start date.
     *
     * @param userId the ID of the user
     * @param page the page number to retrieve
     * @param size the number of transactions per page
     * @param fuelTypeName the name of the fuel type to filter by, can be null
     * @param startDate the start date to filter transactions, can be null
     * @return a paginated list of filtered transactions for the specified user as DTOs
     */
    public Page<TransactionDto> getFilteredUserTransactions(Long userId, int page, int size, String fuelTypeName, LocalDateTime startDate) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions;

        if (fuelTypeName != null && !fuelTypeName.isEmpty() && startDate != null) {
            transactions = transactionRepository.findByUserIdAndFuelTypeNameAndTransactionDateAfter(
                userId, fuelTypeName, startDate, pageRequest);
        } else if (fuelTypeName != null && !fuelTypeName.isEmpty()) {
            transactions = transactionRepository.findByUserIdAndFuelTypeName(userId, fuelTypeName, pageRequest);
        } else if (startDate != null) {
            transactions = transactionRepository.findByUserIdAndTransactionDateAfter(userId, startDate, pageRequest);
        } else {
            transactions = transactionRepository.findByUserId(userId, pageRequest);
        }

        return transactions.map(this::mapToDto);
    }

    /**
     * Retrieves a paginated list of transactions with optional filtering by fuel type and start date.
     *
     * @param page the page number to retrieve
     * @param size the number of transactions per page
     * @param fuelTypeName the name of the fuel type to filter by, can be null
     * @param startDate the start date to filter transactions, can be null
     * @return a paginated list of filtered transactions as DTOs
     */
    public Page<TransactionDto> getFilteredTransactions(int page, int size, String fuelTypeName, LocalDateTime startDate) {
        Pageable pageable = PageRequest.of(page, size);
        if (fuelTypeName != null && !fuelTypeName.isEmpty() && startDate != null) {
            return transactionRepository.findByFuelTypeNameAndTransactionDateAfter(fuelTypeName, startDate, pageable)
                .map(this::mapToDto);
        } else if (fuelTypeName != null && !fuelTypeName.isEmpty()) {
            return transactionRepository.findByFuelTypeName(fuelTypeName, pageable)
                .map(this::mapToDto);
        } else if (startDate != null) {
            return transactionRepository.findByTransactionDateAfter(startDate, pageable)
                .map(this::mapToDto);
        } else {
            return transactionRepository.findAll(pageable)
                .map(this::mapToDto);
        }
    }

    /**
     * Calculates the total sales volume by fuel type within a specified date range.
     *
     * @param startDate the start date of the range, can be null
     * @param endDate the end date of the range, can be null
     * @return a map of fuel type names to their total sales volume
     */
    public Map<String, BigDecimal> getSalesByFuelType(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = fetchTransactionsByDateRange(startDate, endDate);
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getFuelTypeName,
                Collectors.mapping(Transaction::getVolume, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));
    }

    /**
     * Calculates the revenue by fuel type, grouped by the specified period (daily, weekly, or monthly).
     *
     * @param startDate the start date of the range, can be null
     * @param endDate the end date of the range, can be null
     * @param period the period to group by ("daily", "weekly", or "monthly")
     * @return a nested map of fuel type names to period keys and their total revenue
     * @throws IllegalArgumentException if the period is invalid
     */
    public Map<String, Map<String, BigDecimal>> getRevenueByFuelTypeGroupedByPeriod(LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<Transaction> transactions = fetchTransactionsByDateRange(startDate, endDate);

        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getFuelTypeName,
                Collectors.groupingBy(
                    transaction -> formatTransactionDateByPeriod(transaction.getTransactionDate(), period),
                    Collectors.mapping(Transaction::getTotalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                )
            ));
    }

    /**
     * Calculates the sales volume by fuel type, grouped by the specified period (daily, weekly, or monthly).
     *
     * @param startDate the start date of the range, can be null
     * @param endDate the end date of the range, can be null
     * @param period the period to group by ("daily", "weekly", or "monthly")
     * @return a nested map of fuel type names to period keys and their total sales volume
     * @throws IllegalArgumentException if the period is invalid
     */
    public Map<String, Map<String, BigDecimal>> getSalesByFuelTypeGroupedByPeriod(LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<Transaction> transactions = fetchTransactionsByDateRange(startDate, endDate);

        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getFuelTypeName,
                Collectors.groupingBy(
                    transaction -> formatTransactionDateByPeriod(transaction.getTransactionDate(), period),
                    Collectors.mapping(Transaction::getVolume, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                )
            ));
    }

    /**
     * Calculates the total revenue, grouped by the specified period (daily, weekly, or monthly).
     *
     * @param startDate the start date of the range, can be null
     * @param endDate the end date of the range, can be null
     * @param period the period to group by ("daily", "weekly", or "monthly")
     * @return a map of period keys to their total revenue
     * @throws IllegalArgumentException if the period is invalid
     */
    public Map<String, BigDecimal> getTotalRevenueGroupedByPeriod(LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<Transaction> transactions = fetchTransactionsByDateRange(startDate, endDate);

        return transactions.stream()
            .collect(Collectors.groupingBy(
                transaction -> formatTransactionDateByPeriod(transaction.getTransactionDate(), period),
                Collectors.mapping(Transaction::getTotalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));
    }

    /**
     * Fetches transactions within the specified date range.
     * If the date range is not provided, retrieves all transactions.
     *
     * @param startDate the start date of the range, can be null
     * @param endDate the end date of the range, can be null
     * @return a list of transactions within the specified date range
     */
    private List<Transaction> fetchTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null
            ? transactionRepository.findByTransactionDateBetween(startDate, endDate)
            : transactionRepository.findAll();
    }

    /**
     * Formats a transaction date based on the specified period (daily, weekly, or monthly).
     *
     * @param date the transaction date to format
     * @param period the period to format by ("daily", "weekly", or "monthly")
     * @return the formatted date string
     * @throws IllegalArgumentException if the period is invalid
     */
    private String formatTransactionDateByPeriod(LocalDateTime date, String period) {
        return switch (period) {
            case DAILY_PERIOD -> date.format(DateTimeFormatter.ofPattern(DAILY_FORMAT));
            case WEEKLY_PERIOD -> date.format(DateTimeFormatter.ofPattern(WEEKLY_FORMAT));
            case MONTHLY_PERIOD -> date.format(DateTimeFormatter.ofPattern(MONTHLY_FORMAT));
            default -> throw new IllegalArgumentException(INVALID_PERIOD_MESSAGE + period);
        };
    }

    /**
     * Maps a {@code Transaction} entity to a {@code TransactionDto}.
     *
     * @param transaction the transaction entity to map
     * @return the mapped transaction DTO
     */
    private TransactionDto mapToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setFuelTypeId(transaction.getFuelType().getId());
        dto.setFuelTypeName(transaction.getFuelTypeName());
        dto.setPricePerLiter(transaction.getPricePerLiter());
        dto.setVolume(transaction.getVolume());
        dto.setTotalAmount(transaction.getTotalAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setUsername(transaction.getUser() != null ? transaction.getUser().getUsername() : null);
        return dto;
    }
}