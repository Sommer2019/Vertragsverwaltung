package de.axa.robin.vertragsverwaltung.services;

import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.util.VertragUtil;
import de.axa.robin.vertragsverwaltung.validators.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for handling contract-related operations.
 */
@Service
@Component
public class VertragsService {
    private static final Logger logger = LoggerFactory.getLogger(VertragsService.class);
    @Autowired
    private Repository repository;
    @Autowired
    private InputValidator inputValidator;

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
        List<Vertrag> vertrage = getVertrage();
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
     * @param vertrag     the contract to create
     * @param preismodell the pricing model to use
     * @param result      the binding result for validation
     * @return the created contract
     * @throws IllegalArgumentException if validation errors are found
     */
    public Vertrag vertragAnlegen(Vertrag vertrag, Preis preismodell, BindingResult result) throws IllegalArgumentException {
        logger.info("Creating new contract: {}", vertrag);
        List<Vertrag> vertrage = getVertrage();
        vertrag.setVsnr(createvsnr(vertrage));
        vertrag.setPreis(createPreis(vertrag.isMonatlich(), vertrag.getPartner().getGeburtsdatum(), vertrag.getFahrzeug().getHoechstgeschwindigkeit(), preismodell));
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

    /**
     * Edits an existing contract.
     *
     * @param vertragalt  the old contract details
     * @param vsnr        the VSNR of the contract to edit
     * @param preismodell the pricing model to use
     * @param result      the binding result for validation
     * @return the edited contract
     * @throws IllegalArgumentException if validation errors are found
     */
    public Vertrag vertragBearbeiten(Vertrag vertragalt, int vsnr, Preis preismodell, BindingResult result) throws IllegalArgumentException {
        Vertrag vertragneu = VertragUtil.mergeVertrage(getVertrag(vsnr), vertragalt);
        List<Vertrag> vertrage = getVertrage();
        if (result != null) {
            inputValidator.validateVertrag(vertrage, vertragneu, result);
            if (result.hasErrors()) {
                logger.warn("Validation errors found: {}", result.getAllErrors());
                throw new IllegalArgumentException("Validation errors found: " + result.getAllErrors());
            }
        }
        vertragneu.setPreis(createPreis(vertragneu.isMonatlich(), vertragneu.getPartner().getGeburtsdatum(), vertragneu.getFahrzeug().getHoechstgeschwindigkeit(), preismodell));
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
     * @param vsnr     the VSNR of the contract to delete
     * @param vertrage the list of contracts
     */
    public void vertragLoeschen(int vsnr, List<Vertrag> vertrage) {
        logger.info("Deleting contract with ID: {}", vsnr);
        boolean removed = vertrage.removeIf(v -> v.getVsnr() == vsnr);
        if (removed) {
            logger.info("Contract successfully deleted with VSNR: {}", vsnr);
            repository.speichereVertrage(vertrage);
        } else {
            logger.warn("Contract not found for deletion with VSNR: {}", vsnr);
        }
    }

    /**
     * Generates a unique insurance number (VSNR).
     *
     * @param vertrage the list of contracts
     * @return the generated insurance number
     * @throws IllegalStateException if no free insurance numbers are available
     */
    public int createvsnr(List<Vertrag> vertrage) {
        logger.info("Generating unique insurance number (VSNR)");
        int vsnr = 10000000;
        while (inputValidator.vertragExistiert(vertrage, vsnr)) {
            vsnr++;
        }
        if (vsnr > 99999999) {
            logger.error("No free insurance numbers available");
            throw new IllegalStateException("Keine freien Versicherungsnummern mehr!");
        }
        logger.debug("Generated VSNR: {}", vsnr);
        return vsnr;
    }

    /**
     * Calculates the insurance price based on various factors.
     *
     * @param monatlich              whether the payment is monthly
     * @param geburtsDatum           the birthdate of the insured person
     * @param hoechstGeschwindigkeit the maximum speed of the vehicle
     * @param preismodell            the pricing model to use
     * @return the calculated insurance price
     * @throws IllegalStateException if an error occurs during price calculation
     */
    public double createPreis(boolean monatlich, LocalDate geburtsDatum, int hoechstGeschwindigkeit, Preis preismodell) {
        logger.info("Calculating insurance price");
        double preis;
        int alter = LocalDate.now().getYear() - geburtsDatum.getYear();
        try {
            preis = (alter * preismodell.getAge() + hoechstGeschwindigkeit * preismodell.getSpeed()) * preismodell.getFaktor();
            if (!monatlich) {
                preis = preis * 11;
            }
        } catch (Exception e) {
            logger.error("Error calculating price", e);
            throw new IllegalStateException(e);
        }
        double roundedPreis = Math.round(preis * 100.0) / 100.0;
        logger.debug("Calculated price: {}", roundedPreis);
        return roundedPreis;
    }
}