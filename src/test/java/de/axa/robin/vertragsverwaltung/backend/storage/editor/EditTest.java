package de.axa.robin.vertragsverwaltung.backend.storage.editor;

import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EditTest {

    @InjectMocks
    private Edit edit;

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private Repository repository;

    @Mock
    private Create create;

    @Test
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
        when(create.createPreis(eq(false), any(LocalDate.class), anyInt())).thenReturn(100.0);
        when(create.createPreis(eq(true), any(LocalDate.class), anyInt())).thenReturn(50.0);

        BigDecimal result = edit.recalcPrice(1.1, 1.2, 1.3, vertrage);

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

        Vertrag updated = edit.updateVertragFields(source, target);

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
