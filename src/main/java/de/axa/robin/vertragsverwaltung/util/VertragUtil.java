package de.axa.robin.vertragsverwaltung.util;

import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling contract-related operations.
 */
public final class VertragUtil {
    private static final Logger logger = LoggerFactory.getLogger(VertragUtil.class);

    /**
     * Merges the details of two contracts.
     *
     * @param vertrag    the contract containing the new values
     * @param vertragold the contract to be updated
     * @return the updated contract
     */
    public static Vertrag mergeVertrage(Vertrag vertragold, Vertrag vertrag) {
        logger.info("Editing contract with VSNR: {}", vertragold.getVsnr());

        if (vertrag.getVersicherungsbeginn() != null) {
            vertragold.setVersicherungsbeginn(vertrag.getVersicherungsbeginn());
            logger.debug("Updated insurance start date to: {}", vertrag.getVersicherungsbeginn());
        }
        if (vertrag.getVersicherungsablauf() != null) {
            vertragold.setVersicherungsablauf(vertrag.getVersicherungsablauf());
            logger.debug("Updated insurance expiry date to: {}", vertrag.getVersicherungsablauf());
        }
        if (vertrag.getAntragsDatum() != null) {
            vertragold.setAntragsDatum(vertrag.getAntragsDatum());
            logger.debug("Updated application date to: {}", vertrag.getAntragsDatum());
        }

        if (vertrag.getFahrzeug() != null) {
            Fahrzeug f = vertrag.getFahrzeug();
            Fahrzeug fOld = vertragold.getFahrzeug();
            if (fOld == null) {
                fOld = new Fahrzeug();
                vertragold.setFahrzeug(fOld);
            }
            if (f.getAmtlichesKennzeichen() != null) {
                fOld.setAmtlichesKennzeichen(f.getAmtlichesKennzeichen());
                logger.debug("Updated vehicle license plate to: {}", f.getAmtlichesKennzeichen());
            }
            if (f.getHersteller() != null) {
                fOld.setHersteller(f.getHersteller());
                logger.debug("Updated vehicle manufacturer to: {}", f.getHersteller());
            }
            if (f.getTyp() != null) {
                fOld.setTyp(f.getTyp());
                logger.debug("Updated vehicle type to: {}", f.getTyp());
            }
            if (f.getHoechstgeschwindigkeit() != 0) {
                fOld.setHoechstgeschwindigkeit(f.getHoechstgeschwindigkeit());
                logger.debug("Updated vehicle speed to: {}", f.getHoechstgeschwindigkeit());
            }
            if (f.getWagnisskennziffer() != 0) {
                fOld.setWagnisskennziffer(f.getWagnisskennziffer());
                logger.debug("Updated vehicle risk number to: {}", f.getWagnisskennziffer());
            }
        }

        if (vertrag.getPartner() != null) {
            Partner p = vertrag.getPartner();
            Partner pOld = vertragold.getPartner();
            if (pOld == null) {
                pOld = new Partner();
                vertragold.setPartner(pOld);
            }
            if (p.getVorname() != null) {
                pOld.setVorname(p.getVorname());
                logger.debug("Updated partner first name to: {}", p.getVorname());
            }
            if (p.getNachname() != null) {
                pOld.setNachname(p.getNachname());
                logger.debug("Updated partner last name to: {}", p.getNachname());
            }
            if (p.getGeschlecht() != null) {
                pOld.setGeschlecht(p.getGeschlecht());
                logger.debug("Updated partner gender to: {}", p.getGeschlecht());
            }
            if (p.getGeburtsdatum() != null) {
                pOld.setGeburtsdatum(p.getGeburtsdatum());
                logger.debug("Updated partner birth date to: {}", p.getGeburtsdatum());
            }
            if (p.getLand() != null) {
                pOld.setLand(p.getLand());
                logger.debug("Updated partner country to: {}", p.getLand());
            }
            if (p.getStadt() != null) {
                pOld.setStadt(p.getStadt());
                logger.debug("Updated partner city to: {}", p.getStadt());
            }
            if (p.getStrasse() != null) {
                pOld.setStrasse(p.getStrasse());
                logger.debug("Updated partner street to: {}", p.getStrasse());
            }
            if (p.getHausnummer() != null) {
                pOld.setHausnummer(p.getHausnummer());
                logger.debug("Updated partner house number to: {}", p.getHausnummer());
            }
            if (p.getPlz() != null) {
                pOld.setPlz(p.getPlz());
                logger.debug("Updated partner postal code to: {}", p.getPlz());
            }
            if (p.getBundesland() != null) {
                pOld.setBundesland(p.getBundesland());
                logger.debug("Updated partner state to: {}", p.getBundesland());
            }
        }

        return vertragold;
    }
}