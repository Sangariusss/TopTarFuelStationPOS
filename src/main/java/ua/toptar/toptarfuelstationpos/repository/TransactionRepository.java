package ua.toptar.toptarfuelstationpos.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.toptar.toptarfuelstationpos.model.Transaction;

/**
 * Repository interface for managing {@code Transaction} entities.
 * Provides CRUD operations and custom query methods for transactions.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds transactions for a specific user with pagination.
     *
     * @param userId the ID of the user
     * @param pageable the pagination information
     * @return a paginated list of transactions for the specified user
     */
    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    /**
     * Finds transactions for a specific user and fuel type with pagination.
     *
     * @param userId the ID of the user
     * @param fuelTypeName the name of the fuel type
     * @param pageable the pagination information
     * @return a paginated list of transactions matching the criteria
     */
    Page<Transaction> findByUserIdAndFuelTypeName(Long userId, String fuelTypeName, Pageable pageable);

    /**
     * Finds transactions for a specific user after a given start date with pagination.
     *
     * @param userId the ID of the user
     * @param startDate the start date for filtering transactions
     * @param pageable the pagination information
     * @return a paginated list of transactions after the specified date
     */
    Page<Transaction> findByUserIdAndTransactionDateAfter(Long userId, LocalDateTime startDate, Pageable pageable);

    /**
     * Finds transactions for a specific user, fuel type, and after a given start date with pagination.
     *
     * @param userId the ID of the user
     * @param fuelTypeName the name of the fuel type
     * @param startDate the start date for filtering transactions
     * @param pageable the pagination information
     * @return a paginated list of transactions matching the criteria
     */
    Page<Transaction> findByUserIdAndFuelTypeNameAndTransactionDateAfter(
        Long userId, String fuelTypeName, LocalDateTime startDate, Pageable pageable);

    /**
     * Finds transactions for a specific fuel type and after a given start date with pagination.
     *
     * @param fuelTypeName the name of the fuel type
     * @param startDate the start date for filtering transactions
     * @param pageable the pagination information
     * @return a paginated list of transactions matching the criteria
     */
    Page<Transaction> findByFuelTypeNameAndTransactionDateAfter(String fuelTypeName, LocalDateTime startDate, Pageable pageable);

    /**
     * Finds transactions for a specific fuel type with pagination.
     *
     * @param fuelTypeName the name of the fuel type
     * @param pageable the pagination information
     * @return a paginated list of transactions for the specified fuel type
     */
    Page<Transaction> findByFuelTypeName(String fuelTypeName, Pageable pageable);

    /**
     * Finds transactions after a given start date with pagination.
     *
     * @param startDate the start date for filtering transactions
     * @param pageable the pagination information
     * @return a paginated list of transactions after the specified date
     */
    Page<Transaction> findByTransactionDateAfter(LocalDateTime startDate, Pageable pageable);

    /**
     * Finds transactions within a specified date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return a list of transactions within the specified date range
     */
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}