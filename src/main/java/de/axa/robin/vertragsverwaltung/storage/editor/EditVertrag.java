package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class provides methods to edit an existing insurance contract (Vertrag).
 */
@Component
public class EditVertrag {
    @Autowired
    private Vertragsverwaltung vertragsverwaltung;

    /**
     * Edits an existing insurance contract (Vertrag) identified by the given vsnr.
     * Updates the fields of the existing contract with the values from the provided contract.
     *
     * @param vertrag the contract containing the new values
     * @param vsnr the insurance contract number of the contract to be edited
     * @return the updated contract, or null if no contract with the given vsnr exists
     */
    public Vertrag editVertrag(Vertrag vertrag, int vsnr) {
        Vertrag old = vertragsverwaltung.getVertrag(vsnr);
        if (old != null) {
            if (vertrag.getVersicherungsbeginn() != null) {
                old.setVersicherungsbeginn(vertrag.getVersicherungsbeginn());
            }
            if (vertrag.getVersicherungsablauf() != null) {
                old.setVersicherungsablauf(vertrag.getVersicherungsablauf());
            }
            if (vertrag.getAntragsDatum() != null) {
                old.setAntragsDatum(vertrag.getAntragsDatum());
            }
            if (vertrag.getFahrzeug() != null) {
                Fahrzeug f = vertrag.getFahrzeug();
                if (f.getHersteller() != null) {
                    old.getFahrzeug().setHersteller(f.getHersteller());
                }
                if (f.getHoechstgeschwindigkeit() != 0) {
                    old.getFahrzeug().setHoechstgeschwindigkeit(f.getHoechstgeschwindigkeit());
                }
                if (f.getWagnisskennziffer() != 0) {
                    old.getFahrzeug().setWagnisskennziffer(f.getWagnisskennziffer());
                }
            }
            if (vertrag.getPartner() != null) {
                Partner p = vertrag.getPartner();
                if (p.getVorname() != null) {
                    old.getPartner().setVorname(p.getVorname());
                }
                if (p.getNachname() != null) {
                    old.getPartner().setNachname(p.getNachname());
                }
                if (p.getGeschlecht() != null) {
                    old.getPartner().setGeschlecht(p.getGeschlecht());
                }
                if (p.getGeburtsdatum() != null) {
                    old.getPartner().setGeburtsdatum(p.getGeburtsdatum());
                }
                if (p.getLand() != null) {
                    old.getPartner().setLand(p.getLand());
                }
                if (p.getStadt() != null) {
                    old.getPartner().setStadt(p.getStadt());
                }
                if (p.getStrasse() != null) {
                    old.getPartner().setStrasse(p.getStrasse());
                }
                if (p.getHausnummer() != null) {
                    old.getPartner().setHausnummer(p.getHausnummer());
                }
                if (p.getPlz() != null) {
                    old.getPartner().setPlz(p.getPlz());
                }
                if (p.getBundesland() != null) {
                    old.getPartner().setBundesland(p.getBundesland());
                }
                if (p.getLand() != null) {
                    old.getPartner().setLand(p.getLand());
                }
            }
            vertragsverwaltung.vertragLoeschen(vsnr);
            vertragsverwaltung.vertragAnlegen(old);
        }
        return old;
    }
}