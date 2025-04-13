package ua.toptar.toptarfuelstationpos.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.toptar.toptarfuelstationpos.dto.TransactionDto;
import ua.toptar.toptarfuelstationpos.repository.FuelTypeRepository;
import ua.toptar.toptarfuelstationpos.service.TransactionService;

/**
 * Controller for handling Point of Sale (POS) related requests.
 * Manages the POS page display and transaction creation.
 */
@Controller
@RequestMapping("/pos")
public class PosController {

    private final TransactionService transactionService;
    private final FuelTypeRepository fuelTypeRepository;

    /**
     * Constructs a new {@code PosController} with the specified dependencies.
     *
     * @param transactionService the service for managing transactions
     * @param fuelTypeRepository the repository for accessing fuel type data
     */
    public PosController(TransactionService transactionService, FuelTypeRepository fuelTypeRepository) {
        this.transactionService = transactionService;
        this.fuelTypeRepository = fuelTypeRepository;
    }

    /**
     * Displays the POS page with available fuel types and user authentication status.
     * Includes a form for creating a new transaction.
     *
     * @param model the model to add attributes for the view
     * @return the name of the POS view template
     */
    @GetMapping
    public String showPosPage(Model model) {
        addAuthenticationAttributes(model);

        model.addAttribute("transaction", new TransactionDto());
        model.addAttribute("fuelTypes", fuelTypeRepository.findAll());
        return "index";
    }

    /**
     * Processes a transaction creation request from the POS page.
     * Displays a success message on successful transaction creation or an error message if validation fails.
     *
     * @param transactionDto the transaction data submitted from the form
     * @param model the model to add attributes for the view
     * @return the name of the POS view template
     */
    @PostMapping("/transaction")
    public String createTransaction(@ModelAttribute("transaction") TransactionDto transactionDto, Model model) {
        addAuthenticationAttributes(model);

        try {
            transactionService.createTransaction(transactionDto);
            model.addAttribute("message", "Транзакцію успішно завершено!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("transaction", new TransactionDto());
        model.addAttribute("fuelTypes", fuelTypeRepository.findAll());
        return "index";
    }

    /**
     * Adds authentication-related attributes to the model.
     * Sets the "isAuthenticated" flag and the "username" if the user is authenticated.
     *
     * @param model the model to add attributes to
     */
    private void addAuthenticationAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getPrincipal());

        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated) {
            model.addAttribute("username", authentication.getName());
        }
    }
}