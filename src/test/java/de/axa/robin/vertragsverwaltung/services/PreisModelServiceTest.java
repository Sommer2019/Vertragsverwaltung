package de.axa.robin.vertragsverwaltung.services;

import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PreisModelServiceTest {

    @InjectMocks
    private PreisModelService preisModelService;

    @Mock
    private Repository repository;

    @Mock
    private VertragsService vertragsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testet, ob getPreismodell() anhand des vom Repository gelieferten JsonObject
     * ein Preis-Objekt mit den korrekten Werten erzeugt.
     */
    @Test
    public void testGetPreismodell() {
        // Erzeuge ein JsonObject mit den Testwerten
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.5)
                .add("factorage", 2.0)
                .add("factorspeed", 3.0)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        Preis preis = preisModelService.getPreismodell();

        assertNotNull(preis, "Das Preis-Objekt sollte nicht null sein.");
        assertEquals(1.5, preis.getFaktor(), "Der Faktor sollte 1.5 betragen.");
        assertEquals(2.0, preis.getAge(), "Der Altersfaktor sollte 2.0 betragen.");
        assertEquals(3.0, preis.getSpeed(), "Der Geschwindigkeitsfaktor sollte 3.0 betragen.");
    }

    /**
     * Testet updatePreisAndModell() im Preview-Modus.
     * Hier werden zwei Verträge (ein monatlicher und ein nicht-monatlicher) simuliert.
     * Es wird erwartet, dass im Preview-Modus keine Änderungen am Repository vorgenommen werden.
     */
    @Test
    public void testUpdatePreisAndModellPreview() {
        // Setup des JSON-Objekts für das Preismodell
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.5)
                .add("factorage", 2.0)
                .add("factorspeed", 3.0)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        // Erzeuge zwei Verträge
        Vertrag monthlyVertrag = new Vertrag();
        monthlyVertrag.setVsnr(1001);
        monthlyVertrag.setMonatlich(true);
        monthlyVertrag.setVersicherungsablauf(LocalDate.now().plusDays(1));
        monthlyVertrag.setPartner(new Partner());
        monthlyVertrag.getPartner().setGeburtsdatum(LocalDate.of(1990, 1, 1));
        monthlyVertrag.setFahrzeug(new Fahrzeug());
        monthlyVertrag.getFahrzeug().setHoechstgeschwindigkeit(200);

        Vertrag nonMonthlyVertrag = new Vertrag();
        nonMonthlyVertrag.setVsnr(1002);
        nonMonthlyVertrag.setMonatlich(false);
        nonMonthlyVertrag.setVersicherungsablauf(LocalDate.now().plusDays(1));
        nonMonthlyVertrag.setPartner(new Partner());
        nonMonthlyVertrag.getPartner().setGeburtsdatum(LocalDate.of(1990, 1, 1));
        nonMonthlyVertrag.setFahrzeug(new Fahrzeug());
        nonMonthlyVertrag.getFahrzeug().setHoechstgeschwindigkeit(200);

        List<Vertrag> vertragsList = new ArrayList<>();
        vertragsList.add(monthlyVertrag);
        vertragsList.add(nonMonthlyVertrag);
        when(vertragsService.getVertrage()).thenReturn(vertragsList);

        // Stub: Simuliere, dass createPreis() für beide Verträge 10.0 zurückgibt
        when(vertragsService.createPreis(anyBoolean(), any(), anyInt(), any(Preis.class))).thenReturn(10.0);

        // Neues Preismodell, das verwendet werden soll
        Preis newPreismodell = new Preis();
        newPreismodell.setFaktor(1.5);
        newPreismodell.setAge(2.0);
        newPreismodell.setSpeed(3.0);

        // Aufruf der Methode im Preview-Modus
        BigDecimal resultSum = preisModelService.updatePreisAndModell(newPreismodell, true, vertragsList);

        // Erwartete Gesamtsumme:
        // - Für den monatlichen Vertrag: 10.0 * 12 = 120.0
        // - Für den nicht-monatlichen Vertrag: 10.0
        // Summe = 120.0 + 10.0 = 130.0
        BigDecimal expected = BigDecimal.valueOf(130.0).setScale(2, RoundingMode.HALF_DOWN);
        assertEquals(expected, resultSum, "Die berechnete Gesamtsumme sollte 130,00€ betragen.");

        // Im Preview-Modus sollten keine Änderungen vorgenommen werden
        verify(vertragsService, never()).vertragLoeschen(anyInt(), anyList());
        verify(vertragsService, never()).vertragAnlegen(any(Vertrag.class), any(Preis.class), any());
    }

    /**
     * Testet updatePreisAndModell() im Nicht-Preview-Modus.
     * Es wird ein einzelner Vertrag simuliert, dessen Preis neu berechnet wird.
     * Zusätzlich wird überprüft, ob die Methoden vertragLoeschen und vertragAnlegen aufgerufen werden.
     */
    @Test
    public void testUpdatePreisAndModellNotPreview() {
        // Setup des JSON-Objekts für das Preismodell
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.5)
                .add("factorage", 2.0)
                .add("factorspeed", 3.0)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        // Erzeuge einen Vertrag (nicht abgelaufen, nicht monatlich)
        Vertrag vertrag = new Vertrag();
        vertrag.setVsnr(1003);
        vertrag.setMonatlich(false);
        vertrag.setVersicherungsablauf(LocalDate.now().plusDays(1));
        vertrag.setPartner(new Partner());
        vertrag.getPartner().setGeburtsdatum(LocalDate.of(1990, 1, 1));
        vertrag.setFahrzeug(new Fahrzeug());
        vertrag.getFahrzeug().setHoechstgeschwindigkeit(200);

        List<Vertrag> vertragsList = new ArrayList<>();
        vertragsList.add(vertrag);
        when(vertragsService.getVertrage()).thenReturn(vertragsList);

        // Stub: Simuliere, dass createPreis() für diesen Vertrag 20.0 zurückgibt
        when(vertragsService.createPreis(anyBoolean(), any(), anyInt(), any(Preis.class))).thenReturn(20.0);

        Preis newPreismodell = new Preis();
        newPreismodell.setFaktor(1.5);
        newPreismodell.setAge(2.0);
        newPreismodell.setSpeed(3.0);

        // Aufruf der Methode im Nicht-Preview-Modus
        BigDecimal resultSum = preisModelService.updatePreisAndModell(newPreismodell, false, vertragsList);

        // Für einen nicht-monatlichen Vertrag: erwarteter Preis = 20.0
        BigDecimal expected = BigDecimal.valueOf(20.0).setScale(2, RoundingMode.HALF_DOWN);
        assertEquals(expected, resultSum, "Die berechnete Gesamtsumme sollte 20,00€ betragen.");

        // Im Nicht-Preview-Modus sollten vertragLoeschen und vertragAnlegen aufgerufen werden
        verify(vertragsService).vertragLoeschen(eq(vertrag.getVsnr()), anyList());
        verify(vertragsService).vertragAnlegen(eq(vertrag), any(Preis.class), isNull());
    }
}
