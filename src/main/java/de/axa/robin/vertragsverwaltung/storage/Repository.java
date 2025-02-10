package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import jakarta.json.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for managing contracts and factors.
 */
@org.springframework.stereotype.Repository
public class Repository {
    private final Logger logger = Logger.getLogger(Repository.class.getName());
    @Autowired
    private Setup setup;
    private static final String ERROR_LOADING = "Fehler beim Laden";
    private static final String ERROR_SAVING = "Fehler beim Speichern";
    private static final String FILE_NOT_FOUND = "Datei 'vertrage.json' nicht gefunden";

    /**
     * Saves a list of contracts to a JSON file.
     *
     * @param vertrage the list of contracts to save
     */
    public void speichereVertrage(List<Vertrag> vertrage) {
        try (FileWriter file = new FileWriter(setup.getJson_repositoryPath(), false)) {
            JsonArray jsonArray = createVertraegeJsonArray(vertrage);
            JsonWriter writer = Json.createWriter(file);
            writer.writeArray(jsonArray);
        } catch (IOException e) {
            logger.log(Level.SEVERE, ERROR_SAVING, e);
        }
    }

    /**
     * Creates a JSON array from a list of contracts.
     *
     * @param vertrage the list of contracts
     * @return the JSON array representing the contracts
     */
    private JsonArray createVertraegeJsonArray(List<Vertrag> vertrage) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Vertrag v : vertrage) {
            arrayBuilder.add(createVertragJsonObject(v));
        }
        return arrayBuilder.build();
    }

    /**
     * Creates a JSON object from a contract.
     *
     * @param v the contract
     * @return the JSON object representing the contract
     */
    private JsonObject createVertragJsonObject(Vertrag v) {
        return Json.createObjectBuilder()
                .add("vsnr", v.getVsnr())
                .add("abrechnungszeitraum monatlich", v.isMonatlich())
                .add("preis", v.getPreis())
                .add("versicherungsbeginn", v.getVersicherungsbeginn().toString())
                .add("versicherungsablauf", v.getVersicherungsablauf().toString())
                .add("antragsDatum", v.getAntragsDatum().toString())
                .add("fahrzeug", createFahrzeugJsonObject(v.getFahrzeug()))
                .add("partner", createPartnerJsonObject(v.getPartner()))
                .build();
    }

    /**
     * Creates a JSON object from a vehicle.
     *
     * @param fahrzeug the vehicle
     * @return the JSON object representing the vehicle
     */
    private JsonObject createFahrzeugJsonObject(Fahrzeug fahrzeug) {
        return Json.createObjectBuilder()
                .add("amtlichesKennzeichen", fahrzeug.getAmtlichesKennzeichen())
                .add("hersteller", fahrzeug.getHersteller())
                .add("typ", fahrzeug.getTyp())
                .add("hoechstgeschwindigkeit", fahrzeug.getHoechstgeschwindigkeit())
                .add("wagnisskennziffer", fahrzeug.getWagnisskennziffer())
                .build();
    }

    /**
     * Creates a JSON object from a partner.
     *
     * @param partner the partner
     * @return the JSON object representing the partner
     */
    private JsonObject createPartnerJsonObject(Partner partner) {
        return Json.createObjectBuilder()
                .add("vorname", partner.getVorname())
                .add("nachname", partner.getNachname())
                .add("geschlecht", Character.toString(partner.getGeschlecht().charAt(0)))
                .add("geburtsdatum", partner.getGeburtsdatum().toString())
                .add("land", partner.getLand())
                .add("strasse", partner.getStrasse())
                .add("hausnummer", partner.getHausnummer())
                .add("plz", partner.getPlz())
                .add("stadt", partner.getStadt())
                .add("bundesland", partner.getBundesland())
                .build();
    }

    /**
     * Loads a list of contracts from a JSON file.
     *
     * @return the list of contracts
     */
    public List<Vertrag> ladeVertrage() {
        List<Vertrag> vertrage = new ArrayList<>();
        try (FileReader file = new FileReader(setup.getJson_repositoryPath())) {
            JsonReader reader = Json.createReader(file);
            JsonArray jsonArray = reader.readArray();
            for (JsonValue jsonValue : jsonArray) {
                vertrage.add(createVertragFromJson(jsonValue.asJsonObject()));
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, FILE_NOT_FOUND, e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, ERROR_LOADING, e);
        }
        return vertrage;
    }

    /**
     * Creates a contract from a JSON object.
     *
     * @param jsonObject the JSON object
     * @return the contract
     */
    private Vertrag createVertragFromJson(JsonObject jsonObject) {
        return new Vertrag(
                jsonObject.getInt("vsnr"),
                jsonObject.getBoolean("abrechnungszeitraum monatlich"),
                jsonObject.getJsonNumber("preis").doubleValue(),
                LocalDate.parse(jsonObject.getString("versicherungsbeginn")),
                LocalDate.parse(jsonObject.getString("versicherungsablauf")),
                LocalDate.parse(jsonObject.getString("antragsDatum")),
                createFahrzeugFromJson(jsonObject.getJsonObject("fahrzeug")),
                createPartnerFromJson(jsonObject.getJsonObject("partner"))
        );
    }

    /**
     * Creates a vehicle from a JSON object.
     *
     * @param jsonObject the JSON object
     * @return the vehicle
     */
    private Fahrzeug createFahrzeugFromJson(JsonObject jsonObject) {
        return new Fahrzeug(
                jsonObject.getString("amtlichesKennzeichen"),
                jsonObject.getString("hersteller"),
                jsonObject.getString("typ"),
                jsonObject.getInt("hoechstgeschwindigkeit"),
                jsonObject.getInt("wagnisskennziffer")
        );
    }

    /**
     * Creates a partner from a JSON object.
     *
     * @param jsonObject the JSON object
     * @return the partner
     */
    private Partner createPartnerFromJson(JsonObject jsonObject) {
        return new Partner(
                jsonObject.getString("vorname"),
                jsonObject.getString("nachname"),
                jsonObject.getString("geschlecht").charAt(0),
                LocalDate.parse(jsonObject.getString("geburtsdatum")),
                jsonObject.getString("land"),
                jsonObject.getString("strasse"),
                jsonObject.getString("hausnummer"),
                jsonObject.getString("plz"),
                jsonObject.getString("stadt"),
                jsonObject.getString("bundesland")
        );
    }

    /**
     * Loads factors from a JSON file.
     *
     * @return the JSON object representing the factors
     */
    public JsonObject ladeFaktoren() {
        try (JsonReader reader = Json.createReader(new FileReader(setup.getJson_preisPath()))) {
            return reader.readObject();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fehler beim Laden der Faktoren", e);
        }
        return null;
    }

    /**
     * Saves factors to a JSON file.
     *
     * @param factor the factor
     * @param factorage the age factor
     * @param factorspeed the speed factor
     */
    public void speichereFaktoren(double factor, double factorage, double factorspeed) {
        try (FileWriter file = new FileWriter(setup.getJson_preisPath(), false)) {
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("factor", factor)
                    .add("factorage", factorage)
                    .add("factorspeed", factorspeed)
                    .build();
            JsonWriter writer = Json.createWriter(file);
            writer.writeObject(jsonObject);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Fehler beim Speichern der Faktoren", e);
        }
    }
}
