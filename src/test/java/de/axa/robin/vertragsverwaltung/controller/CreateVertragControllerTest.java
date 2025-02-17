package de.axa.robin.vertragsverwaltung.controller;

import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.services.PreisModelService;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import de.axa.robin.vertragsverwaltung.util.VertragUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreateVertragController.class)
public class CreateVertragControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VertragsService vertragsService;
    @MockitoBean
    private VertragUtil vertragUtil;
    @MockitoBean
    private MenuController menuController;
    @MockitoBean
    private PreisModelService preisModelService;

    /**
     * Testet den GET-Endpunkt "/createVertrag" und überprüft,
     * ob die erforderlichen Model-Attribute gesetzt und die richtige View zurückgegeben wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateVertragGet() throws Exception {
        int expectedVsnr = 10000000;
        // Simuliere, dass derzeit keine Verträge existieren
        Mockito.when(vertragsService.getVertrage()).thenReturn(new ArrayList<>());
        Mockito.when(vertragsService.createvsnr(ArgumentMatchers.any())).thenReturn(expectedVsnr);
        Mockito.when(menuController.getVsnr()).thenReturn(expectedVsnr);

        mockMvc.perform(get("/createVertrag").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vertrag"))
                .andExpect(model().attribute("vsnr", expectedVsnr))
                .andExpect(view().name("createVertrag"));
    }

    /**
     * Testet den POST-Endpunkt "/createVertrag" bei erfolgreicher Vertragserstellung.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateVertragPostSuccess() throws Exception {
        // Simuliere das Preismodell
        Preis preisModel = new Preis();
        Mockito.when(preisModelService.getPreismodell()).thenReturn(preisModel);
        // Für diesen Test soll keine Exception auftreten

        mockMvc.perform(post("/createVertrag")
                        .param("monatlich", "true")
                        // Partner-Daten
                        .param("partner.plz", "12345")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(20).toString())
                        .param("partner.geschlecht", "M")
                        .param("partner.land", "Deutschland")
                        // Fahrzeug-Daten
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112")
                        // Weitere Felder
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("versicherungsbeginn", LocalDate.now().toString())
                        .param("antragsDatum", LocalDate.now().toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("confirm"))
                .andExpect(view().name("home"));
    }

    /**
     * Testet den POST-Endpunkt "/createVertrag", wenn während der Vertragserstellung eine Exception auftritt.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateVertragPostError() throws Exception {
        Preis preisModel = new Preis();
        Mockito.when(preisModelService.getPreismodell()).thenReturn(preisModel);
        // Simuliere, dass bei der Vertragserstellung eine IllegalArgumentException geworfen wird.
        Mockito.doThrow(new IllegalArgumentException("Error"))
                .when(vertragsService)
                .vertragAnlegen(ArgumentMatchers.any(Vertrag.class), ArgumentMatchers.eq(preisModel), ArgumentMatchers.any());
        Mockito.when(menuController.getVsnr()).thenReturn(10000000);

        mockMvc.perform(post("/createVertrag")
                        .param("monatlich", "true")
                        .param("partner.plz", "12345")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(20).toString())
                        .param("partner.geschlecht", "M")
                        .param("partner.land", "Deutschland")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112")
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("versicherungsbeginn", LocalDate.now().toString())
                        .param("antragsDatum", LocalDate.now().toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vsnr"))
                .andExpect(view().name("createVertrag"));
    }

    /**
     * Testet den POST-Endpunkt "/createPreis" für den Fall, dass die PLZ vorhanden ist und der Preis korrekt berechnet wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePreisWithValidPlz() throws Exception {
        double calculatedPrice = 123.45;
        Preis preisModel = new Preis();
        Mockito.when(preisModelService.getPreismodell()).thenReturn(preisModel);
        Mockito.when(vertragsService.createPreis(
                        ArgumentMatchers.anyBoolean(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.anyInt(),
                        ArgumentMatchers.eq(preisModel)))
                .thenReturn(calculatedPrice);

        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "12345")
                        .param("monatlich", "true")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(20).toString())
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112")
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                // Überprüft, ob der Preis im deutschen Format ausgegeben wird (z. B. "123,45 €")
                .andExpect(jsonPath("$.preis", containsString("123,45")));
    }

    /**
     * Testet den POST-Endpunkt "/createPreis" für den Fall, dass die PLZ leer ist.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePreisWithEmptyPlz() throws Exception {
        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "")
                        .param("monatlich", "true")
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis").value("--,-- €"));
    }

    /**
     * Testet den POST-Endpunkt "/createPreis" für den Fall, dass das Versicherungsablauf-Datum null ist.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePreisWithNullVersicherungsablauf() throws Exception {
        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "12345")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("monatlich", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis").value("0,00 €"));
    }

    /**
     * Testet den POST-Endpunkt "/createPreis" für den Fall, dass das Versicherungsablauf-Datum in der Vergangenheit liegt.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePreisWithExpiredContract() throws Exception {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "12345")
                        .param("monatlich", "true")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("versicherungsablauf", pastDate.toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis").value("0,00 €"));
    }
}
