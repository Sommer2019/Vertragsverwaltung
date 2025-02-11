package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Preis;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@Import(CustomTestConfig.class)
@ExtendWith(MockitoExtension.class)
class EditPreisTest {

    @InjectMocks
    private EditPreis editPreisController;

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private Repository repository;

    @Mock
    private CreateData createData;


    private Model model;

    @BeforeEach
    void setUp() {
        model = new ExtendedModelMap();
    }

    /**
     * Testet die GET-Mapping /editPreis.
     */
    @Test
    void testGetEditPreis() {
        // Erstelle ein JsonObject mit Faktoren
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.1)
                .add("factorage", 2.2)
                .add("factorspeed", 3.3)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        // Aufruf der Methode
        String view = editPreisController.editPreis(model);

        // Überprüfe, dass der richtige View-Name zurückgegeben wird und das Modell befüllt ist
        assertEquals("editPreis", view);
        assertTrue(model.containsAttribute("preismodell"));
        Preis preismodell = (Preis) model.asMap().get("preismodell");
        assertEquals(1.1, preismodell.getFaktor());
        assertEquals(2.2, preismodell.getAge());
        assertEquals(3.3, preismodell.getSpeed());
    }

    /**
     * Testet die POST-Mapping /precalcPreis, die einen neuen Preis als JSON zurückgibt.
     */
    @Test
    void testPrecalcPreis() {
        // Erstelle ein Eingabe-Preisobjekt mit neuen Faktoren
        Preis inputPreis = new Preis();
        inputPreis.setFaktor(2.0);
        inputPreis.setAge(3.0);
        inputPreis.setSpeed(4.0);

        // Repository gibt ein JsonObject mit anderen Faktoren zurück
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.5)
                .add("factorage", 2.5)
                .add("factorspeed", 3.5)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        // Erstelle eine Liste mit einem Dummy-Vertrag (nicht monatlich)
        Vertrag vertrag = createDummyVertrag(101, false);
        List<Vertrag> vertrage = List.of(vertrag);
        when(vertragsverwaltung.getVertrage()).thenReturn(vertrage);

        // Stub: createData.createPreis soll einen festen Preis zurückgeben (z. B. 100.0)
        when(createData.createPreis(anyBoolean(), any(), anyInt())).thenReturn(100.0);

        // Aufruf der Methode
        Map<String, Object> response = editPreisController.editPreis(inputPreis);

        // Für einen nicht monatlichen Vertrag sollte der Preis 100,00 € betragen
        assertNotNull(response);
        assertTrue(response.containsKey("preis"));
        String preisStr = (String) response.get("preis");
        assertEquals("100,00 €", preisStr);

        // Überprüfe, dass repository.speichereFaktoren zweimal aufgerufen wurde:
        // einmal mit den Eingabefaktoren und einmal mit den JSON-Faktoren
        verify(repository).speichereFaktoren(2.0, 3.0, 4.0);
        verify(repository).speichereFaktoren(1.5, 2.5, 3.5);
    }

    /**
     * Testet die POST-Mapping /editPreis, welche den Preis aktualisiert und den View "home" zurückgibt.
     */
    @Test
    void testPostEditPreis() {
        // Erstelle ein Eingabe-Preisobjekt
        Preis inputPreis = new Preis();
        inputPreis.setFaktor(2.0);
        inputPreis.setAge(3.0);
        inputPreis.setSpeed(4.0);

        // Erstelle eine Liste mit einem Dummy-Vertrag (nicht monatlich)
        Vertrag vertrag = createDummyVertrag(202, false);
        List<Vertrag> vertrage = List.of(vertrag);
        when(vertragsverwaltung.getVertrage()).thenReturn(vertrage);

        // Stub: createData.createPreis soll einen festen Preis zurückgeben (z. B. 100.0)
        when(createData.createPreis(anyBoolean(), any(), anyInt())).thenReturn(100.0);

        // Aufruf der Methode
        String view = editPreisController.editPreis(inputPreis, model);

        // Für einen nicht monatlichen Vertrag sollte der Preis 100,00 € betragen
        String expectedConfirm = "Preise erfolgreich aktualisiert! neue Preissumme: 100,00€ pro Jahr";
        assertEquals("home", view);
        assertTrue(model.containsAttribute("confirm"));
        assertEquals(expectedConfirm, model.asMap().get("confirm"));

        // Überprüfe, dass repository.speichereFaktoren mit den Eingabefaktoren aufgerufen wurde
        verify(repository).speichereFaktoren(2.0, 3.0, 4.0);
        // Verifiziere, dass für den Vertrag vertragLoeschen und vertragAnlegen aufgerufen wurden
        verify(vertragsverwaltung).vertragLoeschen(vertrag.getVsnr());
        verify(vertragsverwaltung).vertragAnlegen(vertrag);
    }

    /**
     * Hilfsmethode zur Erstellung eines Dummy-Vertrags für die Tests.
     *
     * @param vsnr    die Vertragsnummer
     * @param monthly true, wenn der Vertrag monatlich abgerechnet wird
     * @return ein Dummy-Vertrag
     */
    private Vertrag createDummyVertrag(int vsnr, boolean monthly) {
        Vertrag vertrag = new Vertrag();
        vertrag.setVsnr(vsnr);
        vertrag.setMonatlich(monthly);
        // Setze ein zukünftiges Ablaufdatum, damit der Vertrag gültig ist.
        vertrag.setVersicherungsablauf(LocalDate.now().plusDays(365));

        // Initialisiere Partner, falls nicht vorhanden
        if (vertrag.getPartner() == null) {
            Partner partner = new Partner();
            partner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
            vertrag.setPartner(partner);
        }
        // Initialisiere Fahrzeug, falls nicht vorhanden
        if (vertrag.getFahrzeug() == null) {
            Fahrzeug fahrzeug = new Fahrzeug();
            fahrzeug.setHoechstgeschwindigkeit(200);
            vertrag.setFahrzeug(fahrzeug);
        }
        return vertrag;
    }
}
