package de.axa.robin.vertragsverwaltung.validators;

import de.axa.robin.vertragsverwaltung.exceptions.DataLoadException;
import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * This class provides methods to validate various inputs related to contracts.
 */
@Component
public class InputValidator {
    private static final Logger logger = LoggerFactory.getLogger(InputValidator.class);

    @Autowired
    private Setup setup;
    @Autowired
    private AdressValidator adressValidator;

    private static final String ERROR_INVALID_DATE = "Ungültiger Versicherungsbeginn";
    private static final String ERROR_INVALID_EXPIRY = "Ungültiger Versicherungsablauf";
    private static final String ERROR_INVALID_APPLICATION_DATE = "Ungültiges Antragsdatum";
    private static final String ERROR_INVALID_MANUFACTURER = "Ungültiger Hersteller";
    private static final String ERROR_INVALID_SPEED = "Ungültige Hoechstgeschwindigkeit";
    private static final String ERROR_INVALID_WAGNNIS = "Ungültige Wagnisskennziffer";
    private static final String ERROR_INVALID_GENDER = "Ungültiges Geschlecht";
    private static final String ERROR_INVALID_BIRTH_DATE = "Ungültiges Geburtsdatum";
    private static final String ERROR_INVALID_ADDRESS = "Ungültige Adresse";

    /**
     * Validates if the given manufacturer exists in the JSON file.
     *
     * @param searchString the manufacturer name to search for
     * @return true if the manufacturer exists, false otherwise
     */
    public boolean validateHersteller(String searchString) throws Exception {
        String filePath = setup.getJson_brandsPath();
        logger.info("Checking if string '{}' is in JSON file '{}'", searchString, filePath);
        try (InputStream fis = new FileInputStream(filePath);
             JsonReader jsonReader = Json.createReader(fis)) {
            JsonObject jsonObject = jsonReader.readObject();
            boolean contains = jsonObject.toString().contains("'" + searchString + "'");
            logger.debug("String '{}' found in JSON file: {}", searchString, contains);
            return contains;
        } catch (FileNotFoundException e) {
            logger.error("File not found: {}", filePath, e);
            throw new Exception(e);
        } catch (Exception e) {
            logger.error("Error reading JSON file", e);
            throw new Exception(e);
        }
    }

    /**
     * Validates the given contract and adds errors to the BindingResult if any.
     *
     * @param vertrage the list of existing contracts
     * @param vertrag the contract to validate
     * @param result the BindingResult to add errors to
     * @return true if there are validation errors, false otherwise
     */
    public boolean validateVertrag(List<Vertrag> vertrage, Vertrag vertrag, BindingResult result) throws DataLoadException {
        logger.info("Validating contract: {}", vertrag);
        try {
            return validateInsuranceDates(vertrag, result) ||
                    validateVehicle(vertrag, result) ||
                    validatePartner(vertrag, result) ||
                    validateAddress(vertrag, result) ||
                    vertrag.getVersicherungsbeginn().isBefore(LocalDate.now()) ||
                    kennzeichenExistiert(vertrage, vertrag.getFahrzeug().getAmtlichesKennzeichen()) ||
                    vertragExistiert(vertrage, vertrag.getVsnr());
        } catch (Exception e) {
            throw new DataLoadException("Fehler beim Laden der Herstellerliste", e);
        }
    }

    /**
     * Validates the insurance dates of the given contract.
     *
     * @param vertrag the contract to validate
     * @param result the BindingResult to add errors to
     * @return true if there are validation errors, false otherwise
     */
    private boolean validateInsuranceDates(Vertrag vertrag, BindingResult result) {
        if (result == null) {
            logger.info("BindingResult is null, skipping insurance dates validation");
            return false;
        }
        if (vertrag.getVersicherungsbeginn().isBefore(vertrag.getAntragsDatum())) {
            result.rejectValue("versicherungsbeginn", "error.versicherungsbeginn", ERROR_INVALID_DATE);
            logger.warn("Invalid insurance start date: {}", vertrag.getVersicherungsbeginn());
            return true;
        }
        if (vertrag.getVersicherungsablauf().isBefore(vertrag.getVersicherungsbeginn())) {
            result.rejectValue("versicherungsablauf", "error.versicherungsablauf", ERROR_INVALID_EXPIRY);
            logger.warn("Invalid insurance expiry date: {}", vertrag.getVersicherungsablauf());
            return true;
        }
        if (vertrag.getAntragsDatum().isAfter(vertrag.getVersicherungsbeginn())) {
            result.rejectValue("antragsdatum", "error.antragsdatum", ERROR_INVALID_APPLICATION_DATE);
            logger.warn("Invalid application date: {}", vertrag.getAntragsDatum());
            return true;
        }
        return false;
    }

