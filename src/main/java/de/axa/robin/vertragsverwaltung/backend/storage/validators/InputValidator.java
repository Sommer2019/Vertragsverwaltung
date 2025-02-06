package de.axa.robin.vertragsverwaltung.backend.storage.validators;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;

@Component
public class InputValidator {
    @Autowired
    private Setup setup;
    @Autowired
    private AdressValidator adressValidator;
    @Autowired
    private Vertragsverwaltung vertragsverwaltung;

    private static final String ERROR_INVALID_DATE = "Ungültiger Versicherungsbeginn";
    private static final String ERROR_INVALID_EXPIRY = "Ungültiger Versicherungsablauf";
    private static final String ERROR_INVALID_APPLICATION_DATE = "Ungültiges Antragsdatum";
    private static final String ERROR_INVALID_MANUFACTURER = "Ungültiger Hersteller";
    private static final String ERROR_INVALID_SPEED = "Ungültige Hoechstgeschwindigkeit";
    private static final String ERROR_INVALID_WAGNNIS = "Ungültige Wagnisskennziffer";
    private static final String ERROR_INVALID_GENDER = "Ungültiges Geschlecht";
    private static final String ERROR_INVALID_BIRTH_DATE = "Ungültiges Geburtsdatum";
    private static final String ERROR_INVALID_ADDRESS = "Ungültige Adresse";


    public boolean stringContainsDigit(String input) {
        return input.isEmpty() || input.chars().anyMatch(Character::isDigit);
    }

    public boolean isStringInJsonFile(String searchString) {
        String filePath = setup.getJson_brandsPath();
        try (InputStream fis = new FileInputStream(filePath);
             JsonReader jsonReader = Json.createReader(fis)) {
            JsonObject jsonObject = jsonReader.readObject();
            return jsonObject.toString().contains(searchString);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean validateVertrag(Vertrag vertrag, BindingResult result) {
        return validateInsuranceDates(vertrag, result) ||
                validateVehicle(vertrag, result) ||
                validatePartner(vertrag, result) ||
                validateAddress(vertrag, result);
    }

    private boolean validateInsuranceDates(Vertrag vertrag, BindingResult result) {
        if (result==null){
            return false;
        }
        if (vertrag.getVersicherungsbeginn().isBefore(vertrag.getAntragsDatum())) {
            result.rejectValue("versicherungsbeginn", "error.versicherungsbeginn", ERROR_INVALID_DATE);
            return true;
        }
        if (vertrag.getVersicherungsablauf().isBefore(vertrag.getVersicherungsbeginn())) {
            result.rejectValue("versicherungsablauf", "error.versicherungsablauf", ERROR_INVALID_EXPIRY);
            return true;
        }
        if (vertrag.getAntragsDatum().isAfter(vertrag.getVersicherungsbeginn())) {
            result.rejectValue("antragsdatum", "error.antragsdatum", ERROR_INVALID_APPLICATION_DATE);
            return true;
        }
        return false;
    }

    private boolean validateVehicle(Vertrag vertrag, BindingResult result) {
        if (result==null){
            return false;
        }
        if (!isStringInJsonFile(vertrag.getFahrzeug().getHersteller())) {
            result.rejectValue("fahrzeug.hersteller", "error.fahrzeug.hersteller", ERROR_INVALID_MANUFACTURER);
            return true;
        }
        if (vertrag.getFahrzeug().getHoechstgeschwindigkeit() <= 50 || vertrag.getFahrzeug().getHoechstgeschwindigkeit() >= 250) {
            result.rejectValue("fahrzeug.hoechstgeschwindigkeit", "error.fahrzeug.hoechstgeschwindigkeit", ERROR_INVALID_SPEED);
            return true;
        }
        if (vertrag.getFahrzeug().getWagnisskennziffer() != 112) {
            result.rejectValue("fahrzeug.wagnisskennziffer", "error.fahrzeug.wagnisskennziffer", ERROR_INVALID_WAGNNIS);
            return true;
        }
        return false;
    }

    private boolean validatePartner(Vertrag vertrag, BindingResult result) {
        if (result==null){
            return false;
        }
        if (vertrag.getPartner().getGeschlecht().charAt(0) != 'M' && vertrag.getPartner().getGeschlecht().charAt(0) != 'D' && vertrag.getPartner().getGeschlecht().charAt(0) != 'W') {
            result.rejectValue("partner.geschlecht", "error.partner.geschlecht", ERROR_INVALID_GENDER);
            return true;
        }
        if (vertrag.getPartner().getGeburtsdatum().isBefore(LocalDate.now().minusYears(110)) || vertrag.getPartner().getGeburtsdatum().isAfter(LocalDate.now().minusYears(18))) {
            result.rejectValue("partner.geburtsdatum", "error.partner.geburtsdatum", ERROR_INVALID_BIRTH_DATE);
            return true;
        }
        return false;
    }

    private boolean validateAddress(Vertrag vertrag, BindingResult result) {
        if (!adressValidator.validateAddress(vertrag.getPartner().getStrasse(), vertrag.getPartner().getHausnummer(),
                vertrag.getPartner().getPlz(), vertrag.getPartner().getStadt(),
                vertrag.getPartner().getBundesland(), vertrag.getPartner().getLand())) {
            result.rejectValue("partner.land", "error.partner.land", ERROR_INVALID_ADDRESS);
            return true;
        }
        return false;
    }

    public boolean isInvalidVertrag(Vertrag vertrag) {
        return validateVertrag(vertrag, null) ||
                vertrag.getVersicherungsbeginn().isBefore(LocalDate.now()) ||
                vertragsverwaltung.kennzeichenExistiert(vertrag.getFahrzeug().getAmtlichesKennzeichen()) ||
                vertragsverwaltung.vertragExistiert(vertrag.getVsnr());
    }
}
