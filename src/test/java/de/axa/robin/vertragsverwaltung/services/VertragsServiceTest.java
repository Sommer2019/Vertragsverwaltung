package de.axa.robin.vertragsverwaltung.services;

import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VertragsServiceTest {

    @InjectMocks
    private VertragsService vertragsService;

    @Mock
    private Repository repository;

    // Eine mutable Liste, die von repository.ladeVertrage() zurückgegeben wird
    private List<Vertrag> vertrage;

    @BeforeEach
    public void setUp() {
        // Erstellen einer leeren Liste, die als Rückgabewert für ladeVertrage() dient.
        vertrage = new ArrayList<>();
        when(repository.ladeVertrage()).thenReturn(vertrage);
    }

    @Test
    public void testGetVertrage() {
        // Arrange: Erstelle zwei Beispiel-Verträge
        Vertrag vertrag1 = createVertrag(1, "ABC123");
        Vertrag vertrag2 = createVertrag(2, "DEF456");
        vertrage.add(vertrag1);
        vertrage.add(vertrag2);

        // Act: Aufruf der getVertrage()-Methode
        List<Vertrag> result = vertragsService.getVertrage();

        // Assert: Überprüfe, ob beide Verträge zurückgegeben werden
        assertEquals(2, result.size());
        assertTrue(result.contains(vertrag1));
        assertTrue(result.contains(vertrag2));
    }

    @Test
    public void testGetVertragFound() {
        // Arrange: Füge einen Vertrag mit vsnr=10 zur Liste hinzu
        Vertrag vertrag = createVertrag(10, "GHI789");
        vertrage.add(vertrag);

        // Act: Suche nach einem Vertrag mit vsnr=10
        Vertrag result = vertragsService.getVertrag(10);

        // Assert: Es muss ein Vertrag gefunden werden, der die vsnr 10 hat.
        assertNotNull(result);
        assertEquals(10, result.getVsnr());
    }

    @Test
    public void testGetVertragNotFound() {
        // Act & Assert: Suche nach einem Vertrag, der nicht existiert, und erwarte null.
        Vertrag result = vertragsService.getVertrag(99);
        assertNull(result);
    }

    @Test
    public void testVertragAnlegen() {
        // Arrange: Erstelle einen neuen Vertrag
        Vertrag newVertrag = createVertrag(3, "JKL012");
        // Vorher ist die Liste leer.
        assertTrue(vertragsService.getVertrage().isEmpty());

        // Act: Vertrag anlegen

        // Assert:
        // - Der zurückgegebene Vertrag entspricht dem neuen Vertrag.
        // - Der Vertrag wurde zur Liste hinzugefügt.
        // - repository.speichereVertrage() wurde mit der aktualisierten Liste aufgerufen.
        assertTrue(vertragsService.getVertrage().contains(newVertrag));
        verify(repository).speichereVertrage(vertrage);
    }

    @Test
    public void testVertragLoeschen() {
        // Arrange: Füge zwei Verträge hinzu, von denen einer gelöscht werden soll.
        Vertrag vertragToDelete = createVertrag(5, "MNO345");
        vertrage.add(vertragToDelete);
        vertrage.add(createVertrag(6, "PQR678"));

        // Act: Lösche den Vertrag mit vsnr=5

        // Assert:
        // - Die Methode soll true zurückliefern.
        // - Der Vertrag mit vsnr=5 darf nicht mehr in der Liste enthalten sein.
        // - repository.speichereVertrage() wird aufgerufen.
        assertFalse(vertragsService.getVertrage().stream().anyMatch(v -> v.getVsnr() == 5));
        verify(repository).speichereVertrage(vertrage);
    }
    @Test
    public void testMergeVertrage() {
        // Source-Vertrag mit neuen Werten
        Vertrag source = new Vertrag();
        LocalDate versicherungsbeginn = LocalDate.of(2025, 1, 1);
        LocalDate versicherungsablauf = LocalDate.of(2030, 1, 1);
        LocalDate antragsDatum = LocalDate.of(2024, 12, 1);
        source.setVersicherungsbeginn(versicherungsbeginn);
        source.setVersicherungsablauf(versicherungsablauf);
        source.setAntragsDatum(antragsDatum);

        Partner sourcePartner = getPartner();
        source.setPartner(sourcePartner);

        Fahrzeug sourceFahrzeug = new Fahrzeug();
        sourceFahrzeug.setAmtlichesKennzeichen("ABC123");
        sourceFahrzeug.setHersteller("VW");
        sourceFahrzeug.setTyp("Golf");
        sourceFahrzeug.setHoechstgeschwindigkeit(200);
        sourceFahrzeug.setWagnisskennziffer(5);
        source.setFahrzeug(sourceFahrzeug);

        // Target-Vertrag mit alten Werten
        Vertrag target = getVertrag();

        when(getVertrag()).thenReturn(target);
        Vertrag updated = vertragsService.mergeVertrage(source, target);

        // Überprüfen, ob vsnr und die Datumsfelder aktualisiert wurden
        assertEquals(versicherungsbeginn, updated.getVersicherungsbeginn());
        assertEquals(versicherungsablauf, updated.getVersicherungsablauf());
        assertEquals(antragsDatum, updated.getAntragsDatum());

        // Überprüfen, ob die Partner-Felder aktualisiert wurden
        Partner updatedPartner = updated.getPartner();
        assertEquals("Max", updatedPartner.getVorname());
        assertEquals("Mustermann", updatedPartner.getNachname());
        assertEquals("M", updatedPartner.getGeschlecht());
        assertEquals(LocalDate.of(1985, 5, 5), updatedPartner.getGeburtsdatum());
        assertEquals("Deutschland", updatedPartner.getLand());
        assertEquals("Musterstr", updatedPartner.getStrasse());
        assertEquals("1A", updatedPartner.getHausnummer());
        assertEquals("12345", updatedPartner.getPlz());
        assertEquals("Musterstadt", updatedPartner.getStadt());
        assertEquals("BW", updatedPartner.getBundesland());

        // Überprüfen, ob die Fahrzeug-Felder aktualisiert wurden
        Fahrzeug updatedFahrzeug = updated.getFahrzeug();
        assertEquals("XYZ789", updatedFahrzeug.getAmtlichesKennzeichen());
        assertEquals("VW", updatedFahrzeug.getHersteller());
        assertEquals("3er", updatedFahrzeug.getTyp());
        assertEquals(200, updatedFahrzeug.getHoechstgeschwindigkeit());
        assertEquals(5, updatedFahrzeug.getWagnisskennziffer());

    }

    @NotNull
    private static Vertrag getVertrag() {
        Vertrag target = new Vertrag();
        target.setVersicherungsbeginn(LocalDate.of(2020, 1, 1));
        target.setVersicherungsablauf(LocalDate.of(2025, 1, 1));
        target.setAntragsDatum(LocalDate.of(2020, 12, 12));

        Partner targetPartner = getTargetPartner();
        target.setPartner(targetPartner);

        Fahrzeug targetFahrzeug = new Fahrzeug();
        targetFahrzeug.setAmtlichesKennzeichen("XYZ789");
        targetFahrzeug.setHersteller("BMW");
        targetFahrzeug.setTyp("3er");
        targetFahrzeug.setHoechstgeschwindigkeit(180);
        targetFahrzeug.setWagnisskennziffer(3);
        target.setFahrzeug(targetFahrzeug);
        return target;
    }

    @NotNull
    private static Partner getTargetPartner() {
        Partner targetPartner = new Partner();
        targetPartner.setVorname("Anna");
        targetPartner.setNachname("Beispiel");
        targetPartner.setGeschlecht("F");
        targetPartner.setGeburtsdatum(LocalDate.of(1990, 6, 6));
        targetPartner.setLand("Österreich");
        targetPartner.setStrasse("Beispielstr");
        targetPartner.setHausnummer("2B");
        targetPartner.setPlz("54321");
        targetPartner.setStadt("Beispielstadt");
        targetPartner.setBundesland("Bayern");
        return targetPartner;
    }

    @NotNull
    private static Partner getPartner() {
        Partner sourcePartner = new Partner();
        sourcePartner.setVorname("Max");
        sourcePartner.setNachname("Mustermann");
        sourcePartner.setGeschlecht("M");
        sourcePartner.setGeburtsdatum(LocalDate.of(1985, 5, 5));
        sourcePartner.setLand("Deutschland");
        sourcePartner.setStrasse("Musterstr");
        sourcePartner.setHausnummer("1A");
        sourcePartner.setPlz("12345");
        sourcePartner.setStadt("Musterstadt");
        sourcePartner.setBundesland("BW");
        return sourcePartner;
    }
    /**
     * Hilfsmethode zum Erstellen eines minimalen Vertrag-Objekts.
     * Hierbei wird angenommen, dass:
     * - Vertrag über einen Setter für vsnr und Fahrzeug verfügt.
     * - Fahrzeug einen Setter für das amtliche Kennzeichen hat.
     */
    private Vertrag createVertrag(int vsnr, String kennzeichen) {
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setAmtlichesKennzeichen(kennzeichen);
        Vertrag vertrag = new Vertrag();
        vertrag.setVsnr(vsnr);
        vertrag.setFahrzeug(fahrzeug);
        return vertrag;
    }
}
