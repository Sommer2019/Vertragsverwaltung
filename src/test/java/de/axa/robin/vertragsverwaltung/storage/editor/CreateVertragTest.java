package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.user_interaction.MenuSpring;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Locale;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(de.axa.robin.vertragsverwaltung.storage.editor.CreateVertrag.class)
@Import(CustomTestConfig.class)
@ExtendWith(MockitoExtension.class)
public class CreateVertragTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private CreateData createData;

    @Mock
    private MenuSpring menuSpring;

    @Mock
    private InputValidator inputValidator;

    @InjectMocks
    private CreateVertrag createVertrag;

    /**
     * Testet den GET-Aufruf von /createVertrag.
     * Hier wird überprüft, ob ein generierter VSNR im Modell vorhanden ist.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetCreateVertrag() throws Exception {
        int generatedVsnr = 1000;
        Mockito.when(createData.createvsnr()).thenReturn(generatedVsnr);
        Mockito.when(menuSpring.getVsnr()).thenReturn(generatedVsnr);

        mockMvc.perform(get("/createVertrag").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("createVertrag"))
                .andExpect(model().attributeExists("vertrag"))
                .andExpect(model().attribute("vsnr", generatedVsnr));
    }

    /**
     * Testet den POST-Aufruf von /createVertrag im Erfolgsfall.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testPostCreateVertrag_Success() throws Exception {
        mockMvc.perform(post("/createVertrag")
                        .param("versicherungsbeginn", LocalDate.now().toString())
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("antragsDatum", LocalDate.now().toString())
                        .param("monatlich", "true")
                        .param("partner.vorname", "Max")
                        .param("partner.nachname", "Mustermann")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("partner.geschlecht", "M")
                        .param("partner.land", "Deutschland")
                        .param("partner.strasse", "Hauptstr.")
                        .param("partner.hausnummer", "1")
                        .param("partner.plz", "12345")
                        .param("partner.stadt", "Berlin")
                        .param("partner.bundesland", "BE")
                        .param("fahrzeug.amtlichesKennzeichen", "ABC123")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("createVertrag"));
    }

    /**
     * Testet den POST-Aufruf von /createVertrag bei Validierungsfehlern (z. B. ungültiges Datum).
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testPostCreateVertrag_ValidationError() throws Exception {
        LocalDate pastDate = LocalDate.now().minusDays(1);

        mockMvc.perform(post("/createVertrag")
                        .param("versicherungsbeginn", pastDate.toString())
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("antragsDatum", LocalDate.now().toString())
                        .param("monatlich", "true")
                        .param("partner.vorname", "Max")
                        .param("partner.nachname", "Mustermann")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("partner.geschlecht", "M")
                        .param("partner.land", "Deutschland")
                        .param("partner.strasse", "Hauptstr.")
                        .param("partner.hausnummer", "1")
                        .param("partner.plz", "12345")
                        .param("partner.stadt", "Berlin")
                        .param("partner.bundesland", "BE")
                        .param("fahrzeug.amtlichesKennzeichen", "ABC123")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("createVertrag"));
    }

    /**
     * Testet den POST-Aufruf von /createPreis mit gültiger PLZ.
     * Dabei wird die kalkulierte Preisangabe als JSON zurückgegeben.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreatePreis_WithValidPlz() throws Exception {
        double preisValue = 123.45;
        Mockito.when(createData.createPreis(Mockito.anyBoolean(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(preisValue);

        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "12345")
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("monatlich", "true")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is(String.format(Locale.GERMANY, "%.2f €", preisValue))));
    }

    /**
     * Testet den POST-Aufruf von /createPreis, wenn die Versicherung bereits abgelaufen ist.
     * In diesem Fall soll der Preis "0,00 €" zurückgegeben werden.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreatePreis_WithExpiredInsurance() throws Exception {
        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "12345")
                        .param("versicherungsablauf", LocalDate.now().minusDays(1).toString())
                        .param("monatlich", "true")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is("0,00 €")));
    }

    /**
     * Testet den POST-Aufruf von /createPreis, wenn die PLZ fehlt.
     * In diesem Fall soll ein Platzhalterwert ("--,-- €") zurückgegeben werden.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreatePreis_WithMissingPlz() throws Exception {
        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "")
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("monatlich", "true")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is("--,-- €")));
    }
}
