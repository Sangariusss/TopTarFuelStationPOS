package ua.toptar.toptarfuelstationpos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.toptar.toptarfuelstationpos.service.UserService;

/**
 * Controller for handling user registration requests.
 * Provides endpoints for displaying the registration page and processing registration submissions.
 */
@Controller
public class RegisterController {

    private final UserService userService;

    /**
     * Constructs a new {@code RegisterController} with the specified user service.
     *
     * @param userService the service for managing user operations
     */
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the registration page.
     *
     * @param model the model to add attributes for the view
     * @return the name of the registration view template
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        return "register";
    }

    /**
     * Processes a user registration request.
     * Registers a new user with the "USER" role and redirects to the login page on success.
     * Displays an error message on the registration page if the username already exists.
     *
     * @param username the username for the new user
     * @param password the password for the new user
     * @param model the model to add attributes for the view
     * @return the name of the view template to render (either "login" or "register")
     */
    @PostMapping("/register")
    public String register(@RequestParam String username,
        @RequestParam String password,
        Model model) {
        try {
            userService.registerUser(username, password, "USER");
            model.addAttribute("message", "Реєстрація успішна! Увійдіть за допомогою нового логіну.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Помилка: користувач із таким логіном уже існує!");
            return "register";
        }
    }
}