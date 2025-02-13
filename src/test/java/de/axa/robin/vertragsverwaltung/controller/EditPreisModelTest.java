package de.axa.robin.vertragsverwaltung.controller;

import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.services.PreisModelService;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EditPreisModel.class)
public class EditPreisModelTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PreisModelService preisModelService;

    @MockitoBean
    private VertragsService vertragsService;

    /**
     * Testet den GET-Endpunkt "/editPreis" und überprüft, ob das aktuelle Preismodell
     * korrekt in das Model eingefügt und die View "editPreis" zurückgegeben wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testEditPreisGet() throws Exception {
        Preis preismodell = new Preis();
        preismodell.setFaktor(1.0);
        preismodell.setAge(1.0);
        preismodell.setSpeed(1.0);
        Mockito.when(preisModelService.getPreismodell()).thenReturn(preismodell);

        mockMvc.perform(get("/editPreis")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("preismodell"))
                .andExpect(view().name("editPreis"));
    }

    /**
     * Testet den POST-Endpunkt "/precalcPreis". Es wird simuliert, dass die Preisberechnung
     * einen bestimmten Wert zurückliefert. Anschließend wird überprüft, ob die JSON-Antwort
     * den erwarteten Preis enthält.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testPrecalcPreisPost() throws Exception {
        // Testdaten erstellen
        Preis preismodell = new Preis();
        preismodell.setFaktor(1.2);
        preismodell.setAge(0.8);
        preismodell.setSpeed(1.5);

        BigDecimal calculatedPrice = new BigDecimal(250);
        List<Vertrag> vertrage = new ArrayList<>();
        Mockito.when(vertragsService.getVertrage()).thenReturn(vertrage);
        Mockito.when(preisModelService.updatePreisAndModell(
                        ArgumentMatchers.any(Preis.class),
                        ArgumentMatchers.eq(true),
                        ArgumentMatchers.eq(vertrage)))
                .thenReturn(calculatedPrice);

        mockMvc.perform(post("/precalcPreis")
                        .param("faktor", "1.2")
                        .param("age", "0.8")
                        .param("speed", "1.5")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", containsString(calculatedPrice + " €")));
    }

    /**
     * Testet den POST-Endpunkt "/editPreis". Es wird simuliert, dass die Aktualisierung des
     * Preismodells einen bestimmten Gesamtbetrag zurückliefert. Anschließend wird überprüft,
     * ob die Bestätigungsnachricht korrekt im Model abgelegt wird und die View "home" zurückgegeben wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testEditPreisPost() throws Exception {
        Preis preismodell = new Preis();
        preismodell.setFaktor(1.5);
        preismodell.setAge(0.9);
        preismodell.setSpeed(1.3);

        BigDecimal updatedTotal = new BigDecimal(250);
        List<Vertrag> vertrage = new ArrayList<>();
        Mockito.when(vertragsService.getVertrage()).thenReturn(vertrage);
        Mockito.when(preisModelService.updatePreisAndModell(
                        ArgumentMatchers.any(Preis.class),
                        ArgumentMatchers.eq(false),
                        ArgumentMatchers.eq(vertrage)))
                .thenReturn(updatedTotal);

        mockMvc.perform(post("/editPreis")
                        .param("faktor", "1.5")
                        .param("age", "0.9")
                        .param("speed", "1.3")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("confirm"))
                .andExpect(model().attribute("confirm", containsString("Preise erfolgreich angepasst. Neue Einnahmensumme: " + updatedTotal + "€.")))
                .andExpect(view().name("home"));
    }
}
