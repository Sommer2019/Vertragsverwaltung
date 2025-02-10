package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditVertragTest {

    @InjectMocks
    private EditVertrag editVertrag;

    @Test
    public void testUpdateVertragFields() {
        // Source-Vertrag mit neuen Werten
        Vertrag source = new Vertrag();
        source.setVsnr(123);
        LocalDate versicherungsbeginn = LocalDate.of(2025, 1, 1);
        LocalDate versicherungsablauf = LocalDate.of(2030, 1, 1);
        LocalDate antragsDatum = LocalDate.of(2024, 12, 1);
        source.setVersicherungsbeginn(versicherungsbeginn);
        source.setVersicherungsablauf(versicherungsablauf);
        source.setAntragsDatum(antragsDatum);

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
        source.setPartner(sourcePartner);

        Fahrzeug sourceFahrzeug = new Fahrzeug();
        sourceFahrzeug.setAmtlichesKennzeichen("ABC123");
        sourceFahrzeug.setHersteller("VW");
        sourceFahrzeug.setTyp("Golf");
        sourceFahrzeug.setHoechstgeschwindigkeit(200);
        sourceFahrzeug.setWagnisskennziffer(5);
        source.setFahrzeug(sourceFahrzeug);

        // Target-Vertrag mit alten Werten
        Vertrag target = new Vertrag();
        target.setVsnr(0); // Wird nur aktualisiert, wenn source.vsnr != 0
        target.setVersicherungsbeginn(LocalDate.of(2020, 1, 1));
        target.setVersicherungsablauf(LocalDate.of(2025, 1, 1));
        target.setAntragsDatum(LocalDate.of(2020, 12, 12));

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
        target.setPartner(targetPartner);

        Fahrzeug targetFahrzeug = new Fahrzeug();
        targetFahrzeug.setAmtlichesKennzeichen("XYZ789");
        targetFahrzeug.setHersteller("BMW");
        targetFahrzeug.setTyp("3er");
        targetFahrzeug.setHoechstgeschwindigkeit(180);
        targetFahrzeug.setWagnisskennziffer(3);
        target.setFahrzeug(targetFahrzeug);

        Vertrag updated = editVertrag.editVertrag(source, target.getVsnr());

        // Überprüfen, ob vsnr und die Datumsfelder aktualisiert wurden
        assertEquals(123, updated.getVsnr());
        assertEquals(versicherungsbeginn, updated.getVersicherungsbeginn());
        assertEquals(versicherungsablauf, updated.getVersicherungsablauf());
        assertEquals(antragsDatum, updated.getAntragsDatum());

        // Überprüfen, ob die Partner-Felder aktualisiert wurden
        Partner updatedPartner = updated.getPartner();
        assertEquals("Max", updatedPartner.getVorname());
        assertEquals("Mustermann", updatedPartner.getNachname());
        // Da die Methode updatePartnerFields das Geschlecht des Targets nicht aktualisiert, erwarten wir "F"
        assertEquals("F", updatedPartner.getGeschlecht());
        assertEquals(LocalDate.of(1985, 5, 5), updatedPartner.getGeburtsdatum());
        assertEquals("Deutschland", updatedPartner.getLand());
        assertEquals("Musterstr", updatedPartner.getStrasse());
        assertEquals("1A", updatedPartner.getHausnummer());
        assertEquals("12345", updatedPartner.getPlz());
        assertEquals("Musterstadt", updatedPartner.getStadt());
        assertEquals("BW", updatedPartner.getBundesland());

        // Überprüfen, ob die Fahrzeug-Felder aktualisiert wurden
        Fahrzeug updatedFahrzeug = updated.getFahrzeug();
        assertEquals("ABC123", updatedFahrzeug.getAmtlichesKennzeichen());
        assertEquals("VW", updatedFahrzeug.getHersteller());
        assertEquals("Golf", updatedFahrzeug.getTyp());
        assertEquals(200, updatedFahrzeug.getHoechstgeschwindigkeit());
        assertEquals(5, updatedFahrzeug.getWagnisskennziffer());
    }
}
