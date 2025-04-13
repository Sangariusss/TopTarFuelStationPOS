package ua.toptar.toptarfuelstationpos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.toptar.toptarfuelstationpos.dto.TransactionDto;
import ua.toptar.toptarfuelstationpos.model.FuelType;
import ua.toptar.toptarfuelstationpos.model.Transaction;
import ua.toptar.toptarfuelstationpos.model.User;
import ua.toptar.toptarfuelstationpos.repository.FuelTypeRepository;
import ua.toptar.toptarfuelstationpos.repository.TransactionRepository;
import ua.toptar.toptarfuelstationpos.repository.UserRepository;
import ua.toptar.toptarfuelstationpos.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@code TransactionService} class.
 * Tests transaction creation, retrieval, and analytics functionality.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private FuelTypeRepository fuelTypeRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private FuelType a95;
    private FuelType diesel;
    private User guestUser;
    private User authenticatedUser;

    /**
     * Sets up test data before each test.
     * Initializes fuel types and users for testing.
     */
    @BeforeEach
    void setUp() {
        // Ініціалізація тестових даних
        a95 = new FuelType("A95", new BigDecimal("55.50"));
        a95.setId(1L);
        diesel = new FuelType("ДП", new BigDecimal("52.30"));
        diesel.setId(2L);

        guestUser = new User();
        guestUser.setId(1L);
        guestUser.setUsername("guest");

        authenticatedUser = new User();
        authenticatedUser.setId(2L);
        authenticatedUser.setUsername("testUser");
    }

    /**
     * Tests transaction creation with volume for an unauthenticated user.
     * Verifies that the transaction is created with the correct price and user.
     */
    @Test
    void testCreateTransactionWithVolumeUnauthenticated() {
        // Налаштування моків
        when(fuelTypeRepository.findById(1L)).thenReturn(Optional.of(a95));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guestUser));

        // Налаштування SecurityContext для неавторизованого користувача
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(a95.getId());
        dto.setVolume(new BigDecimal("20.5"));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setFuelType(a95);
        savedTransaction.setFuelTypeName("A95");
        savedTransaction.setPricePerLiter(new BigDecimal("55.50"));
        savedTransaction.setVolume(new BigDecimal("20.5"));
        savedTransaction.setTotalAmount(new BigDecimal("1137.75"));
        savedTransaction.setTransactionDate(LocalDateTime.now());
        savedTransaction.setUser(guestUser);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionDto result = transactionService.createTransaction(dto);

        assertNotNull(result.getId());
        assertEquals("A95", result.getFuelTypeName());
        assertEquals(0, new BigDecimal("55.50").compareTo(result.getPricePerLiter()));
        assertEquals(0, new BigDecimal("20.5").compareTo(result.getVolume()));
        assertEquals(0, new BigDecimal("1137.75").compareTo(result.getTotalAmount()));
        assertNotNull(result.getTransactionDate());
        assertEquals("guest", result.getUsername());
    }

    /**
     * Tests transaction creation with volume for an authenticated user.
     * Verifies that the transaction applies a discount and associates with the correct user.
     */
    @Test
    void testCreateTransactionWithVolumeAuthenticated() {
        // Налаштування моків
        when(fuelTypeRepository.findById(1L)).thenReturn(Optional.of(a95));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(authenticatedUser));

        // Налаштування SecurityContext для авторизованого користувача
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("testUser");
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(a95.getId());
        dto.setVolume(new BigDecimal("20.5"));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setFuelType(a95);
        savedTransaction.setFuelTypeName("A95");
        savedTransaction.setPricePerLiter(new BigDecimal("53.50")); // 55.50 - 2
        savedTransaction.setVolume(new BigDecimal("20.5"));
        savedTransaction.setTotalAmount(new BigDecimal("1096.75")); // 20.5 * 53.50
        savedTransaction.setTransactionDate(LocalDateTime.now());
        savedTransaction.setUser(authenticatedUser);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionDto result = transactionService.createTransaction(dto);

        assertNotNull(result.getId());
        assertEquals("A95", result.getFuelTypeName());
        assertEquals(0, new BigDecimal("53.50").compareTo(result.getPricePerLiter()));
        assertEquals(0, new BigDecimal("20.5").compareTo(result.getVolume()));
        assertEquals(0, new BigDecimal("1096.75").compareTo(result.getTotalAmount()));
        assertNotNull(result.getTransactionDate());
        assertEquals("testUser", result.getUsername());
    }

    /**
     * Tests transaction creation with total amount for an unauthenticated user.
     * Verifies that the volume is calculated correctly and the transaction is associated with the guest user.
     */
    @Test
    void testCreateTransactionWithTotalAmountUnauthenticated() {
        // Налаштування моків
        when(fuelTypeRepository.findById(2L)).thenReturn(Optional.of(diesel));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guestUser));

        // Налаштування SecurityContext для неавторизованого користувача
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(diesel.getId());
        dto.setTotalAmount(new BigDecimal("1046.00"));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setFuelType(diesel);
        savedTransaction.setFuelTypeName("Diesel");
        savedTransaction.setPricePerLiter(new BigDecimal("52.30"));
        savedTransaction.setVolume(new BigDecimal("20.00")); // 1046.00 / 52.30 = 20.00
        savedTransaction.setTotalAmount(new BigDecimal("1046.00"));
        savedTransaction.setTransactionDate(LocalDateTime.now());
        savedTransaction.setUser(guestUser);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionDto result = transactionService.createTransaction(dto);

        assertNotNull(result.getId());
        assertEquals("Diesel", result.getFuelTypeName());
        assertEquals(0, new BigDecimal("52.30").compareTo(result.getPricePerLiter()));
        assertEquals(0, new BigDecimal("20.00").compareTo(result.getVolume()));
        assertEquals(0, new BigDecimal("1046.00").compareTo(result.getTotalAmount()));
        assertNotNull(result.getTransactionDate());
        assertEquals("guest", result.getUsername());
    }

    /**
     * Tests transaction creation with total amount for an authenticated user.
     * Verifies that the volume is calculated correctly with a discount and the transaction is associated with the authenticated user.
     */
    @Test
    void testCreateTransactionWithTotalAmountAuthenticated() {
        // Налаштування моків
        when(fuelTypeRepository.findById(2L)).thenReturn(Optional.of(diesel));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(authenticatedUser));

        // Налаштування SecurityContext для авторизованого користувача
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("testUser");
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(diesel.getId());
        dto.setTotalAmount(new BigDecimal("1004.00"));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setFuelType(diesel);
        savedTransaction.setFuelTypeName("Diesel");
        savedTransaction.setPricePerLiter(new BigDecimal("50.30")); // 52.30 - 2
        savedTransaction.setVolume(new BigDecimal("19.96")); // 1004.00 / 50.30 ≈ 19.96
        savedTransaction.setTotalAmount(new BigDecimal("1004.00"));
        savedTransaction.setTransactionDate(LocalDateTime.now());
        savedTransaction.setUser(authenticatedUser);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionDto result = transactionService.createTransaction(dto);

        assertNotNull(result.getId());
        assertEquals("Diesel", result.getFuelTypeName());
        assertEquals(0, new BigDecimal("50.30").compareTo(result.getPricePerLiter()));
        assertEquals(0, new BigDecimal("19.96").compareTo(result.getVolume()));
        assertEquals(0, new BigDecimal("1004.00").compareTo(result.getTotalAmount()));
        assertNotNull(result.getTransactionDate());
        assertEquals("testUser", result.getUsername());
    }

    /**
     * Tests transaction creation with an invalid fuel type ID.
     * Verifies that an {@code IllegalArgumentException} is thrown.
     */
    @Test
    void testCreateTransactionWithInvalidFuelType() {
        when(fuelTypeRepository.findById(999L)).thenReturn(Optional.empty());

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(999L);
        dto.setVolume(new BigDecimal("10.0"));

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
    }

    /**
     * Tests transaction creation with a null fuel type ID.
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testCreateTransactionWithNullFuelTypeId() {
        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(null);
        dto.setVolume(new BigDecimal("10.0"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
        assertEquals("Fuel type ID cannot be null", exception.getMessage());
    }

    /**
     * Tests transaction creation with zero volume.
     * Verifies that an {@code IllegalArgumentException} is thrown.
     */
    @Test
    void testCreateTransactionWithZeroVolume() {
        // Налаштування моків
        when(fuelTypeRepository.findById(1L)).thenReturn(Optional.of(a95));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guestUser));

        // Налаштування SecurityContext для неавторизованого користувача
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(a95.getId());
        dto.setVolume(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
    }

    /**
     * Tests transaction creation with negative volume.
     * Verifies that an {@code IllegalArgumentException} is thrown.
     */
    @Test
    void testCreateTransactionWithNegativeVolume() {
        // Налаштування моків
        when(fuelTypeRepository.findById(1L)).thenReturn(Optional.of(a95));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guestUser));

        // Налаштування SecurityContext для неавторизованого користувача
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(a95.getId());
        dto.setVolume(new BigDecimal("-10.0"));

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
    }

    /**
     * Tests transaction creation with insufficient volume (less than 1 liter).
     * Verifies that an {@code IllegalArgumentException} is thrown.
     */
    @Test
    void testCreateTransactionWithInsufficientVolume() {
        // Налаштування моків
        when(fuelTypeRepository.findById(1L)).thenReturn(Optional.of(a95));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guestUser));

        // Налаштування SecurityContext для неавторизованого користувача
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(a95.getId());
        dto.setVolume(new BigDecimal("0.5"));

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
    }

    /**
     * Tests transaction creation with zero total amount.
     * Verifies that an {@code IllegalArgumentException} is thrown.
     */
    @Test
    void testCreateTransactionWithZeroTotalAmount() {
        // Налаштування моків
        when(fuelTypeRepository.findById(2L)).thenReturn(Optional.of(diesel));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guestUser));

        // Налаштування SecurityContext для неавторизованого користувача
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(diesel.getId());
        dto.setTotalAmount(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
    }

    /**
     * Tests transaction creation with negative total amount.
     * Verifies that an {@code IllegalArgumentException} is thrown.
     */
    @Test
    void testCreateTransactionWithNegativeTotalAmount() {
        // Налаштування моків
        when(fuelTypeRepository.findById(2L)).thenReturn(Optional.of(diesel));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guestUser));

        // Налаштування SecurityContext для неавторизованого користувача
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(diesel.getId());
        dto.setTotalAmount(new BigDecimal("-100.0"));

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
    }

    /**
     * Tests transaction creation with neither volume nor total amount provided.
     * Verifies that an {@code IllegalArgumentException} is thrown with the correct message.
     */
    @Test
    void testCreateTransactionWithNeitherVolumeNorTotalAmount() {
        // Налаштування моків
        when(fuelTypeRepository.findById(1L)).thenReturn(Optional.of(a95));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guestUser));

        // Налаштування SecurityContext для неавторизованого користувача
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        TransactionDto dto = new TransactionDto();
        dto.setFuelTypeId(a95.getId());

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto), "Either volume or totalAmount must be provided");
    }

    /**
     * Tests retrieval of all transactions.
     * Verifies that the correct number of transactions is returned with the expected details.
     */
    @Test
    void testGetAllTransactions() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setFuelType(a95);
        transaction1.setFuelTypeName("A95");
        transaction1.setPricePerLiter(new BigDecimal("55.50"));
        transaction1.setVolume(new BigDecimal("10.0"));
        transaction1.setTotalAmount(new BigDecimal("555.00"));
        transaction1.setTransactionDate(LocalDateTime.now());
        transaction1.setUser(guestUser);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setFuelType(diesel);
        transaction2.setFuelTypeName("Diesel");
        transaction2.setPricePerLiter(new BigDecimal("52.30"));
        transaction2.setVolume(new BigDecimal("15.0"));
        transaction2.setTotalAmount(new BigDecimal("784.50"));
        transaction2.setTransactionDate(LocalDateTime.now());
        transaction2.setUser(guestUser);

        when(transactionRepository.findAll(any(PageRequest.class)))
            .thenReturn(new PageImpl<>(Arrays.asList(transaction1, transaction2)));

        Page<TransactionDto> transactions = transactionService.getAllTransactions(0, 10);

        assertEquals(2, transactions.getTotalElements());
        assertEquals(1, transactions.getTotalPages());
        assertEquals("A95", transactions.getContent().get(0).getFuelTypeName());
        assertEquals("Diesel", transactions.getContent().get(1).getFuelTypeName());
        assertEquals(0, new BigDecimal("555.00").compareTo(transactions.getContent().get(0).getTotalAmount()));
        assertEquals(0, new BigDecimal("784.50").compareTo(transactions.getContent().get(1).getTotalAmount()));
        assertEquals("guest", transactions.getContent().get(0).getUsername());
        assertEquals("guest", transactions.getContent().get(1).getUsername());
    }

    /**
     * Tests retrieval of recent transactions.
     * Verifies that the correct number of transactions is returned in ascending order by date.
     */
    @Test
    void testGetRecentTransactions() {
        Transaction[] transactions = new Transaction[6];
        for (int i = 0; i < 6; i++) {
            transactions[i] = new Transaction();
            transactions[i].setId((long) (i + 1));
            transactions[i].setFuelType(a95);
            transactions[i].setFuelTypeName("A95");
            transactions[i].setPricePerLiter(new BigDecimal("55.50"));
            transactions[i].setVolume(new BigDecimal((i + 1) + ".0")); // 1, 2, 3, 4, 5, 6
            transactions[i].setTotalAmount(BigDecimal.valueOf((i + 1) * 55.50));
            transactions[i].setTransactionDate(LocalDateTime.now().minusMinutes(6 - i)); // Старіші -> новіші
            transactions[i].setUser(guestUser);
        }

        PageRequest pageable = PageRequest.of(0, 5, Sort.by("transactionDate").ascending());
        when(transactionRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(Arrays.asList(transactions[0], transactions[1], transactions[2], transactions[3], transactions[4])));

        Page<TransactionDto> recentTransactions = transactionService.getRecentTransactions();

        assertEquals(5, recentTransactions.getContent().size());
        assertEquals(0, new BigDecimal("1.0").compareTo(recentTransactions.getContent().get(0).getVolume()));
        assertEquals(0, new BigDecimal("5.0").compareTo(recentTransactions.getContent().get(4).getVolume()));
    }

    /**
     * Tests retrieval of transactions for a specific user.
     * Verifies that the correct transactions are returned for the user.
     */
    @Test
    void testGetUserTransactions() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setFuelType(a95);
        transaction1.setFuelTypeName("A95");
        transaction1.setPricePerLiter(new BigDecimal("55.50"));
        transaction1.setVolume(new BigDecimal("10.0"));
        transaction1.setTotalAmount(new BigDecimal("555.00"));
        transaction1.setTransactionDate(LocalDateTime.now());
        transaction1.setUser(authenticatedUser);

        PageRequest pageable = PageRequest.of(0, 10);
        when(transactionRepository.findByUserId(2L, pageable))
            .thenReturn(new PageImpl<>(List.of(transaction1)));

        Page<TransactionDto> transactions = transactionService.getUserTransactions(2L, 0, 10);

        assertEquals(1, transactions.getTotalElements());
        assertEquals("A95", transactions.getContent().get(0).getFuelTypeName());
        assertEquals(0, new BigDecimal("555.00").compareTo(transactions.getContent().get(0).getTotalAmount()));
        assertEquals("testUser", transactions.getContent().get(0).getUsername());
    }

    /**
     * Tests retrieval of transactions filtered by fuel type and date.
     * Verifies that the correct transactions are returned based on the filter criteria.
     */
    @Test
    void testGetFilteredTransactionsByFuelTypeAndDate() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setFuelType(a95);
        transaction1.setFuelTypeName("A95");
        transaction1.setPricePerLiter(new BigDecimal("55.50"));
        transaction1.setVolume(new BigDecimal("10.0"));
        transaction1.setTotalAmount(new BigDecimal("555.00"));
        transaction1.setTransactionDate(LocalDateTime.now());
        transaction1.setUser(guestUser);

        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        PageRequest pageable = PageRequest.of(0, 10);
        when(transactionRepository.findByFuelTypeNameAndTransactionDateAfter("A95", startDate, pageable))
            .thenReturn(new PageImpl<>(List.of(transaction1)));

        Page<TransactionDto> transactions = transactionService.getFilteredTransactions(0, 10, "A95", startDate);

        assertEquals(1, transactions.getTotalElements());
        assertEquals("A95", transactions.getContent().get(0).getFuelTypeName());
        assertEquals(0, new BigDecimal("555.00").compareTo(transactions.getContent().get(0).getTotalAmount()));
    }

    /**
     * Tests calculation of sales by fuel type.
     * Verifies that the total sales volume is correctly calculated for each fuel type.
     */
    @Test
    void testGetSalesByFuelType() {
        Transaction transaction1 = new Transaction();
        transaction1.setFuelType(a95);
        transaction1.setFuelTypeName("A95");
        transaction1.setVolume(new BigDecimal("10.0"));

        Transaction transaction2 = new Transaction();
        transaction2.setFuelType(diesel);
        transaction2.setFuelTypeName("Diesel");
        transaction2.setVolume(new BigDecimal("15.0"));

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        Map<String, BigDecimal> sales = transactionService.getSalesByFuelType(null, null);

        assertEquals(2, sales.size());
        assertEquals(0, new BigDecimal("10.0").compareTo(sales.get("A95")));
        assertEquals(0, new BigDecimal("15.0").compareTo(sales.get("Diesel")));
    }

    /**
     * Tests calculation of revenue by fuel type grouped by period.
     * Verifies that the revenue is correctly calculated and grouped by fuel type and period.
     */
    @Test
    void testGetRevenueByFuelTypeGroupedByPeriod() {
        Transaction transaction1 = new Transaction();
        transaction1.setFuelType(a95);
        transaction1.setFuelTypeName("A95");
        transaction1.setTotalAmount(new BigDecimal("555.00"));
        transaction1.setTransactionDate(LocalDateTime.of(2025, 4, 1, 12, 0));

        Transaction transaction2 = new Transaction();
        transaction2.setFuelType(diesel);
        transaction2.setFuelTypeName("Diesel");
        transaction2.setTotalAmount(new BigDecimal("784.50"));
        transaction2.setTransactionDate(LocalDateTime.of(2025, 4, 1, 12, 0));

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        Map<String, Map<String, BigDecimal>> revenue = transactionService.getRevenueByFuelTypeGroupedByPeriod(null, null, "daily");

        assertEquals(2, revenue.size());
        assertEquals(0, new BigDecimal("555.00").compareTo(revenue.get("A95").get("2025-04-01")));
        assertEquals(0, new BigDecimal("784.50").compareTo(revenue.get("Diesel").get("2025-04-01")));
    }

    /**
     * Tests calculation of sales by fuel type grouped by period.
     * Verifies that the sales volume is correctly calculated and grouped by fuel type and period.
     */
    @Test
    void testGetSalesByFuelTypeGroupedByPeriod() {
        Transaction transaction1 = new Transaction();
        transaction1.setFuelType(a95);
        transaction1.setFuelTypeName("A95");
        transaction1.setVolume(new BigDecimal("10.0"));
        transaction1.setTransactionDate(LocalDateTime.of(2025, 4, 1, 12, 0));

        Transaction transaction2 = new Transaction();
        transaction2.setFuelType(diesel);
        transaction2.setFuelTypeName("Diesel");
        transaction2.setVolume(new BigDecimal("15.0"));
        transaction2.setTransactionDate(LocalDateTime.of(2025, 4, 1, 12, 0));

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        Map<String, Map<String, BigDecimal>> sales = transactionService.getSalesByFuelTypeGroupedByPeriod(null, null, "daily");

        assertEquals(2, sales.size());
        assertEquals(0, new BigDecimal("10.0").compareTo(sales.get("A95").get("2025-04-01")));
        assertEquals(0, new BigDecimal("15.0").compareTo(sales.get("Diesel").get("2025-04-01")));
    }

    /**
     * Tests calculation of total revenue grouped by period.
     * Verifies that the total revenue is correctly calculated and grouped by period.
     */
    @Test
    void testGetTotalRevenueGroupedByPeriod() {
        Transaction transaction1 = new Transaction();
        transaction1.setTotalAmount(new BigDecimal("555.00"));
        transaction1.setTransactionDate(LocalDateTime.of(2025, 4, 1, 12, 0));

        Transaction transaction2 = new Transaction();
        transaction2.setTotalAmount(new BigDecimal("784.50"));
        transaction2.setTransactionDate(LocalDateTime.of(2025, 4, 1, 12, 0));

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        Map<String, BigDecimal> revenue = transactionService.getTotalRevenueGroupedByPeriod(null, null, "daily");

        assertEquals(1, revenue.size());
        assertEquals(0, new BigDecimal("1339.50").compareTo(revenue.get("2025-04-01")));
    }
}