package ua.toptar.toptarfuelstationpos.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.toptar.toptarfuelstationpos.dto.TransactionDto;
import ua.toptar.toptarfuelstationpos.model.User;
import ua.toptar.toptarfuelstationpos.service.TransactionService;

/**
 * REST controller for handling transaction-related API requests.
 * Provides endpoints for creating transactions and retrieving user-specific transactions.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Constructs a new {@code TransactionController} with the specified transaction service.
     *
     * @param transactionService the service for managing transactions
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Creates a new transaction based on the provided transaction data.
     *
     * @param transactionDto the transaction data to create
     * @return a {@code ResponseEntity} containing the created transaction
     */
    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {
        TransactionDto createdTransaction = transactionService.createTransaction(transactionDto);
        return ResponseEntity.ok(createdTransaction);
    }

    /**
     * Retrieves a paginated list of transactions for the authenticated user.
     *
     * @param user the authenticated user
     * @param page the page number to retrieve, defaults to 0
     * @return a {@code ResponseEntity} containing the paginated list of transactions
     */
    @GetMapping("/user")
    public ResponseEntity<Page<TransactionDto>> getUserTransactions(
        @AuthenticationPrincipal User user,
        @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
        Page<TransactionDto> transactions = transactionService.getUserTransactions(user.getId(), page, pageSize);
        return ResponseEntity.ok(transactions);
    }
}