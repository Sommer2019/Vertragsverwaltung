package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.VertragsService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EditVertragTest {

    @Mock
    private VertragsService vertragsService;

    @InjectMocks
    private EditVertrag editVertrag;

    @Test
    public void testUpdateVertragFields() {
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

        when(vertragsService.getVertrag(target.getVsnr())).thenReturn(target);
        Vertrag updated = editVertrag.editVertrag(source, target.getVsnr());

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

        verify(vertragsService).vertragLoeschen(target.getVsnr());
        verify(vertragsService).vertragAnlegen(updated);
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
}