    /**
     * Validates the vehicle details of the given contract.
     *
     * @param vertrag the contract to validate
     * @param result the BindingResult to add errors to
     * @return true if there are validation errors, false otherwise
     */
    private boolean validateVehicle(Vertrag vertrag, BindingResult result) throws Exception {
        if (result == null) {
            logger.info("BindingResult is null, skipping vehicle validation");
            return false;
        }
        try {
            if (!validateHersteller(vertrag.getFahrzeug().getHersteller())) {
                result.rejectValue("fahrzeug.hersteller", "error.fahrzeug.hersteller", ERROR_INVALID_MANUFACTURER);
                logger.warn("Invalid vehicle manufacturer: {}", vertrag.getFahrzeug().getHersteller());
                return true;
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        if (vertrag.getFahrzeug().getHoechstgeschwindigkeit() <= 50 || vertrag.getFahrzeug().getHoechstgeschwindigkeit() >= 250) {
            result.rejectValue("fahrzeug.hoechstgeschwindigkeit", "error.fahrzeug.hoechstgeschwindigkeit", ERROR_INVALID_SPEED);
            logger.warn("Invalid vehicle speed: {}", vertrag.getFahrzeug().getHoechstgeschwindigkeit());
            return true;
        }
        if (vertrag.getFahrzeug().getWagnisskennziffer() != 112) {
            result.rejectValue("fahrzeug.wagnisskennziffer", "error.fahrzeug.wagnisskennziffer", ERROR_INVALID_WAGNNIS);
            logger.warn("Invalid vehicle risk number: {}", vertrag.getFahrzeug().getWagnisskennziffer());
            return true;
        }
        return false;
    }

    /**
     * Validates the partner details of the given contract.
     *
     * @param vertrag the contract to validate
     * @param result the BindingResult to add errors to
     * @return true if there are validation errors, false otherwise
     */
    private boolean validatePartner(Vertrag vertrag, BindingResult result) {
        if (result == null) {
            logger.info("BindingResult is null, skipping partner validation");
            return false;
        }
        char gender = vertrag.getPartner().getGeschlecht().charAt(0);
        if (gender != 'M' && gender != 'D' && gender != 'W') {
            result.rejectValue("partner.geschlecht", "error.partner.geschlecht", ERROR_INVALID_GENDER);
            logger.warn("Invalid partner gender: {}", gender);
            return true;
        }
        LocalDate birthDate = vertrag.getPartner().getGeburtsdatum();
        if (birthDate.isBefore(LocalDate.now().minusYears(110)) || birthDate.isAfter(LocalDate.now().minusYears(18))) {
            result.rejectValue("partner.geburtsdatum", "error.partner.geburtsdatum", ERROR_INVALID_BIRTH_DATE);
            logger.warn("Invalid partner birth date: {}", birthDate);
            return true;
        }
        return false;
    }

    /**
     * Validates the address of the given contract.
     *
     * @param vertrag the contract to validate
     * @param result the BindingResult to add errors to
     * @return true if there are validation errors, false otherwise
     */
    private boolean validateAddress(Vertrag vertrag, BindingResult result) {
        if (!adressValidator.validateAddress(vertrag.getPartner().getStrasse(), vertrag.getPartner().getHausnummer(),
                vertrag.getPartner().getPlz(), vertrag.getPartner().getStadt(),
                vertrag.getPartner().getBundesland(), vertrag.getPartner().getLand())) {
            result.rejectValue("partner.land", "error.partner.land", ERROR_INVALID_ADDRESS);
            logger.warn("Invalid partner address: {}", vertrag.getPartner());
            return true;
        }
        return false;
    }

    /**
     * Checks if a contract exists by its VSNR.
     *
     * @param vertrage the list of existing contracts
     * @param vsnr the VSNR of the contract
     * @return true if the contract exists, false otherwise
     */
    public boolean vertragExistiert(List<Vertrag> vertrage, int vsnr) {
        boolean exists = vertrage.stream().anyMatch(v -> v.getVsnr() == vsnr);
        logger.info("Contract existence check for VSNR {}: {}", vsnr, exists);
        return exists;
    }

    /**
     * Checks if a license plate exists.
     *
     * @param vertrage the list of existing contracts
     * @param kennzeichen the license plate to check
     * @return true if the license plate exists, false otherwise
     */
    public boolean kennzeichenExistiert(List<Vertrag> vertrage, String kennzeichen) {
        boolean exists = vertrage.stream()
                .anyMatch(v -> v.getFahrzeug().getAmtlichesKennzeichen().equals(kennzeichen));
        logger.info("License plate existence check for {}: {}", kennzeichen, exists);
        return exists;
    }
}