package de.axa.robin.vertragsverwaltung.backend.storage.editor;

import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EditTest {

    private Edit edit;
    private Vertragsverwaltung vertragsverwaltung;

    @BeforeEach
    public void setUp() {
        vertragsverwaltung = mock(Vertragsverwaltung.class);
        edit = new Edit();
    }

    @Test
    public void testRecalcpricerun() {
        List<Vertrag> vertrage = new ArrayList<>();
        Vertrag vertrag1 = new Vertrag();
        Partner partner1 = new Partner();
        Fahrzeug fahrzeug1 = new Fahrzeug();
        vertrag1.setPartner(partner1);
        vertrag1.setFahrzeug(fahrzeug1);
        vertrag1.setMonatlich(false);
        vertrag1.setPreis(100);
        vertrag1.setVersicherungsablauf(LocalDate.now().plusDays(1));
        vertrag1.setVsnr(1);
        vertrag1.getPartner().setGeburtsdatum(LocalDate.now().minusDays(1));
        vertrag1.getFahrzeug().setHoechstgeschwindigkeit(100);
        vertrage.add(vertrag1);

        Vertrag vertrag2 = new Vertrag();
        Partner partner2 = new Partner();
        Fahrzeug fahrzeug2 = new Fahrzeug();
        vertrag2.setPartner(partner2);
        vertrag2.setFahrzeug(fahrzeug2);
        vertrag2.setMonatlich(true);
        vertrag2.setPreis(50);
        vertrag2.setVersicherungsablauf(LocalDate.now().plusDays(1));
        vertrag2.setVsnr(2);
        vertrag2.getPartner().setGeburtsdatum(LocalDate.now().minusDays(1));
        vertrag2.getFahrzeug().setHoechstgeschwindigkeit(100);
        vertrage.add(vertrag2);
        double factor = 1.0;
        double factoralter = 1.0;
        double factorspeed = 1.0;

        // Calculate expected prices
        int age1 = Period.between(vertrag1.getPartner().getGeburtsdatum(), LocalDate.now()).getYears();
        BigDecimal expectedPrice1 = BigDecimal.valueOf((age1 * factoralter + fahrzeug1.getHoechstgeschwindigkeit() * factorspeed) * factor * 11);

        int age2 = Period.between(vertrag2.getPartner().getGeburtsdatum(), LocalDate.now()).getYears();
        BigDecimal expectedPrice2 = BigDecimal.valueOf((age2 * factoralter + fahrzeug2.getHoechstgeschwindigkeit() * factorspeed) * factor);

        BigDecimal expectedSum = expectedPrice1.add(expectedPrice2.multiply(BigDecimal.valueOf(12)));

        BigDecimal result = edit.recalcPrice(factor, factoralter, factorspeed, vertrage);

        assertEquals(expectedSum.setScale(2, RoundingMode.HALF_DOWN), result.setScale(2, RoundingMode.HALF_DOWN));

        InOrder inOrder = inOrder(vertragsverwaltung);
        inOrder.verify(vertragsverwaltung).vertragLoeschen(1);
        inOrder.verify(vertragsverwaltung).vertragLoeschen(2);

        verify(vertragsverwaltung, times(2)).vertragAnlegen(any(Vertrag.class));
    }
}