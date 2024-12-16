package de.axa.robin.vertragsverwaltung.frontend.user_interaction;

import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrintVertrage.class)
class PrintVertrageTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Vertragsverwaltung vertragsverwaltung;

    private List<Vertrag> vertrage;

    @BeforeEach
    void setUp() {
        Fahrzeug fahrzeug1 = new Fahrzeug("ABC123", "BMW", "X5", 240, 1234);
        Fahrzeug fahrzeug2 = new Fahrzeug("XYZ789", "Audi", "A4", 200, 5678);

        Partner partner1 = new Partner("Max", "Mustermann", 'M', LocalDate.of(1980, 1, 1), "Deutschland", "Musterstraße", "1", "12345", "Musterstadt", "NRW");
        Partner partner2 = new Partner("Erika", "Mustermann", 'F', LocalDate.of(1985, 5, 15), "Deutschland", "Beispielstraße", "2", "54321", "Beispielstadt", "NRW");

        Vertrag vertrag1 = new Vertrag(12345, true, 299.99, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), LocalDate.of(2022, 12, 1), fahrzeug1, partner1);
        Vertrag vertrag2 = new Vertrag(67890, false, 199.99, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), LocalDate.of(2022, 12, 1), fahrzeug2, partner2);

        vertrage = Arrays.asList(vertrag1, vertrag2);

        given(vertragsverwaltung.getVertrage()).willReturn(vertrage);
    }

    @Test
    void showAll() throws Exception {
        // Debug statement to check the returned contracts

        mockMvc.perform(get("/printVertrage"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("299,99")))
                .andExpect(content().string(containsString("199,99")))
                .andExpect(content().string(containsString("ABC123")))
                .andExpect(content().string(containsString("XYZ789")))
                .andExpect(content().string(containsString("Max")))
                .andExpect(content().string(containsString("Erika")));
    }
}