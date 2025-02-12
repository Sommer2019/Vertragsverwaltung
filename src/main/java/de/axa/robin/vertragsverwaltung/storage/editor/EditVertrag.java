package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.VertragsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class provides methods to edit an existing insurance contract (Vertrag).
 */
@Component
public class EditVertrag {
    private static final Logger logger = LoggerFactory.getLogger(EditVertrag.class);

    @Autowired
    private VertragsService vertragsService;

    /**
     * Edits an existing insurance contract (Vertrag) identified by the given vsnr.
     * Updates the fields of the existing contract with the values from the provided contract.
     *
     * @param vertrag the contract containing the new values
     * @param vsnr    the insurance contract number of the contract to be edited
     * @return the updated contract, or null if no contract with the given vsnr exists
     */
    public Vertrag editVertrag(Vertrag vertrag, int vsnr) {
        logger.info("Editing contract with VSNR: {}", vsnr);
        Vertrag old = vertragsService.getVertrag(vsnr);
        if (old != null) {
            logger.info("Found existing contract: {}", old);
            if (vertrag.getVersicherungsbeginn() != null) {
                old.setVersicherungsbeginn(vertrag.getVersicherungsbeginn());
                logger.debug("Updated insurance start date to: {}", vertrag.getVersicherungsbeginn());
            }
            if (vertrag.getVersicherungsablauf() != null) {
                old.setVersicherungsablauf(vertrag.getVersicherungsablauf());
                logger.debug("Updated insurance expiry date to: {}", vertrag.getVersicherungsablauf());
            }
            if (vertrag.getAntragsDatum() != null) {
                old.setAntragsDatum(vertrag.getAntragsDatum());
                logger.debug("Updated application date to: {}", vertrag.getAntragsDatum());
            }
            if (vertrag.getFahrzeug() != null) {
                Fahrzeug f = vertrag.getFahrzeug();
                if (f.getHersteller() != null) {
                    old.getFahrzeug().setHersteller(f.getHersteller());
                    logger.debug("Updated vehicle manufacturer to: {}", f.getHersteller());
                }
                if (f.getHoechstgeschwindigkeit() != 0) {
                    old.getFahrzeug().setHoechstgeschwindigkeit(f.getHoechstgeschwindigkeit());
                    logger.debug("Updated vehicle speed to: {}", f.getHoechstgeschwindigkeit());
                }
                if (f.getWagnisskennziffer() != 0) {
                    old.getFahrzeug().setWagnisskennziffer(f.getWagnisskennziffer());
                    logger.debug("Updated vehicle risk number to: {}", f.getWagnisskennziffer());
                }
            }
            if (vertrag.getPartner() != null) {
                Partner p = vertrag.getPartner();
                if (p.getVorname() != null) {
                    old.getPartner().setVorname(p.getVorname());
                    logger.debug("Updated partner first name to: {}", p.getVorname());
                }
                if (p.getNachname() != null) {
                    old.getPartner().setNachname(p.getNachname());
                    logger.debug("Updated partner last name to: {}", p.getNachname());
                }
                if (p.getGeschlecht() != null) {
                    old.getPartner().setGeschlecht(p.getGeschlecht());
                    logger.debug("Updated partner gender to: {}", p.getGeschlecht());
                }
                if (p.getGeburtsdatum() != null) {
                    old.getPartner().setGeburtsdatum(p.getGeburtsdatum());
                    logger.debug("Updated partner birth date to: {}", p.getGeburtsdatum());
                }
                if (p.getLand() != null) {
                    old.getPartner().setLand(p.getLand());
                    logger.debug("Updated partner country to: {}", p.getLand());
                }
                if (p.getStadt() != null) {
                    old.getPartner().setStadt(p.getStadt());
                    logger.debug("Updated partner city to: {}", p.getStadt());
                }
                if (p.getStrasse() != null) {
                    old.getPartner().setStrasse(p.getStrasse());
                    logger.debug("Updated partner street to: {}", p.getStrasse());
                }
                if (p.getHausnummer() != null) {
                    old.getPartner().setHausnummer(p.getHausnummer());
                    logger.debug("Updated partner house number to: {}", p.getHausnummer());
                }
                if (p.getPlz() != null) {
                    old.getPartner().setPlz(p.getPlz());
                    logger.debug("Updated partner postal code to: {}", p.getPlz());
                }
                if (p.getBundesland() != null) {
                    old.getPartner().setBundesland(p.getBundesland());
                    logger.debug("Updated partner state to: {}", p.getBundesland());
                }
            }
            vertragsService.vertragLoeschen(vsnr);
            vertragsService.vertragAnlegen(old);
            logger.info("Contract successfully updated and saved: {}", old);
        } else {
            logger.warn("No contract found with VSNR: {}", vsnr);
        }
        return old;
    }
}