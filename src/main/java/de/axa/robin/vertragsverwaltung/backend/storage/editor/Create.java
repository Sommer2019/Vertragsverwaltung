package de.axa.robin.vertragsverwaltung.backend.storage.editor;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;

import java.time.LocalDate;
import java.util.List;

public class Create {
    ////Klassen einlesen////
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung;
    private final Repository repository = new Repository(setup);

    public Create(Vertragsverwaltung vertragsverwaltung) {
        this.vertragsverwaltung = vertragsverwaltung;
    }

    public int createvsnr() {
        int vsnr = 10000000;
        while (vertragsverwaltung.getVertrag(vsnr) != null) {
            vsnr++;
        }
        if (vsnr > 99999999) {
            System.err.println("Keine freien Versicherungsnummern mehr!");
            throw new IllegalStateException("Keine freien Versicherungsnummern mehr!");
        }
        return vsnr;
    }

    public double createPreis(boolean monatlich, Partner partner, Fahrzeug fahrzeug) {
        double preis = 0;
        int alter = LocalDate.now().getYear() - partner.getGeburtsdatum().getYear();
        List<Double> faktoren = repository.ladeFaktoren();
        try {
            preis = (alter * faktoren.get(2) + fahrzeug.getHoechstgeschwindigkeit() * faktoren.get(3)) * faktoren.get(1);
            if (!monatlich) {
                preis = preis * 11;
            }
        } catch (Exception e) {
            System.err.println("Ungültige Eingabe!");
        }
        return Math.round(preis * 100.0) / 100.0;
    }

    public double createVertragtoSave(Vertrag vertrag, boolean monatlich, int vsnr) {
        Partner partner = new Partner(vertrag.getPartner().getVorname(), vertrag.getPartner().getNachname(), vertrag.getPartner().getGeschlecht(), vertrag.getPartner().getGeburtsdatum(), vertrag.getPartner().getLand(), vertrag.getPartner().getStrasse(), vertrag.getPartner().getHausnummer(), vertrag.getPartner().getPlz(), vertrag.getPartner().getStadt(), vertrag.getPartner().getBundesland());
        Fahrzeug fahrzeug = new Fahrzeug(vertrag.getFahrzeug().getAmtlichesKennzeichen(), vertrag.getFahrzeug().getHersteller(), vertrag.getFahrzeug().getTyp(), vertrag.getFahrzeug().getHoechstgeschwindigkeit(), vertrag.getFahrzeug().getWagnisskennziffer());
        double preis = createPreis(monatlich, partner, fahrzeug);
        Vertrag vertragsave = new Vertrag(vsnr, monatlich, preis, vertrag.getVersicherungsbeginn(), vertrag.getVersicherungsablauf(), vertrag.getAntragsDatum(), fahrzeug, partner);
        vertragsverwaltung.vertragAnlegen(vertragsave);
        return preis;
    }
}
