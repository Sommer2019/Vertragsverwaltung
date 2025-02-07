package de.axa.robin.vertragsverwaltung.frontend.storage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.axa.robin.vertragsverwaltung.backend.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Create;
import de.axa.robin.vertragsverwaltung.backend.storage.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.frontend.user_interaction.MenuSpring;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@WebMvcTest(HandleVertrag.class)
@ExtendWith(SpringExtension.class)
@Import(CustomTestConfig.class)
public class HandleVertragTest {

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private MenuSpring menuSpring;

    @Mock
    private Create create;

    @Mock
    private InputValidator inputValidator;

    // Zur direkten Methodevaluierung injizieren wir den Controller auch als Bean.
    @Mock
    private HandleVertrag handleVertrag;

    // Autowire MockMvc für Web-MVC-Tests
    @org.springframework.beans.factory.annotation.Autowired
    private MockMvc mockMvc;

    /**
     * Testet den POST-Request an "/home", wenn der Parameter "vsnr" leer ist.
     * Erwartet wird, dass die View "home" zurückgegeben wird.
     */
    @Test
    public void testProcessPrintVertrag_EmptyVsnr() throws Exception {
        mockMvc.perform(post("/home").param("vsnr", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    /**
     * Testet den POST-Request an "/home" mit einem ungültigen (nicht-numerischen) VSNR-Wert.
     * Es wird erwartet, dass im Model ein Fehler ("Ungültige VSNR!") abgelegt und die View "home" gerendert wird.
     */
    @Test
    public void testProcessPrintVertrag_InvalidVsnr() throws Exception {
        mockMvc.perform(post("/home").param("vsnr", "abc"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", "Ungültige VSNR!"))
                .andExpect(view().name("home"));
    }

    /**
     * Testet den POST-Request an "/home", wenn die angefragte VSNR zwar numerisch, aber nicht vorhanden ist.
     */
    @Test
    public void testProcessPrintVertrag_NotFound() throws Exception {
        int vsnr = 123;
        when(vertragsverwaltung.getVertrag(vsnr)).thenReturn(null);

        mockMvc.perform(post("/home").param("vsnr", String.valueOf(vsnr)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", "Vertrag nicht gefunden!"))
                .andExpect(view().name("home"));
    }

    /**
     * Testet den POST-Request an "/home", wenn ein Vertrag gefunden wird.
     * Hier wird ein Dummy-Vertrag erstellt, und es wird geprüft, ob der Model-Aufbau (z. B. die VSNR) erfolgt.
     */
    @Test
    public void testProcessPrintVertrag_Found() throws Exception {
        int vsnr = 456;
        Partner partner = new Partner("John", "Doe", 'M', LocalDate.of(1990, 1, 1),
                "Germany", "Main Street", "10", "12345", "Berlin", "Berlin");
        Fahrzeug fahrzeug = new Fahrzeug("XYZ-123", "VW", "Golf", 200, 42);
        Vertrag vertrag = new Vertrag(vsnr, true, 1000.0, LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 1, 1), LocalDate.now(), fahrzeug, partner);
        when(vertragsverwaltung.getVertrag(vsnr)).thenReturn(vertrag);

        mockMvc.perform(post("/home").param("vsnr", String.valueOf(vsnr)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vsnr", vsnr))
                .andExpect(view().name("handleVertrag"));
    }

    /**
     * Testet den GET-Request an "/showDelete" und überprüft, ob das Model-Attribut "showFields" gesetzt wird.
     */
    @Test
    public void testShowDelete() throws Exception {
        mockMvc.perform(get("/showDelete"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("showFields", true))
                .andExpect(view().name("handleVertrag"));
    }

    /**
     * Testet den POST-Request an "/showDelete".
     * Es wird erwartet, dass der Vertrag über die Vertragsverwaltung gelöscht wird
     * und ein Bestätigungstext im Model abgelegt wird.
     */
    @Test
    public void testDeleteVertrag() throws Exception {
        int vsnr = 789;
        when(menuSpring.getVsnr()).thenReturn(vsnr);

        mockMvc.perform(post("/showDelete"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("confirm", "Vertrag erfolgreich gelöscht!"))
                .andExpect(view().name("home"));

        verify(vertragsverwaltung).vertragLoeschen(vsnr);
    }

    /**
     * Direkter Unit-Test der Methode editVertrag, wenn Validierungsfehler auftreten.
     * Hier wird ein Fehler in der BindingResult simuliert, sodass die View "handleVertrag" zurückgegeben wird.
     */
    @Test
    public void testEditVertrag_WithErrors() {
        // Erstelle einen Beispielvertrag
        Partner partner = new Partner("Alice", "Smith", 'F', LocalDate.of(1985, 5, 15),
                "Germany", "Street", "5", "10115", "Berlin", "Berlin");
        Fahrzeug fahrzeug = new Fahrzeug("ABC-999", "BMW", "X5", 240, 99);
        Vertrag inputVertrag = new Vertrag(0, false, 0.0,
                LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), LocalDate.now(), fahrzeug, partner);

        // Simuliere Validierungsfehler
        BindingResult bindingResult = new BeanPropertyBindingResult(inputVertrag, "vertrag");
        bindingResult.reject("error", "Error message");

        // Simuliere, dass bereits ein existierender Vertrag vorhanden ist
        when(vertragsverwaltung.getVertrag(anyInt())).thenReturn(inputVertrag);

        Model model = new ExtendedModelMap();
        // Da wir den Controller direkt testen, rufen wir die Methode auf
        // (Hinweis: handleVertrag ist hier als echte Bean verfügbar)
        String view = handleVertrag.editVertrag(inputVertrag, bindingResult, true, model);

        // Erwartet wird die Rückgabe der View "handleVertrag" und das Model-Attribut "editVisible" auf true
        org.junit.jupiter.api.Assertions.assertEquals("handleVertrag", view);
        org.junit.jupiter.api.Assertions.assertTrue((Boolean) model.getAttribute("editVisible"));
    }

    /**
     * Direkter Unit-Test der Methode editVertrag im Erfolgsfall (keine Validierungsfehler).
     * Es wird erwartet, dass der Vertrag gelöscht, neu angelegt wird (über "create") und
     * ein Bestätigungstext im Model abgelegt wird.
     */
    @Test
    public void testEditVertrag_Success() {
        Partner partner = new Partner("Bob", "Johnson", 'M', LocalDate.of(1975, 7, 20),
                "Germany", "Broadway", "20", "20095", "Hamburg", "Hamburg");
        Fahrzeug fahrzeug = new Fahrzeug("DEF-456", "Audi", "A4", 220, 88);
        Vertrag inputVertrag = new Vertrag(0, true, 0.0,
                LocalDate.of(2023, 6, 1), LocalDate.of(2024, 6, 1), LocalDate.now(), fahrzeug, partner);

        BindingResult bindingResult = new BeanPropertyBindingResult(inputVertrag, "vertrag");
        // Keine Fehler hinzufügen

        int vsnr = 321;
        when(menuSpring.getVsnr()).thenReturn(vsnr);
        when(create.createVertragAndSave(any(Vertrag.class), anyBoolean(), anyInt())).thenReturn(1500.0);

        Model model = new ExtendedModelMap();
        String view = handleVertrag.editVertrag(inputVertrag, bindingResult, false, model);

        org.junit.jupiter.api.Assertions.assertEquals("home", view);
        String confirmMsg = (String) model.getAttribute("confirm");
        org.junit.jupiter.api.Assertions.assertTrue(confirmMsg.contains("321"));
        org.junit.jupiter.api.Assertions.assertTrue(confirmMsg.contains("1500,0€"));
        verify(vertragsverwaltung).vertragLoeschen(vsnr);
    }
}
