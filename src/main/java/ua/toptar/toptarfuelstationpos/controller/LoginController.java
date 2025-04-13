package ua.toptar.toptarfuelstationpos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling login-related requests.
 * Provides access to the login page.
 */
@Controller
public class LoginController {

    /**
     * Displays the login page.
     *
     * @return the name of the login view template
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}