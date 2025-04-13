package ua.toptar.toptarfuelstationpos.controller;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.toptar.toptarfuelstationpos.dto.TransactionDto;
import ua.toptar.toptarfuelstationpos.model.User;
import ua.toptar.toptarfuelstationpos.repository.FuelTypeRepository;
import ua.toptar.toptarfuelstationpos.repository.UserRepository;
import ua.toptar.toptarfuelstationpos.service.TransactionService;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Controller for handling user-related requests.
 * Provides endpoints for authenticated users to view their transactions.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final FuelTypeRepository fuelTypeRepository;

    /**
     * Constructs a new {@code UserController} with the specified dependencies.
     *
     * @param transactionService the service for managing transactions
     * @param userRepository the repository for accessing user data
     * @param fuelTypeRepository the repository for accessing fuel type data
     */
    public UserController(TransactionService transactionService, UserRepository userRepository, FuelTypeRepository fuelTypeRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.fuelTypeRepository = fuelTypeRepository;
    }

    /**
     * Displays a paginated list of transactions for the authenticated user with optional filtering.
     * Redirects to the login page if the user is not authenticated.
     *
     * @param userDetails the authenticated user's details
     * @param page the page number to display, defaults to 0
     * @param fuelTypeName the name of the fuel type to filter by, optional
     * @param startDate the start date to filter transactions, optional
     * @param model the model to add attributes for the view
     * @return the name of the user transactions view template, or a redirect to the login page
     * @throws IllegalStateException if the authenticated user is not found in the repository
     */
    @GetMapping("/transactions")
    public String getUserTransactions(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String fuelTypeName,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        Model model) {
        if (userDetails == null) {
            logger.severe("UserDetails is null, redirecting to login");
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        logger.info(() -> String.format("Authenticated user: %s", username));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        int pageSize = 10;
        logger.info(() -> String.format("Fetching transactions for user ID: %d, page: %d, fuelTypeName: %s, startDate: %s",
            user.getId(), page, fuelTypeName, startDate));
        Page<TransactionDto> transactions = transactionService.getFilteredUserTransactions(
            user.getId(), page, pageSize, fuelTypeName, startDate);

        model.addAttribute("transactions", transactions.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactions.getTotalPages());
        model.addAttribute("hasPrevious", transactions.hasPrevious());
        model.addAttribute("hasNext", transactions.hasNext());
        model.addAttribute("fuelTypes", fuelTypeRepository.findAll());
        model.addAttribute("fuelTypeName", fuelTypeName);
        model.addAttribute("startDate", startDate);

        logger.info("Found " + transactions.getTotalElements() + " transactions for user ID: " + user.getId());
        return "user-transactions";
    }
}