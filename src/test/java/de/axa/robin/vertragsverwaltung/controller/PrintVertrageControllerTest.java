package de.axa.robin.vertragsverwaltung.controller;

import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PrintVertrageControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private PrintVertrageController printVertrageController;

    @MockitoBean
    private VertragsService vertragsService;

    @BeforeEach
    void setUp() {
        // Aufbau des MockMvc-Objekts mit dem WebApplicationContext
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Erzeuge Testdaten: Zwei Fahrzeuge und zwei Partner
        Fahrzeug fahrzeug1 = new Fahrzeug("ABC123", "BMW", "X5", 240, 1234);
        Fahrzeug fahrzeug2 = new Fahrzeug("XYZ789", "Audi", "A4", 200, 5678);

        Partner partner1 = new Partner("Max", "Mustermann", 'M', LocalDate.of(1980, 1, 1),
                "Deutschland", "Musterstraße", "1", "12345", "Musterstadt", "NRW");
        List<Vertrag> vertrage = getVertrags(fahrzeug1, partner1, fahrzeug2);

        // Mocking: VertragsService liefert die Testverträge zurück
        given(vertragsService.getVertrage()).willReturn(vertrage);
    }

    private static List<Vertrag> getVertrags(Fahrzeug fahrzeug1, Partner partner1, Fahrzeug fahrzeug2) {
        Partner partner2 = new Partner("Erika", "Mustermann", 'W', LocalDate.of(1985, 5, 15),
                "Deutschland", "Beispielstraße", "2", "54321", "Beispielstadt", "NRW");

        Vertrag vertrag1 = new Vertrag(12345, true, 299.99, LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 1, 1), LocalDate.of(2022, 12, 1), fahrzeug1, partner1);
        Vertrag vertrag2 = new Vertrag(67890, false, 199.99, LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 1, 1), LocalDate.of(2022, 12, 1), fahrzeug2, partner2);

        return Arrays.asList(vertrag1, vertrag2);
    }

    /**
     * Testet den GET-Endpunkt "/printVertrage", der eine Übersicht aller Verträge zurückliefert.
     */
    @Test
    void showAll() throws Exception {
        mockMvc.perform(get("/printVertrage"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("299,99")))
                .andExpect(content().string(containsString("199,99")))
                .andExpect(content().string(containsString("ABC123")))
                .andExpect(content().string(containsString("XYZ789")))
                .andExpect(content().string(containsString("Max")))
                .andExpect(content().string(containsString("Erika")));
    }

    /**
     * Testet die Berechnung der Gesamtsumme aller Preise.
     * Dabei wird berücksichtigt, dass Verträge mit monatlicher Abrechnung mit 12 multipliziert werden.
     */
    @Test
    void testComputeTotalPriceWrapper() {
        // Arrange: Erzeuge zwei Verträge
        Vertrag vertrag1 = new Vertrag();
        vertrag1.setPreis(15.0);
        vertrag1.setMonatlich(true); // 15.0 * 12 = 180.0

        Vertrag vertrag2 = new Vertrag();
        vertrag2.setPreis(25.0);
        vertrag2.setMonatlich(false); // 25.0

        List<Vertrag> vertrage = Arrays.asList(vertrag1, vertrag2);

        // Act: Gesamtsumme berechnen
        BigDecimal totalPrice = printVertrageController.calculateTotalPrice(vertrage);

        // Assert: Erwartete Gesamtsumme 180.0 + 25.0 = 205.0
        BigDecimal expected = BigDecimal.valueOf(205.0);
        assertEquals(0, expected.compareTo(totalPrice), "Die Gesamtsumme sollte 205.0 betragen.");
    }
}
