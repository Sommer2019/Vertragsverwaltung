package de.axa.robin.vertragsverwaltung.backend.storage.editor;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class Edit {
    /// /Klassen einlesen////
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung;
    private final Repository repository = new Repository(setup);
    private final Create create;
    public Edit(Vertragsverwaltung vertragsverwaltung) {
        this.vertragsverwaltung = vertragsverwaltung;
        this.create = new Create(vertragsverwaltung);
    }
    public BigDecimal recalcpricerun(double factor, double factoralter, double factorspeed, List<Vertrag> vertrage) {
        repository.speichereFaktoren(factor,factoralter,factorspeed);
        BigDecimal summe = BigDecimal.ZERO;
        for (Vertrag v : vertrage) {
            v.setPreis(create.createPreis(v.isMonatlich(), v.getPartner(), v.getFahrzeug()));
            if (!v.getVersicherungsablauf().isBefore(LocalDate.now())){
                if (!v.isMonatlich()) {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis()));
                } else {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis() * 12));
                }
            }
            vertragsverwaltung.vertragLoeschen(v.getVsnr());
            vertragsverwaltung.vertragAnlegen(v);
        }
        System.out.println("Neue Summe aller Beiträge im Jahr: " + summe.setScale(2, RoundingMode.HALF_DOWN) + "€");
        return summe;
    }
    public Vertrag updateVertragFields(Vertrag source, Vertrag target) {
        if (source.getVsnr() != 0) {
            target.setVsnr(source.getVsnr());
        }
        if (source.getVersicherungsbeginn() != null && !source.getVersicherungsbeginn().toString().equals("nullnull")) {
            target.setVersicherungsbeginn(source.getVersicherungsbeginn());
        }
        if (source.getVersicherungsablauf() != null && !source.getVersicherungsablauf().toString().equals("nullnull")) {
            target.setVersicherungsablauf(source.getVersicherungsablauf());
        }
        if (source.getAntragsDatum() != null && !source.getAntragsDatum().toString().equals("nullnull")) {
            target.setAntragsDatum(source.getAntragsDatum());
        }
        if (source.getPartner() != null) {
            Partner sourcePartner = source.getPartner();
            Partner targetPartner = target.getPartner();
            if (sourcePartner.getVorname() != null && !sourcePartner.getVorname().isEmpty()) {
                targetPartner.setVorname(sourcePartner.getVorname());
            }
            if (sourcePartner.getNachname() != null && !sourcePartner.getNachname().isEmpty()) {
                targetPartner.setNachname(sourcePartner.getNachname());
            }
            if (sourcePartner.getGeschlecht()!=' ') {
                targetPartner.setGeschlecht(sourcePartner.getGeschlecht());
            }
            if (sourcePartner.getGeburtsdatum() != null && !sourcePartner.getGeburtsdatum().toString().equals("nullnull")) {
                targetPartner.setGeburtsdatum(sourcePartner.getGeburtsdatum());
            }
            if (sourcePartner.getLand() != null && !sourcePartner.getLand().isEmpty()) {
                targetPartner.setLand(sourcePartner.getLand());
            }
            if (sourcePartner.getStrasse() != null && !sourcePartner.getStrasse().isEmpty()) {
                targetPartner.setStrasse(sourcePartner.getStrasse());
            }
            if (sourcePartner.getHausnummer() != null && !sourcePartner.getHausnummer().isEmpty()) {
                targetPartner.setHausnummer(sourcePartner.getHausnummer());
            }
            if (sourcePartner.getPlz() != null && !sourcePartner.getPlz().isEmpty()) {
                targetPartner.setPlz(sourcePartner.getPlz());
            }
            if (sourcePartner.getStadt() != null && !sourcePartner.getStadt().isEmpty()) {
                targetPartner.setStadt(sourcePartner.getStadt());
            }
            if (sourcePartner.getBundesland() != null && !sourcePartner.getBundesland().isEmpty()) {
                targetPartner.setBundesland(sourcePartner.getBundesland());
            }
        }
        if (source.getFahrzeug() != null) {
            Fahrzeug sourceFahrzeug = source.getFahrzeug();
            Fahrzeug targetFahrzeug = target.getFahrzeug();
            if (sourceFahrzeug.getAmtlichesKennzeichen() != null && !sourceFahrzeug.getAmtlichesKennzeichen().isEmpty()) {
                targetFahrzeug.setAmtlichesKennzeichen(sourceFahrzeug.getAmtlichesKennzeichen());
            }
            if (sourceFahrzeug.getHersteller() != null && !sourceFahrzeug.getHersteller().isEmpty()) {
                targetFahrzeug.setHersteller(sourceFahrzeug.getHersteller());
            }
            if (sourceFahrzeug.getTyp() != null && !sourceFahrzeug.getTyp().isEmpty()) {
                targetFahrzeug.setTyp(sourceFahrzeug.getTyp());
            }
            if (sourceFahrzeug.getHoechstgeschwindigkeit() != 0) {
                targetFahrzeug.setHoechstgeschwindigkeit(sourceFahrzeug.getHoechstgeschwindigkeit());
            }
            if (sourceFahrzeug.getWagnisskennziffer() != 0) {
                targetFahrzeug.setWagnisskennziffer(sourceFahrzeug.getWagnisskennziffer());
            }
        }
        return target;
    }
}