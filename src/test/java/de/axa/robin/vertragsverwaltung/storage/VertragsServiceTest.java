package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VertragsServiceTest {

    @InjectMocks
    private VertragsService verwaltung;

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
        List<Vertrag> result = verwaltung.getVertrage();

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
        Vertrag result = verwaltung.getVertrag(10);

        // Assert: Es muss ein Vertrag gefunden werden, der die vsnr 10 hat.
        assertNotNull(result);
        assertEquals(10, result.getVsnr());
    }

    @Test
    public void testGetVertragNotFound() {
        // Act & Assert: Suche nach einem Vertrag, der nicht existiert, und erwarte null.
        Vertrag result = verwaltung.getVertrag(99);
        assertNull(result);
    }

    @Test
    public void testVertragAnlegen() {
        // Arrange: Erstelle einen neuen Vertrag
        Vertrag newVertrag = createVertrag(3, "JKL012");
        // Vorher ist die Liste leer.
        assertTrue(verwaltung.getVertrage().isEmpty());

        // Act: Vertrag anlegen
        Vertrag result = verwaltung.vertragAnlegen(newVertrag);

        // Assert:
        // - Der zurückgegebene Vertrag entspricht dem neuen Vertrag.
        // - Der Vertrag wurde zur Liste hinzugefügt.
        // - repository.speichereVertrage() wurde mit der aktualisierten Liste aufgerufen.
        assertEquals(newVertrag, result);
        assertTrue(verwaltung.getVertrage().contains(newVertrag));
        verify(repository).speichereVertrage(vertrage);
    }

    @Test
    public void testVertragLoeschen() {
        // Arrange: Füge zwei Verträge hinzu, von denen einer gelöscht werden soll.
        Vertrag vertragToDelete = createVertrag(5, "MNO345");
        vertrage.add(vertragToDelete);
        vertrage.add(createVertrag(6, "PQR678"));

        // Act: Lösche den Vertrag mit vsnr=5
        boolean removed = verwaltung.vertragLoeschen(5);

        // Assert:
        // - Die Methode soll true zurückliefern.
        // - Der Vertrag mit vsnr=5 darf nicht mehr in der Liste enthalten sein.
        // - repository.speichereVertrage() wird aufgerufen.
        assertTrue(removed);
        assertFalse(verwaltung.getVertrage().stream().anyMatch(v -> v.getVsnr() == 5));
        verify(repository).speichereVertrage(vertrage);
    }

    @Test
    public void testVertragExistiert() {
        // Arrange: Füge einen Vertrag mit vsnr=7 hinzu
        Vertrag vertrag = createVertrag(7, "STU901");
        vertrage.add(vertrag);

        // Act & Assert: Überprüfe, ob vertragExistiert(7) true und vertragExistiert(8) false liefert.
        assertTrue(verwaltung.vertragExistiert(7));
        assertFalse(verwaltung.vertragExistiert(8));
    }

    @Test
    public void testKennzeichenExistiert() {
        // Arrange: Füge einen Vertrag mit einem bestimmten amtlichen Kennzeichen hinzu.
        Vertrag vertrag = createVertrag(8, "VWX234");
        vertrage.add(vertrag);

        // Act & Assert: Überprüfe, ob das Kennzeichen in der Liste gefunden wird.
        assertTrue(verwaltung.kennzeichenExistiert("VWX234"));
        assertFalse(verwaltung.kennzeichenExistiert("ZZZ999"));
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
