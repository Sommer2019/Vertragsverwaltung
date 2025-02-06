package de.axa.robin.vertragsverwaltung;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.axa.robin.vertragsverwaltung") //ToDO: recreate tests for frontend.storage
//ToDo: Kommentare
public class VertragsverwaltungApplication {
    public static void main(String[] args) {
        SpringApplication.run(VertragsverwaltungApplication.class, args);
    }
}