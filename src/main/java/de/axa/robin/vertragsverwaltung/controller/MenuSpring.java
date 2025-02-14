package de.axa.robin.vertragsverwaltung.controller;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class for handling menu-related web requests.
 */
@Controller
@Getter
@Setter
public class MenuSpring {
    private static final Logger logger = LoggerFactory.getLogger(MenuSpring.class);
    private int vsnr;

    /**
     * Handles the root URL ("/") and returns the index page.
     *
     * @return the name of the index page
     */
    @GetMapping("/")
    public String startWebsite() {
        logger.info("Accessing index page");
        return "index";
    }

    /**
     * Handles the "/home" URL and returns the home page.
     *
     * @return the name of the home page
     */
    @GetMapping("/home")
    public String home() {
        logger.info("Accessing home page");
        return "home";
    }
}