package de.axa.robin.vertragsverwaltung.frontend.storage;

import de.axa.robin.vertragsverwaltung.backend.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Create;
import de.axa.robin.vertragsverwaltung.backend.storage.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.frontend.user_interaction.MenuSpring;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreateVertrag.class)
@Import(CustomTestConfig.class)
public class CreateVertragTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private Create create;

    @Mock
    private MenuSpring menuSpring;

    @Mock
    private InputValidator inputValidator;

    /**
     * Testet den GET-Request auf "/createVertrag".
     * Es wird überprüft, ob der korrekte View-Name und die benötigten Model-Attribute gesetzt werden.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetCreateVertrag() throws Exception {
        int generatedVsnr = 1000;
        Mockito.when(create.createvsnr()).thenReturn(generatedVsnr);
        Mockito.when(menuSpring.getVsnr()).thenReturn(generatedVsnr);

        mockMvc.perform(get("/createVertrag"))
                .andExpect(status().isOk())
                .andExpect(view().name("createVertrag"))
                .andExpect(model().attributeExists("vertrag"))
                .andExpect(model().attribute("vsnr", generatedVsnr));
    }

    /**
     * Testet den POST-Request auf "/createVertrag" bei gültigen Eingaben.
     * Es werden alle erforderlichen Parameter als Form-Parameter übermittelt.
     * Erwartet wird, dass der Vertrag angelegt wird und der View "home" mit einer Bestätigung zurückgegeben wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testPostCreateVertrag_Success() throws Exception {
        int generatedVsnr = 2000;
        double preis = 250.0;
        Mockito.when(create.createvsnr()).thenReturn(generatedVsnr);
        Mockito.when(create.createPreis(Mockito.anyBoolean(), Mockito.any(), Mockito.anyInt())).thenReturn(preis);
        // Simuliere, dass vertragsverwaltung.vertragAnlegen den Vertrag unverändert zurückgibt.
        Mockito.when(vertragsverwaltung.vertragAnlegen(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/createVertrag")
                        .param("versicherungsbeginn", LocalDate.now().toString())
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("antragsDatum", LocalDate.now().toString())
                        .param("monatlich", "true")
                        // Partner-Daten
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
                        // Fahrzeug-Daten
                        .param("fahrzeug.amtlichesKennzeichen", "ABC123")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("confirm"))
                .andExpect(model().attribute("confirm", containsString(String.valueOf(generatedVsnr))));
    }

    /**
     * Testet den POST-Request auf "/createVertrag" bei ungültigen Eingaben.
     * Hier wird als Beispiel ein Versicherungsbeginn in der Vergangenheit übermittelt,
     * was zu einem Validierungsfehler führen soll. Erwartet wird, dass wieder der View "createVertrag" zurückgegeben wird.
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
                        // Minimal erforderliche Partner-Daten
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
                        // Minimal erforderliche Fahrzeug-Daten
                        .param("fahrzeug.amtlichesKennzeichen", "ABC123")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("createVertrag"));
    }

    /**
     * Testet den POST-Request auf "/createPreis", wenn eine gültige PLZ vorhanden ist.
     * Es wird überprüft, ob der von der Methode berechnete Preis im erwarteten Format als JSON zurückgegeben wird.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreatePreis_WithValidPlz() throws Exception {
        double preisValue = 123.45;
        Mockito.when(create.createPreis(Mockito.anyBoolean(), Mockito.any(), Mockito.anyInt())).thenReturn(preisValue);

        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "12345")
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("monatlich", "true")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is(String.format(Locale.GERMANY, "%.2f €", preisValue))));
    }

    /**
     * Testet den POST-Request auf "/createPreis", wenn der Versicherungsablauf bereits in der Vergangenheit liegt.
     * Erwartet wird in diesem Fall der Preis "0,00 €".
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
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is("0,00 €")));
    }

    /**
     * Testet den POST-Request auf "/createPreis", wenn keine PLZ angegeben wurde.
     * In diesem Fall wird der Preis als "--,-- €" zurückgegeben.
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
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is("--,-- €")));
    }
}
