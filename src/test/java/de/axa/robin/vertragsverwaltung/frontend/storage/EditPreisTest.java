package de.axa.robin.vertragsverwaltung.frontend.storage;

import de.axa.robin.vertragsverwaltung.backend.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Edit;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EditPreis.class)
@Import(CustomTestConfig.class)
public class EditPreisTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private Repository repository;

    @Mock
    private Edit edit;


    /**
     * Testet den GET-Request auf "/editPreis".
     * Es wird geprüft, ob das Model ein 'preismodell' mit den aus dem Repository geladenen
     * Faktoren enthält und der View-Name "editPreis" zurückgegeben wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetEditPreis() throws Exception {
        // Arrange: Erstelle ein Dummy-JsonObject mit den Faktoren
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.1)
                .add("factorage", 2.2)
                .add("factorspeed", 3.3)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        // Act & Assert
        mockMvc.perform(get("/editPreis"))
                .andExpect(status().isOk())
                .andExpect(view().name("editPreis"))
                .andExpect(model().attributeExists("preismodell"))
                .andExpect(model().attribute("preismodell", hasProperty("faktor", is(1.1))))
                .andExpect(model().attribute("preismodell", hasProperty("age", is(2.2))))
                .andExpect(model().attribute("preismodell", hasProperty("speed", is(3.3))));
    }

    /**
     * Testet den POST-Request auf "/precalcPreis".
     * Es wird simuliert, dass für die übermittelten Parameter ein Preis berechnet wird.
     * Anschließend wird geprüft, ob der JSON-Response den formatierten Preis (z. B. "100,25 €") enthält.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testPostPrecalcPreis() throws Exception {
        // Arrange: Simuliere die Rückgabe eines JsonObject mit Faktoren
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.1)
                .add("factorage", 2.2)
                .add("factorspeed", 3.3)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        // Simuliere, dass vertragsverwaltung.getVertrage() eine leere Liste zurückgibt
        when(vertragsverwaltung.getVertrage()).thenReturn(Collections.emptyList());

        // Simuliere den Aufruf von edit.recalcPrice für die vom Client übermittelten Werte.
        // Angenommen, für (faktor=2.5, age=3.5, speed=4.5) wird 100.25 zurückgegeben.
        BigDecimal calculatedPrice = BigDecimal.valueOf(100.25);
        when(edit.recalcPrice(2.5, 3.5, 4.5, Collections.emptyList())).thenReturn(calculatedPrice);
        // Auch der Aufruf mit den aus dem Repository geladenen Faktoren wird simuliert.
        when(edit.recalcPrice(1.1, 2.2, 3.3, Collections.emptyList())).thenReturn(calculatedPrice);

        // Act & Assert: Prüfe, ob der JSON-Response den korrekten Preis enthält.
        mockMvc.perform(post("/precalcPreis")
                        .param("faktor", "2.5")
                        .param("age", "3.5")
                        .param("speed", "4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is("100,25 €")));
    }

    /**
     * Testet den POST-Request auf "/editPreis".
     * Hier wird ein Preis berechnet und anschließend der View "home" mit einer Bestätigungsmeldung
     * zurückgegeben. Die Meldung soll den formatierten Preis (z. B. "200,50") enthalten.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testPostEditPreis() throws Exception {
        // Arrange: Simuliere, dass vertragsverwaltung.getVertrage() eine leere Liste zurückgibt.
        when(vertragsverwaltung.getVertrage()).thenReturn(Collections.emptyList());
        // Simuliere den Rückgabewert von edit.recalcPrice für die übermittelten Werte.
        BigDecimal calculatedPrice = BigDecimal.valueOf(200.50);
        when(edit.recalcPrice(2.5, 3.5, 4.5, Collections.emptyList())).thenReturn(calculatedPrice);

        // Act & Assert: Der View-Name "home" und eine Confirm-Meldung, die "200,50" enthält, werden erwartet.
        mockMvc.perform(post("/editPreis")
                        .param("faktor", "2.5")
                        .param("age", "3.5")
                        .param("speed", "4.5"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("confirm"))
                .andExpect(model().attribute("confirm", containsString("Preise erfolgreich aktualisiert!")))
                .andExpect(model().attribute("confirm", containsString("200,50")));
    }
}
