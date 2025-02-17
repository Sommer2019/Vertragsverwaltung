package de.axa.robin.vertragsverwaltung.services;

import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.util.VertragUtil;
import de.axa.robin.vertragsverwaltung.validators.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class VertragsServiceTest {

    @InjectMocks
    private VertragsService vertragsService;

    @Mock
    private Repository repository;

    @Mock
    private InputValidator inputValidator;

    @Mock
    private VertragUtil vertragUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test getVertrage()
    @Test
    public void testGetVertrage() {
        List<Vertrag> vertragsListe = new ArrayList<>();
        Vertrag vertrag = new Vertrag();
        vertragsListe.add(vertrag);
        when(repository.ladeVertrage()).thenReturn(vertragsListe);

        List<Vertrag> result = vertragsService.getVertrage();
        assertEquals(vertragsListe, result, "Die Liste der Verträge sollte zurückgegeben werden.");
    }

    // Test getVertrag() - Vertrag gefunden
    @Test
    public void testGetVertragFound() {
        int vsnr = 123;
        Vertrag vertrag = new Vertrag();
        vertrag.setVsnr(vsnr);
        List<Vertrag> vertragsListe = new ArrayList<>();
        vertragsListe.add(vertrag);
        when(repository.ladeVertrage()).thenReturn(vertragsListe);

        Vertrag result = vertragsService.getVertrag(vsnr);
        assertNotNull(result, "Der Vertrag sollte gefunden werden.");
        assertEquals(vsnr, result.getVsnr(), "Die VSNR des gefundenen Vertrags sollte übereinstimmen.");
    }

    // Test getVertrag() - Vertrag nicht gefunden
    @Test
    public void testGetVertragNotFound() {
        when(repository.ladeVertrage()).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            vertragsService.getVertrag(999);
        });
        assertTrue(exception.getMessage().contains("Contract not found"), "Es sollte eine passende Exception geworfen werden.");
    }

    // Test vertragAnlegen() - Erfolgreiche Anlage
    @Test
    public void testVertragAnlegenSuccess() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        List<Vertrag> vertragsListe = new ArrayList<>();
        when(repository.ladeVertrage()).thenReturn(vertragsListe);
        // Simuliere, dass für den generierten VSNR noch kein Vertrag existiert.
        when(inputValidator.vertragExistiert(anyList(), anyInt())).thenReturn(false);

        // Erstelle einen Vertrag mit notwendigen Feldern
        Vertrag vertrag = new Vertrag();
        vertrag.setMonatlich(true);
        Partner partner = new Partner();
        partner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        vertrag.setPartner(partner);
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setHoechstgeschwindigkeit(200);
        vertrag.setFahrzeug(fahrzeug);
        // Dummy-Werte für weitere Felder können nach Bedarf ergänzt werden.

        // Dummy-Preis-Objekt
        Preis preismodell = new Preis();
        preismodell.setAge(1.0);
        preismodell.setSpeed(1.0);
        preismodell.setFaktor(1.0);

        // Aufruf der Methode
        Vertrag created = vertragsService.vertragAnlegen(vertrag, preismodell, bindingResult);

        // Berechne den erwarteten Preis: alter = aktuelles Jahr - 1990, Preis = (alter + 200) * 1.0
        int alter = LocalDate.now().getYear() - 1990;
        double expectedPrice = alter + 200;
        expectedPrice = Math.round(expectedPrice * 100.0) / 100.0;

        assertNotNull(created.getVsnr(), "Die VSNR sollte gesetzt sein.");
        assertTrue(created.getVsnr() >= 10000000, "Die VSNR sollte >= 10000000 sein.");
        assertEquals(expectedPrice, created.getPreis(), 0.001, "Der berechnete Preis sollte korrekt sein.");

        // Überprüfe, ob der Vertrag in die Liste eingefügt und im Repository gespeichert wurde
        verify(repository).speichereVertrage(vertragsListe);
    }

    // Test vertragAnlegen() - Validierungsfehler
    @Test
    public void testVertragAnlegenValidationError() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        List<Vertrag> vertragsListe = new ArrayList<>();
        when(repository.ladeVertrage()).thenReturn(vertragsListe);
        when(inputValidator.vertragExistiert(anyList(), anyInt())).thenReturn(false);

        Vertrag vertrag = new Vertrag();
        vertrag.setMonatlich(true);
        Partner partner = new Partner();
        partner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        vertrag.setPartner(partner);
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setHoechstgeschwindigkeit(200);
        vertrag.setFahrzeug(fahrzeug);

        Preis preismodell = new Preis();
        preismodell.setAge(1.0);
        preismodell.setSpeed(1.0);
        preismodell.setFaktor(1.0);

        assertThrows(IllegalArgumentException.class, () -> {
            vertragsService.vertragAnlegen(vertrag, preismodell, bindingResult);
        }, "Bei Validierungsfehlern sollte eine IllegalArgumentException geworfen werden.");
    }

    // Test vertragBearbeiten() - Erfolgreiche Bearbeitung
    @Test
    public void testVertragBearbeitenSuccess() {
        int vsnr = 123;
        // Vorhandener Vertrag
        Vertrag existing = new Vertrag();
        existing.setVsnr(vsnr);
        existing.setMonatlich(true);
        Partner partner = new Partner();
        partner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        existing.setPartner(partner);
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setHoechstgeschwindigkeit(200);
        existing.setFahrzeug(fahrzeug);

        List<Vertrag> vertragsListe = new ArrayList<>();
        vertragsListe.add(existing);
        when(repository.ladeVertrage()).thenReturn(vertragsListe);

        // Neue Vertragsdaten (z. B. könnten hier Änderungen vorgenommen werden)
        Vertrag neueDaten = new Vertrag();
        neueDaten.setMonatlich(true);
        Partner neuerPartner = new Partner();
        neuerPartner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        neueDaten.setPartner(neuerPartner);
        Fahrzeug neuesFahrzeug = new Fahrzeug();
        neuesFahrzeug.setHoechstgeschwindigkeit(200);
        neueDaten.setFahrzeug(neuesFahrzeug);

        // Simuliere das Zusammenführen (Merge) der Verträge
        Vertrag merged = new Vertrag();
        merged.setMonatlich(neueDaten.isMonatlich());
        merged.setPartner(neueDaten.getPartner());
        merged.setFahrzeug(neueDaten.getFahrzeug());

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Preis preismodell = new Preis();
        preismodell.setAge(1.0);
        preismodell.setSpeed(1.0);
        preismodell.setFaktor(1.0);

        Vertrag updated = vertragsService.vertragBearbeiten(neueDaten, vsnr, preismodell, bindingResult);

        assertEquals(vsnr, updated.getVsnr(), "Die VSNR des aktualisierten Vertrags sollte unverändert bleiben.");
        int alter = LocalDate.now().getYear() - 1990;
        double expectedPrice = (alter + 200) * 1.0; // Da monatlich true, keine Multiplikation mit 11.
        expectedPrice = Math.round(expectedPrice * 100.0) / 100.0;
        assertEquals(expectedPrice, updated.getPreis(), 0.001, "Der aktualisierte Preis sollte korrekt berechnet werden.");

        verify(repository, atLeastOnce()).speichereVertrage(anyList());
    }

    // Test vertragBearbeiten() - Validierungsfehler
    @Test
    public void testVertragBearbeitenValidationError() {
        int vsnr = 123;
        Vertrag existing = new Vertrag();
        existing.setVsnr(vsnr);
        existing.setMonatlich(true);
        Partner partner = new Partner();
        partner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        existing.setPartner(partner);
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setHoechstgeschwindigkeit(200);
        existing.setFahrzeug(fahrzeug);

        List<Vertrag> vertragsListe = new ArrayList<>();
        vertragsListe.add(existing);

        Vertrag neueDaten = new Vertrag();
        neueDaten.setMonatlich(true);
        Partner neuerPartner = new Partner();
        neuerPartner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        neueDaten.setPartner(neuerPartner);
        Fahrzeug neuesFahrzeug = new Fahrzeug();
        neuesFahrzeug.setHoechstgeschwindigkeit(200);
        neueDaten.setFahrzeug(neuesFahrzeug);

        Vertrag merged = new Vertrag();
        merged.setMonatlich(neueDaten.isMonatlich());
        merged.setPartner(neueDaten.getPartner());
        merged.setFahrzeug(neueDaten.getFahrzeug());

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        Preis preismodell = new Preis();
        preismodell.setAge(1.0);
        preismodell.setSpeed(1.0);
        preismodell.setFaktor(1.0);

        assertThrows(IllegalArgumentException.class, () -> {
            vertragsService.vertragBearbeiten(neueDaten, vsnr, preismodell, bindingResult);
        }, "Bei Validierungsfehlern sollte eine Exception geworfen werden.");
    }

    // Test vertragLoeschen()
    @Test
    public void testVertragLoeschen() {
        int vsnr = 123;
        Vertrag vertrag = new Vertrag();
        vertrag.setVsnr(vsnr);
        List<Vertrag> vertragsListe = new ArrayList<>();
        vertragsListe.add(vertrag);

        vertragsService.vertragLoeschen(vsnr, vertragsListe);
        assertTrue(vertragsListe.stream().noneMatch(v -> v.getVsnr() == vsnr), "Der Vertrag sollte aus der Liste entfernt worden sein.");
        verify(repository).speichereVertrage(vertragsListe);
    }

    // Test createvsnr()
    @Test
    public void testCreatevsnr() {
        Vertrag vertrag1 = new Vertrag();
        vertrag1.setVsnr(10000000);
        Vertrag vertrag2 = new Vertrag();
        vertrag2.setVsnr(10000001);
        List<Vertrag> vertragsListe = new ArrayList<>();
        vertragsListe.add(vertrag1);
        vertragsListe.add(vertrag2);

        when(inputValidator.vertragExistiert(vertragsListe, 10000000)).thenReturn(true);
        when(inputValidator.vertragExistiert(vertragsListe, 10000001)).thenReturn(true);
        when(inputValidator.vertragExistiert(vertragsListe, 10000002)).thenReturn(false);

        int newVsnr = vertragsService.createvsnr(vertragsListe);
        assertEquals(10000002, newVsnr, "Die generierte VSNR sollte 10000002 sein.");
    }

    // Test createPreis() für monatliche Verträge
    @Test
    public void testCreatePreisMonthly() {
        LocalDate geburtsdatum = LocalDate.of(1990, 1, 1);
        int hoechstGeschwindigkeit = 200;
        Preis preismodell = new Preis();
        preismodell.setAge(1.0);
        preismodell.setSpeed(1.0);
        preismodell.setFaktor(1.0);

        int alter = LocalDate.now().getYear() - 1990;
        double expected = (alter * 1.0 + hoechstGeschwindigkeit * 1.0) * 1.0;
        expected = Math.round(expected * 100.0) / 100.0;
        double calculated = vertragsService.createPreis(true, geburtsdatum, hoechstGeschwindigkeit, preismodell);
        assertEquals(expected, calculated, 0.001, "Der berechnete Preis für einen monatlichen Vertrag sollte korrekt sein.");
    }

    // Test createPreis() für nicht-monatliche Verträge
    @Test
    public void testCreatePreisNotMonthly() {
        LocalDate geburtsdatum = LocalDate.of(1990, 1, 1);
        int hoechstGeschwindigkeit = 200;
        Preis preismodell = new Preis();
        preismodell.setAge(1.0);
        preismodell.setSpeed(1.0);
        preismodell.setFaktor(1.0);

        int alter = LocalDate.now().getYear() - 1990;
        double preis = (alter * 1.0 + hoechstGeschwindigkeit * 1.0) * 1.0;
        preis = preis * 11;
        preis = Math.round(preis * 100.0) / 100.0;
        double calculated = vertragsService.createPreis(false, geburtsdatum, hoechstGeschwindigkeit, preismodell);
        assertEquals(preis, calculated, 0.001, "Der berechnete Preis für einen nicht-monatlichen Vertrag sollte korrekt sein.");
    }
}
