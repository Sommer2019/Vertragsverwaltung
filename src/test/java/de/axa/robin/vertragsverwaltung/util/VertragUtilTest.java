package de.axa.robin.vertragsverwaltung.util;

import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class VertragUtilTest {

    @Test
    public void testMergeVertrage_UpdateAllFields() {
        // Arrange: Erzeuge einen "alten" Vertrag mit initialen Werten
        Vertrag vertragOld = new Vertrag();
        vertragOld.setVsnr(10000000);
        vertragOld.setVersicherungsbeginn(LocalDate.of(2022, 1, 1));
        vertragOld.setVersicherungsablauf(LocalDate.of(2023, 1, 1));
        vertragOld.setAntragsDatum(LocalDate.of(2021, 12, 1));

        Fahrzeug oldFahrzeug = new Fahrzeug();
        oldFahrzeug.setHersteller("OldHersteller");
        oldFahrzeug.setHoechstgeschwindigkeit(100);
        oldFahrzeug.setWagnisskennziffer(1);
        vertragOld.setFahrzeug(oldFahrzeug);

        Partner oldPartner = new Partner();
        oldPartner.setVorname("OldVorname");
        oldPartner.setNachname("OldNachname");
        oldPartner.setGeschlecht("M");
        oldPartner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        oldPartner.setLand("OldLand");
        oldPartner.setStadt("OldStadt");
        oldPartner.setStrasse("OldStrasse");
        oldPartner.setHausnummer("1A");
        oldPartner.setPlz("12345");
        oldPartner.setBundesland("OldBundesland");
        vertragOld.setPartner(oldPartner);

        // Erzeuge einen "neuen" Vertrag mit aktualisierten Werten
        Vertrag vertragNew = new Vertrag();
        vertragNew.setVersicherungsbeginn(LocalDate.of(2022, 2, 2));
        vertragNew.setVersicherungsablauf(LocalDate.of(2023, 2, 2));
        vertragNew.setAntragsDatum(LocalDate.of(2021, 11, 30));

        Fahrzeug newFahrzeug = new Fahrzeug();
        newFahrzeug.setHersteller("NewHersteller");
        newFahrzeug.setHoechstgeschwindigkeit(200);
        newFahrzeug.setWagnisskennziffer(2);
        vertragNew.setFahrzeug(newFahrzeug);

        Partner newPartner = new Partner();
        newPartner.setVorname("NewVorname");
        newPartner.setNachname("NewNachname");
        newPartner.setGeschlecht("F");
        newPartner.setGeburtsdatum(LocalDate.of(1995, 5, 5));
        newPartner.setLand("NewLand");
        newPartner.setStadt("NewStadt");
        newPartner.setStrasse("NewStrasse");
        newPartner.setHausnummer("2B");
        newPartner.setPlz("54321");
        newPartner.setBundesland("NewBundesland");
        vertragNew.setPartner(newPartner);

        VertragUtil vertragUtil = new VertragUtil();

        // Act: Merge der neuen Werte in den alten Vertrag
        Vertrag result = vertragUtil.mergeVertrage(vertragNew, vertragOld);

        // Assert: Alle Felder müssen entsprechend aktualisiert worden sein
        assertEquals(LocalDate.of(2022, 2, 2), result.getVersicherungsbeginn());
        assertEquals(LocalDate.of(2023, 2, 2), result.getVersicherungsablauf());
        assertEquals(LocalDate.of(2021, 11, 30), result.getAntragsDatum());

        // Fahrzeug-Attribute
        assertNotNull(result.getFahrzeug());
        assertEquals("NewHersteller", result.getFahrzeug().getHersteller());
        assertEquals(200, result.getFahrzeug().getHoechstgeschwindigkeit());
        assertEquals(2, result.getFahrzeug().getWagnisskennziffer());

        // Partner-Attribute
        assertNotNull(result.getPartner());
        assertEquals("NewVorname", result.getPartner().getVorname());
        assertEquals("NewNachname", result.getPartner().getNachname());
        assertEquals("F", result.getPartner().getGeschlecht());
        assertEquals(LocalDate.of(1995, 5, 5), result.getPartner().getGeburtsdatum());
        assertEquals("NewLand", result.getPartner().getLand());
        assertEquals("NewStadt", result.getPartner().getStadt());
        assertEquals("NewStrasse", result.getPartner().getStrasse());
        assertEquals("2B", result.getPartner().getHausnummer());
        assertEquals("54321", result.getPartner().getPlz());
        assertEquals("NewBundesland", result.getPartner().getBundesland());
    }

    @Test
    public void testMergeVertrage_PartialUpdate() {
        // Arrange: Erzeuge einen alten Vertrag mit initialen Werten
        Vertrag vertragOld = new Vertrag();
        vertragOld.setVsnr(10000000);
        vertragOld.setVersicherungsbeginn(LocalDate.of(2022, 1, 1));
        vertragOld.setVersicherungsablauf(LocalDate.of(2023, 1, 1));
        vertragOld.setAntragsDatum(LocalDate.of(2021, 12, 1));

        Fahrzeug oldFahrzeug = new Fahrzeug();
        oldFahrzeug.setHersteller("OldHersteller");
        oldFahrzeug.setHoechstgeschwindigkeit(150);
        oldFahrzeug.setWagnisskennziffer(3);
        vertragOld.setFahrzeug(oldFahrzeug);

        Partner oldPartner = new Partner();
        oldPartner.setVorname("OldVorname");
        oldPartner.setNachname("OldNachname");
        oldPartner.setGeschlecht("M");
        oldPartner.setGeburtsdatum(LocalDate.of(1985, 3, 3));
        oldPartner.setLand("OldLand");
        oldPartner.setStadt("OldStadt");
        oldPartner.setStrasse("OldStrasse");
        oldPartner.setHausnummer("10");
        oldPartner.setPlz("11111");
        oldPartner.setBundesland("OldBundesland");
        vertragOld.setPartner(oldPartner);

        // Erzeuge einen neuen Vertrag, der nur teilweise aktualisiert
        Vertrag vertragNew = new Vertrag();
        // Nur Versicherungsbeginn wird aktualisiert
        vertragNew.setVersicherungsbeginn(LocalDate.of(2022, 5, 5));
        // Versicherungsablauf und AntragsDatum bleiben null

        Fahrzeug newFahrzeug = new Fahrzeug();
        newFahrzeug.setHersteller("NewHersteller");
        // Hoechstgeschwindigkeit und Wagnisskennziffer bleiben bei 0 (Standardwert)
        vertragNew.setFahrzeug(newFahrzeug);

        Partner newPartner = new Partner();
        // Keine Partnerfelder gesetzt
        vertragNew.setPartner(newPartner);

        VertragUtil vertragUtil = new VertragUtil();

        // Act: Merge der neuen Werte in den alten Vertrag
        Vertrag result = vertragUtil.mergeVertrage(vertragNew, vertragOld);

        // Assert: Es sollte nur der Versicherungsbeginn und der Fahrzeughersteller aktualisiert werden
        assertEquals(LocalDate.of(2022, 5, 5), result.getVersicherungsbeginn());
        // Unverändert
        assertEquals(LocalDate.of(2023, 1, 1), result.getVersicherungsablauf());
        assertEquals(LocalDate.of(2021, 12, 1), result.getAntragsDatum());

        // Fahrzeug: Nur der Hersteller wird geändert
        assertNotNull(result.getFahrzeug());
        assertEquals("NewHersteller", result.getFahrzeug().getHersteller());
        // Unverändert: Hoechstgeschwindigkeit und Wagnisskennziffer
        assertEquals(150, result.getFahrzeug().getHoechstgeschwindigkeit());
        assertEquals(3, result.getFahrzeug().getWagnisskennziffer());

        // Partner: Alle Felder bleiben unverändert
        assertNotNull(result.getPartner());
        assertEquals("OldVorname", result.getPartner().getVorname());
        assertEquals("OldNachname", result.getPartner().getNachname());
        assertEquals("M", result.getPartner().getGeschlecht());
        assertEquals(LocalDate.of(1985, 3, 3), result.getPartner().getGeburtsdatum());
        assertEquals("OldLand", result.getPartner().getLand());
        assertEquals("OldStadt", result.getPartner().getStadt());
        assertEquals("OldStrasse", result.getPartner().getStrasse());
        assertEquals("10", result.getPartner().getHausnummer());
        assertEquals("11111", result.getPartner().getPlz());
        assertEquals("OldBundesland", result.getPartner().getBundesland());
    }
}
