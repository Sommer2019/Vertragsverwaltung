package de.axa.robin.vertragsverwaltung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * Main class for the Vertragsverwaltung application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = "de.axa.robin.vertragsverwaltung")
public class VertragsverwaltungApplication {
    private static final Logger logger = LoggerFactory.getLogger(VertragsverwaltungApplication.class);
    /**
     * The main method that serves as the entry point for the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(VertragsverwaltungApplication.class, args);
        logger.info("Vertragsverwaltung application started successfully.");
    }
}