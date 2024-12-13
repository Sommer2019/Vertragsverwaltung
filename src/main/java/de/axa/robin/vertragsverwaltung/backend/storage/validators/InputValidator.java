package de.axa.robin.vertragsverwaltung.backend.storage.validators;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.springframework.validation.BindingResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;

public class InputValidator {
    private final Setup setup = new Setup();
    private final AdressValidator adressValidator = new AdressValidator();

    public boolean string(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return input.isEmpty();
    }

    public boolean isStringInJsonFile(String searchString) {
        String filePath = setup.getBrandsPath();
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
    public void validateVertrag(Vertrag vertrag, BindingResult result) {
        if (vertrag.getVersicherungsbeginn().isBefore(vertrag.getAntragsDatum())) {
            result.rejectValue("versicherungsbeginn", "error.versicherungsbeginn", "Ungültiger Versicherungsbeginn");
        }
        if (vertrag.getVersicherungsablauf().isBefore(vertrag.getVersicherungsbeginn())) {
            result.rejectValue("versicherungsablauf", "error.versicherungsablauf", "Ungültiger Versicherungsablauf");
        }
        if (vertrag.getAntragsDatum().isAfter(vertrag.getVersicherungsbeginn())) {
            result.rejectValue("antragsdatum", "error.antragsdatum", "Ungültiges Antragsdatum");
        }

        if (!isStringInJsonFile(vertrag.getFahrzeug().getHersteller())) {
            result.rejectValue("fahrzeug.hersteller", "error.fahrzeug.hersteller", "Ungültiger Hersteller");
        }
        if (vertrag.getFahrzeug().getHoechstgeschwindigkeit() <= 50 || vertrag.getFahrzeug().getHoechstgeschwindigkeit() >= 250) {
            result.rejectValue("fahrzeug.hoechstgeschwindigkeit", "error.fahrzeug.hoechstgeschwindigkeit", "Ungültige Hoechstgeschwindigkeit");
        }
        if (vertrag.getFahrzeug().getWagnisskennziffer() != 112) {
            result.rejectValue("fahrzeug.wagnisskennziffer", "error.fahrzeug.wagnisskennziffer", "Ungültige Wagnisskennziffer");
        }
        if (vertrag.getPartner().getGeschlecht() != 'M' && vertrag.getPartner().getGeschlecht() != 'D' && vertrag.getPartner().getGeschlecht() != 'W') {
            result.rejectValue("partner.geschlecht", "error.partner.geschlecht", "Ungültiges Geschlecht");
        }
        if (vertrag.getPartner().getGeburtsdatum().isBefore(LocalDate.now().minusYears(110)) || vertrag.getPartner().getGeburtsdatum().isAfter(LocalDate.now().minusYears(18))) {
            result.rejectValue("partner.geburtsdatum", "error.partner.geburtsdatum", "Ungültiges Geburtsdatum");
        }
        // Address validation
        if (!adressValidator.validateAddress(vertrag.getPartner().getStrasse(), vertrag.getPartner().getHausnummer(), vertrag.getPartner().getPlz(), vertrag.getPartner().getStadt(), vertrag.getPartner().getBundesland(), vertrag.getPartner().getLand())) {
            result.rejectValue("partner.land", "error.partner.land", "Ungültige Adresse");
        }
    }
}
