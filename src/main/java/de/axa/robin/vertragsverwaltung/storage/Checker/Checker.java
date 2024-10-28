package de.axa.robin.vertragsverwaltung.storage.Checker;

import java.time.LocalDate;

import de.axa.robin.vertragsverwaltung.user_interaction.Output;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Checker {
    ////Klassen einlesen////
    private final Output output = new Output();

    public boolean geburtsdatum(LocalDate geburtsdatum) {
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(18);
        LocalDate maxDate = now.minusYears(110);
        return !geburtsdatum.isBefore(minDate) || !geburtsdatum.isAfter(maxDate);
    }

    public boolean string(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return input.isEmpty();
    }
    public boolean isStringInJsonFile(String searchString) {
        String filePath = "src/main/resources/brands.json";
        try (InputStream fis = new FileInputStream(filePath);
             JsonReader jsonReader = Json.createReader(fis)) {
            JsonObject jsonObject = jsonReader.readObject();
            return jsonObject.toString().contains(searchString);
        } catch (FileNotFoundException e) {
            output.errorvalidate("File not found: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
