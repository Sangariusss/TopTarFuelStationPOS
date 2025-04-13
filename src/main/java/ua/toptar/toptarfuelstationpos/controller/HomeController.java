package ua.toptar.toptarfuelstationpos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling requests to the root URL.
 * Redirects users to the POS page.
 */
@Controller
public class HomeController {

    /**
     * Redirects requests from the root URL to the POS page.
     *
     * @return a redirect instruction to the "/pos" endpoint
     */
    @GetMapping("/")
    public String redirectToPos() {
        return "redirect:/pos";
    }
}