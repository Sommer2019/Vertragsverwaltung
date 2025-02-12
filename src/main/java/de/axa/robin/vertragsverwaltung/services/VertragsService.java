package de.axa.robin.vertragsverwaltung.services;

import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.validators.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
@Component
public class VertragsService {
    private static final Logger logger = LoggerFactory.getLogger(VertragsService.class);
    @Autowired
    private Repository repository;
    @Autowired
    private InputValidator inputValidator;
    @Autowired
    private CreateUnsetableData createUnsetableData;

    /**
     * Retrieves all contracts.
     *
     * @return a list of all contracts
     */
    public List<Vertrag> getVertrage() {
        logger.info("Retrieving all contracts");
        return repository.ladeVertrage();
    }

    /**
     * Retrieves a contract by its VSNR.
     *
     * @param vsnr the VSNR of the contract
     * @return the contract with the specified VSNR, or null if not found
     */
    public Vertrag getVertrag(int vsnr) {
        logger.info("Retrieving contract with VSNR: {}", vsnr);
        List<Vertrag> vertrage;
        vertrage = getVertrage();
        Vertrag vertrag = vertrage.stream()
                .filter(v -> v.getVsnr() == vsnr)
                .findFirst()
                .orElse(null);
        if (vertrag != null) {
            logger.info("Contract found: {}", vertrag);
            return vertrag;
        } else {
            logger.warn("Contract not found for VSNR: {}", vsnr);
            throw new IllegalArgumentException("Contract not found for VSNR: " + vsnr);
        }
    }

    /**
     * Creates a new contract.
     *
     * @param vertrag the contract to create
     * @return the created contract
     */
    public Vertrag vertragAnlegen(Vertrag vertrag, Preis preismodell, BindingResult result) throws IllegalArgumentException {
        logger.info("Creating new contract: {}", vertrag);
        List<Vertrag> vertrage = getVertrage();
        vertrag.setVsnr(createUnsetableData.createvsnr(vertrage));
        vertrag.setPreis(createUnsetableData.createPreis(vertrag.isMonatlich(), vertrag.getPartner().getGeburtsdatum(), vertrag.getFahrzeug().getHoechstgeschwindigkeit(), preismodell));
        inputValidator.validateVertrag(vertrage, vertrag, result);
        if (result != null && result.hasErrors()) {
            logger.warn("Validation errors found: {}", result.getAllErrors());
            throw new IllegalArgumentException("Validation errors found: " + result.getAllErrors());
        }
        logger.info("Contract successfully created: {}", vertrag);
        vertrage.add(vertrag);
        repository.speichereVertrage(vertrage);
        return vertrag;
    }

    public Vertrag vertragBearbeiten(Vertrag vertragalt, int vsnr, Preis preismodell, BindingResult result) throws IllegalArgumentException {
        Vertrag vertragneu = mergeVertrage(vertragalt, vertragalt);
        List<Vertrag> vertrage = getVertrage();
        if(result!=null){
            inputValidator.validateVertrag(vertrage, vertragneu, result);
            if (result.hasErrors()) {
                logger.warn("Validation errors found: {}", result.getAllErrors());
                throw new IllegalArgumentException("Validation errors found: " + result.getAllErrors());
            }
        }
        vertragneu.setPreis(createUnsetableData.createPreis(vertragneu.isMonatlich(), vertragneu.getPartner().getGeburtsdatum(), vertragneu.getFahrzeug().getHoechstgeschwindigkeit(), preismodell));
        vertragneu.setVsnr(vsnr);
        vertragLoeschen(vsnr, vertrage);
        vertrage = getVertrage();
        vertrage.add(vertragneu);
        repository.speichereVertrage(vertrage);
        return vertragneu;
    }

    /**
     * Deletes a contract by its VSNR.
     *
     * @param vsnr the VSNR of the contract to delete
     */
    public void vertragLoeschen(int vsnr, List<Vertrag> vertrage) {
        logger.info("Deleting contract with ID: {}", vsnr);
        boolean removed = vertrage.removeIf(v -> v.getVsnr() == vsnr);
        if (removed) {
            logger.info("Contract successfully deleted with VSNR: {}", vsnr);
            logger.info("Contract removed: " + removed);
            logger.info("VSNR: " + vsnr);
            repository.speichereVertrage(vertrage);
        } else {
            logger.warn("Contract not found for deletion with VSNR: {}", vsnr);
        }
    }

    /**
     * Edits an existing insurance contract (Vertrag) identified by the given vsnr.
     * Updates the fields of the existing contract with the values from the provided contract.
     *
     * @param vertrag the contract containing the new values
     * @return the updated contract, or null if no contract with the given vsnr exists
     */
    public Vertrag mergeVertrage(Vertrag vertrag, Vertrag vertragold) {
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
            if (f.getHersteller() != null) {
                vertragold.getFahrzeug().setHersteller(f.getHersteller());
                logger.debug("Updated vehicle manufacturer to: {}", f.getHersteller());
            }
            if (f.getHoechstgeschwindigkeit() != 0) {
                vertragold.getFahrzeug().setHoechstgeschwindigkeit(f.getHoechstgeschwindigkeit());
                logger.debug("Updated vehicle speed to: {}", f.getHoechstgeschwindigkeit());
            }
            if (f.getWagnisskennziffer() != 0) {
                vertragold.getFahrzeug().setWagnisskennziffer(f.getWagnisskennziffer());
                logger.debug("Updated vehicle risk number to: {}", f.getWagnisskennziffer());
            }
        }
        if (vertrag.getPartner() != null) {
            Partner p = vertrag.getPartner();
            if (p.getVorname() != null) {
                vertragold.getPartner().setVorname(p.getVorname());
                logger.debug("Updated partner first name to: {}", p.getVorname());
            }
            if (p.getNachname() != null) {
                vertragold.getPartner().setNachname(p.getNachname());
                logger.debug("Updated partner last name to: {}", p.getNachname());
            }
            if (p.getGeschlecht() != null) {
                vertragold.getPartner().setGeschlecht(p.getGeschlecht());
                logger.debug("Updated partner gender to: {}", p.getGeschlecht());
            }
            if (p.getGeburtsdatum() != null) {
                vertragold.getPartner().setGeburtsdatum(p.getGeburtsdatum());
                logger.debug("Updated partner birth date to: {}", p.getGeburtsdatum());
            }
            if (p.getLand() != null) {
                vertragold.getPartner().setLand(p.getLand());
                logger.debug("Updated partner country to: {}", p.getLand());
            }
            if (p.getStadt() != null) {
                vertragold.getPartner().setStadt(p.getStadt());
                logger.debug("Updated partner city to: {}", p.getStadt());
            }
            if (p.getStrasse() != null) {
                vertragold.getPartner().setStrasse(p.getStrasse());
                logger.debug("Updated partner street to: {}", p.getStrasse());
            }
            if (p.getHausnummer() != null) {
                vertragold.getPartner().setHausnummer(p.getHausnummer());
                logger.debug("Updated partner house number to: {}", p.getHausnummer());
            }
            if (p.getPlz() != null) {
                vertragold.getPartner().setPlz(p.getPlz());
                logger.debug("Updated partner postal code to: {}", p.getPlz());
            }
            if (p.getBundesland() != null) {
                vertragold.getPartner().setBundesland(p.getBundesland());
                logger.debug("Updated partner state to: {}", p.getBundesland());
            }
        }
        return vertragold;
    }
}
