package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//ToDO Tests
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
    private CreateData createData;

    @InjectMocks
    private EditPreis editPreis;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testGetEditPreis() throws Exception {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.1)
                .add("factorage", 2.2)
                .add("factorspeed", 3.3)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        mockMvc.perform(get("/editPreis"))
                .andExpect(status().isOk())
                .andExpect(view().name("editPreis"))
                .andExpect(model().attributeExists("preismodell"))
                .andExpect(model().attribute("preismodell", hasProperty("faktor", is(1.1))))
                .andExpect(model().attribute("preismodell", hasProperty("age", is(2.2))))
                .andExpect(model().attribute("preismodell", hasProperty("speed", is(3.3))));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testPostPrecalcPreis() throws Exception {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.1)
                .add("factorage", 2.2)
                .add("factorspeed", 3.3)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);
        when(vertragsverwaltung.getVertrage()).thenReturn(Collections.emptyList());
        BigDecimal calculatedPrice = BigDecimal.valueOf(100.25);
        when(editPreis.recalcPrice(2.5, 3.5, 4.5, Collections.emptyList())).thenReturn(calculatedPrice);
        when(editPreis.recalcPrice(1.1, 2.2, 3.3, Collections.emptyList())).thenReturn(calculatedPrice);

        mockMvc.perform(post("/precalcPreis")
                        .param("faktor", "2.5")
                        .param("age", "3.5")
                        .param("speed", "4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is("100,25 €")));
    }

    @Test
    @Disabled //ToDo Fix
    public void testRecalcPrice() {
        // Vertrag 1: Nicht monatlich, gültige Versicherung
        Vertrag vertrag1 = new Vertrag();
        vertrag1.setVsnr(1);
        vertrag1.setMonatlich(false);
        vertrag1.setVersicherungsablauf(LocalDate.now().plusDays(10));
        Partner partner1 = new Partner();
        partner1.setGeburtsdatum(LocalDate.of(1980, 1, 1));
        vertrag1.setPartner(partner1);
        Fahrzeug fahrzeug1 = new Fahrzeug();
        fahrzeug1.setHoechstgeschwindigkeit(200);
        vertrag1.setFahrzeug(fahrzeug1);

        // Vertrag 2: Monatlich, gültige Versicherung
        Vertrag vertrag2 = new Vertrag();
        vertrag2.setVsnr(2);
        vertrag2.setMonatlich(true);
        vertrag2.setVersicherungsablauf(LocalDate.now().plusDays(10));
        Partner partner2 = new Partner();
        partner2.setGeburtsdatum(LocalDate.of(1990, 2, 2));
        vertrag2.setPartner(partner2);
        Fahrzeug fahrzeug2 = new Fahrzeug();
        fahrzeug2.setHoechstgeschwindigkeit(180);
        vertrag2.setFahrzeug(fahrzeug2);

        // Vertrag 3: Abgelaufene Versicherung (soll nicht zur Summe addiert werden)
        Vertrag vertrag3 = new Vertrag();
        vertrag3.setVsnr(3);
        vertrag3.setMonatlich(false);
        vertrag3.setVersicherungsablauf(LocalDate.now().minusDays(1));
        Partner partner3 = new Partner();
        partner3.setGeburtsdatum(LocalDate.of(1970, 3, 3));
        vertrag3.setPartner(partner3);
        Fahrzeug fahrzeug3 = new Fahrzeug();
        fahrzeug3.setHoechstgeschwindigkeit(160);
        vertrag3.setFahrzeug(fahrzeug3);

        List<Vertrag> vertrage = List.of(vertrag1, vertrag2, vertrag3);

        // Setup des Mocks für create.createPreis()
        when(createData.createPreis(eq(false), any(LocalDate.class), anyInt())).thenReturn(100.0);
        when(createData.createPreis(eq(true), any(LocalDate.class), anyInt())).thenReturn(50.0);

        BigDecimal result = editPreis.recalcPrice(1.1, 1.2, 1.3, vertrage);

        // Erwartete Summe:
        // - Vertrag 1: 100.0 (nicht monatlich)
        // - Vertrag 2: 50.0 * 12 = 600.0 (monatlich)
        // - Vertrag 3: abgelaufen → 0
        BigDecimal expectedSum = BigDecimal.valueOf(700.0).setScale(1, RoundingMode.HALF_DOWN);
        assertEquals(expectedSum, result);

        // Überprüfen, ob repository.speichereFaktoren mit den korrekten Werten aufgerufen wurde.
        verify(repository).speichereFaktoren(1.1, 1.2, 1.3);

        // Überprüfen, ob für jeden Vertrag die Lösch- und Anlege-Methoden aufgerufen wurden.
        verify(vertragsverwaltung).vertragLoeschen(1);
        verify(vertragsverwaltung).vertragAnlegen(vertrag1);
        verify(vertragsverwaltung).vertragLoeschen(2);
        verify(vertragsverwaltung).vertragAnlegen(vertrag2);
        verify(vertragsverwaltung).vertragLoeschen(3);
        verify(vertragsverwaltung).vertragAnlegen(vertrag3);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testPostEditPreis() throws Exception {
        when(vertragsverwaltung.getVertrage()).thenReturn(Collections.emptyList());
        BigDecimal calculatedPrice = BigDecimal.valueOf(200.50);
        when(editPreis.recalcPrice(2.5, 3.5, 4.5, Collections.emptyList())).thenReturn(calculatedPrice);

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