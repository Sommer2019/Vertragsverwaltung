package de.axa.robin.vertragsverwaltung.user_interaction;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class for handling menu-related web requests.
 */
@Controller
@Getter
@Setter
public class MenuSpring {
    private int vsnr;

    /**
     * Handles the root URL ("/") and returns the index page.
     *
     * @return the name of the index page
     */
    @GetMapping("/")
    public String startWebsite() {
        return "index";
    }

    /**
     * Handles the "/home" URL and returns the home page.
     *
     * @return the name of the home page
     */
    @GetMapping("/home")
    public String home() {
        return "home";
    }
}