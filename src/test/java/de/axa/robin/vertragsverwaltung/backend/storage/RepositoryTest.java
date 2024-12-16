package de.axa.robin.vertragsverwaltung.backend.storage;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    private Repository repository;
    private Setup setup;
    private List<Vertrag> vertrage;
    private File file;

    @BeforeEach
    void setUp() {
        setup = Mockito.mock(Setup.class);
        repository = new Repository(setup);
        vertrage = new ArrayList<>();
        vertrage.add(new Vertrag(12345, true, 299.99, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), LocalDate.of(2022, 12, 1),
                new Fahrzeug("ABC123", "BMW", "X5", 240, 1234),
                new Partner("Max", "Mustermann", 'M', LocalDate.of(1980, 1, 1), "Deutschland", "Musterstraße", "11", String.valueOf(51469), "Musterstadt", "NRW")));
        file = new File("src/main/resources/vertragetest.json");
    }

    @AfterEach
    void tearDown() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testSpeichereUndLadeVertrage() {

    }
}