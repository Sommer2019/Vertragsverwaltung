package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import jakarta.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Repository
public class Repository {
    private final Logger logger = LoggerFactory.getLogger(Repository.class.getName());
    @Autowired
    private Setup setup;
    private static final String ERROR_LOADING = "Fehler beim Laden";
    private static final String ERROR_SAVING = "Fehler beim Speichern";
    private static final String FILE_NOT_FOUND = "Datei 'vertrage.json' nicht gefunden";

    public void speichereVertrage(List<Vertrag> vertrage) {
        logger.info("Starting to save contracts to JSON file");
        try (FileWriter file = new FileWriter(setup.getJson_repositoryPath(), false)) {
            JsonArray jsonArray = createVertraegeJsonArray(vertrage);
            JsonWriter writer = Json.createWriter(file);
            writer.writeArray(jsonArray);
            logger.info("Successfully saved contracts to JSON file");
        } catch (IOException e) {
            logger.error(ERROR_SAVING, e);
        }
    }

    private JsonArray createVertraegeJsonArray(List<Vertrag> vertrage) {
        logger.debug("Creating JSON array from contracts");
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Vertrag v : vertrage) {
            arrayBuilder.add(createVertragJsonObject(v));
            logger.debug("Added contract to JSON array: {}", v);
        }
        return arrayBuilder.build();
    }

    private JsonObject createVertragJsonObject(Vertrag v) {
        logger.debug("Creating JSON object from contract: {}", v);
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

    private JsonObject createFahrzeugJsonObject(Fahrzeug fahrzeug) {
        logger.debug("Creating JSON object from vehicle: {}", fahrzeug);
        return Json.createObjectBuilder()
                .add("amtlichesKennzeichen", fahrzeug.getAmtlichesKennzeichen())
                .add("hersteller", fahrzeug.getHersteller())
                .add("typ", fahrzeug.getTyp())
                .add("hoechstgeschwindigkeit", fahrzeug.getHoechstgeschwindigkeit())
                .add("wagnisskennziffer", fahrzeug.getWagnisskennziffer())
                .build();
    }

    private JsonObject createPartnerJsonObject(Partner partner) {
        logger.debug("Creating JSON object from partner: {}", partner);
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

    public List<Vertrag> ladeVertrage(){
        logger.info("Starting to load contracts from JSON file");
        List<Vertrag> vertrage = new ArrayList<>();
        try (FileReader file = new FileReader(setup.getJson_repositoryPath())) {
            JsonReader reader = Json.createReader(file);
            JsonArray jsonArray = reader.readArray();
            for (JsonValue jsonValue : jsonArray) {
                Vertrag vertrag = createVertragFromJson(jsonValue.asJsonObject());
                vertrage.add(vertrag);
                logger.debug("Loaded contract from JSON: {}", vertrag);
            }
            logger.info("Successfully loaded contracts from JSON file");
        } catch (FileNotFoundException e) {
            logger.warn(FILE_NOT_FOUND, e);
        } catch (IOException e) {
            logger.error(ERROR_LOADING, e);
        }
        return vertrage;
    }

    private Vertrag createVertragFromJson(JsonObject jsonObject) {
        logger.debug("Creating contract from JSON object: {}", jsonObject);
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

    private Fahrzeug createFahrzeugFromJson(JsonObject jsonObject) {
        logger.debug("Creating vehicle from JSON object: {}", jsonObject);
        return new Fahrzeug(
                jsonObject.getString("amtlichesKennzeichen"),
                jsonObject.getString("hersteller"),
                jsonObject.getString("typ"),
                jsonObject.getInt("hoechstgeschwindigkeit"),
                jsonObject.getInt("wagnisskennziffer")
        );
    }

    private Partner createPartnerFromJson(JsonObject jsonObject) {
        logger.debug("Creating partner from JSON object: {}", jsonObject);
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

    public JsonObject ladeFaktoren() {
        logger.info("Starting to load factors from JSON file");
        try (JsonReader reader = Json.createReader(new FileReader(setup.getJson_preisPath()))) {
            JsonObject jsonObject = reader.readObject();
            logger.info("Successfully loaded factors from JSON file: {}", jsonObject);
            return jsonObject;
        } catch (Exception e) {
            logger.error("Fehler beim Laden der Faktoren", e);
        }
        return null;
    }

    public void speichereFaktoren(double factor, double factorage, double factorspeed) {
        logger.info("Starting to save factors to JSON file");
        try (FileWriter file = new FileWriter(setup.getJson_preisPath(), false)) {
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("factor", factor)
                    .add("factorage", factorage)
                    .add("factorspeed", factorspeed)
                    .build();
            JsonWriter writer = Json.createWriter(file);
            writer.writeObject(jsonObject);
            logger.info("Successfully saved factors to JSON file: factor={}, factorage={}, factorspeed={}", factor, factorage, factorspeed);
        } catch (IOException e) {
            logger.error("Fehler beim Speichern der Faktoren", e);
        }
    }
}