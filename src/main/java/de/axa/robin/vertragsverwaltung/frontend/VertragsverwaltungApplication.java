package de.axa.robin.vertragsverwaltung.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.axa.robin.vertragsverwaltung")
public class VertragsverwaltungApplication {
    public static void main(String[] args) {
        SpringApplication.run(VertragsverwaltungApplication.class, args);
    }
}