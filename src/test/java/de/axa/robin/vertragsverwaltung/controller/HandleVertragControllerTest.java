package de.axa.robin.vertragsverwaltung.controller;

import de.axa.robin.vertragsverwaltung.VertragsverwaltungApplication;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.services.PreisModelService;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VertragsverwaltungApplication.class)
@AutoConfigureMockMvc
class HandleVertragControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VertragsService vertragsService;

    @MockitoBean
    private MenuController menuController;

    @MockitoBean
    private PreisModelService preisModelService;

    /**
     * Hilfsmethode zur Erstellung eines gültigen Vertrags.
     */
    private Vertrag createValidVertrag(int vsnr, double preis, boolean monatlich, LocalDate beginn, LocalDate ablauf) {
        Partner partner = new Partner();
        partner.setVorname("Max");
        partner.setNachname("Mustermann");
        partner.setGeschlecht("M");
        partner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        partner.setStrasse("Musterstraße");
        partner.setHausnummer("1");
        partner.setPlz("12345");
        partner.setStadt("Musterstadt");
        partner.setBundesland("Bundesland");
        partner.setLand("Deutschland");

        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setAmtlichesKennzeichen("AB-123");
        fahrzeug.setHersteller("VW");
        fahrzeug.setTyp("Golf");
        fahrzeug.setHoechstgeschwindigkeit(200);
        fahrzeug.setWagnisskennziffer(112);

        Vertrag vertrag = new Vertrag(fahrzeug, partner);
        vertrag.setVsnr(vsnr);
        vertrag.setPreis(preis);
        vertrag.setMonatlich(monatlich);
        vertrag.setVersicherungsbeginn(beginn);
        vertrag.setVersicherungsablauf(ablauf);
        vertrag.setAntragsDatum(LocalDate.now());
        return vertrag;
    }

    /**
     * Testet den POST-Endpunkt "/home" mit gültiger VSNR, sodass ein existierender Vertrag gefunden und
     * das Model korrekt befüllt wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testProcessPrintVertragSuccess() throws Exception {
        int vsnr = 123;
        Vertrag vertrag = createValidVertrag(vsnr, 100.0, true, LocalDate.now(), LocalDate.now().plusDays(30));
        Mockito.when(vertragsService.getVertrag(vsnr)).thenReturn(vertrag);
        Mockito.doNothing().when(menuController).setVsnr(vsnr);
        Mockito.when(menuController.getVsnr()).thenReturn(vsnr);

        mockMvc.perform(post("/home")
                        .param("vsnr", String.valueOf(vsnr)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vertrag"))
                .andExpect(model().attribute("vsnr", vsnr))
                .andExpect(view().name("handleVertrag"));
    }

    /**
     * Testet den POST-Endpunkt "/home" mit ungültigem VSNR-Format.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testProcessPrintVertragInvalidFormat() throws Exception {
        mockMvc.perform(post("/home")
                        .param("vsnr", "abc").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", "Ungültige VSNR!"))
                .andExpect(view().name("home"));
    }

    /**
     * Testet den POST-Endpunkt "/home", wenn der Vertrag nicht gefunden wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testProcessPrintVertragNotFound() throws Exception {
        int vsnr = 123;
        Mockito.doNothing().when(menuController).setVsnr(vsnr);
        Mockito.when(menuController.getVsnr()).thenReturn(vsnr);
        Mockito.when(vertragsService.getVertrag(vsnr)).thenThrow(new IllegalArgumentException("Not found"));

        mockMvc.perform(post("/home")
                        .param("vsnr", String.valueOf(vsnr)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", "Vertrag nicht gefunden!"))
                .andExpect(view().name("home"));
    }

    // Beispiel für testDeleteVertrag: Verwende eq() für vsnr
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDeleteVertrag() throws Exception {
        int vsnr = 123;
        Mockito.when(menuController.getVsnr()).thenReturn(vsnr);
        // Verwende eq(vsnr) statt des Rohwerts
        Mockito.doNothing().when(vertragsService).vertragLoeschen(ArgumentMatchers.eq(vsnr), ArgumentMatchers.anyList());

        mockMvc.perform(post("/showDelete").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("confirm", "Vertrag erfolgreich gelöscht!"))
                .andExpect(view().name("home"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testEditVertragSuccess() throws Exception {
        int vsnr = 123;
        Vertrag vertrag = createValidVertrag(vsnr, 150.0, true, LocalDate.now(), LocalDate.now().plusDays(30));
        Mockito.when(menuController.getVsnr()).thenReturn(vsnr);
        // Ensure the getVertrag method returns a valid Vertrag object
        Mockito.when(vertragsService.getVertrag(vsnr)).thenReturn(vertrag);
        // Simulate a successful edit result
        Mockito.when(vertragsService.vertragBearbeiten(
                        ArgumentMatchers.any(Vertrag.class),
                        ArgumentMatchers.eq(vsnr),
                        ArgumentMatchers.any(Preis.class),
                        ArgumentMatchers.any()))
                .thenReturn(vertrag);

        mockMvc.perform(post("/showEdit")
                        .param("editVisible", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("confirm"))
                .andExpect(model().attribute("confirm", containsString("Vertrag mit VSNR " + vsnr + " erfolgreich bearbeitet!")))
                .andExpect(view().name("home"));
    }


    /**
     * Testet den POST-Endpunkt "/showEdit", wenn während der Bearbeitung eine IllegalArgumentException geworfen wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testEditVertragValidationError() throws Exception {
        int vsnr = 123;
        Mockito.when(menuController.getVsnr()).thenReturn(vsnr);
        // Simuliere, dass bei der Bearbeitung eine Exception auftritt
        Mockito.doThrow(new IllegalArgumentException("Validation error"))
                .when(vertragsService).vertragBearbeiten(
                        ArgumentMatchers.any(Vertrag.class),
                        ArgumentMatchers.eq(vsnr),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any());
        // Simuliere, dass ein bestehender Vertrag zurückgegeben wird, um das Model erneut zu befüllen
        Vertrag existingVertrag = createValidVertrag(vsnr, 150.0, true, LocalDate.now(), LocalDate.now().plusDays(30));
        Mockito.when(vertragsService.getVertrag(vsnr)).thenReturn(existingVertrag);

        mockMvc.perform(post("/showEdit")
                        .param("editVisible", "true")
                        .param("monatlich", "true")
                        .param("versicherungsbeginn", LocalDate.now().toString())
                        .param("versicherungsablauf", LocalDate.now().plusDays(30).toString())
                        .param("antragsDatum", LocalDate.now().toString())
                        // Partner-Daten
                        .param("partner.vorname", "Max")
                        .param("partner.nachname", "Mustermann")
                        .param("partner.geschlecht", "M")
                        .param("partner.geburtsdatum", "1990-01-01")
                        .param("partner.strasse", "Musterstraße")
                        .param("partner.hausnummer", "1")
                        .param("partner.plz", "12345")
                        .param("partner.stadt", "Musterstadt")
                        .param("partner.bundesland", "Bundesland")
                        .param("partner.land", "Deutschland")
                        // Fahrzeug-Daten
                        .param("fahrzeug.amtlichesKennzeichen", "AB-123")
                        .param("fahrzeug.hersteller", "VW")
                        .param("fahrzeug.typ", "Golf")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("editVisible"))
                .andExpect(model().attribute("editVisible", true))
                .andExpect(view().name("handleVertrag"));
    }
}
