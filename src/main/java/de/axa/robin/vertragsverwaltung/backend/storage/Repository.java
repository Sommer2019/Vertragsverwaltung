package de.axa.robin.vertragsverwaltung.backend.storage;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import jakarta.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Repository {
    private final Logger logger = Logger.getLogger(Repository.class.getName());
    @Autowired
    private Setup setup;
    private static final String ERROR_LOADING = "Fehler beim Laden";
    private static final String ERROR_SAVING = "Fehler beim Speichern";
    private static final String FILE_NOT_FOUND = "Datei 'vertrage.json' nicht gefunden";


    public void speichereVertrage(List<Vertrag> vertrage) {
        try (FileWriter file = new FileWriter(setup.getJson_repositoryPath(), false)) {
            JsonArray jsonArray = createVertraegeJsonArray(vertrage);
            JsonWriter writer = Json.createWriter(file);
            writer.writeArray(jsonArray);
        } catch (IOException e) {
            logger.log(Level.SEVERE, ERROR_SAVING, e);
        }
    }

    private JsonArray createVertraegeJsonArray(List<Vertrag> vertrage) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Vertrag v : vertrage) {
            arrayBuilder.add(createVertragJsonObject(v));
        }
        return arrayBuilder.build();
    }

    private JsonObject createVertragJsonObject(Vertrag v) {
        return Json.createObjectBuilder()
                .add("vsnr", v.getVsnr())
                .add("abrechnungszeitraum monatlich", v.getMonatlich())
                .add("preis", v.getPreis())
                .add("versicherungsbeginn", v.getVersicherungsbeginn().toString())
                .add("versicherungsablauf", v.getVersicherungsablauf().toString())
                .add("antragsDatum", v.getAntragsDatum().toString())
                .add("fahrzeug", createFahrzeugJsonObject(convertToBackendFahrzeug(v.getFahrzeug())))
                .add("partner", createPartnerJsonObject(convertToBackendPartner(v.getPartner())))
                .build();
    }

    private JsonObject createFahrzeugJsonObject(Fahrzeug fahrzeug) {
        return Json.createObjectBuilder()
                .add("amtlichesKennzeichen", fahrzeug.getAmtlichesKennzeichen())
                .add("hersteller", fahrzeug.getHersteller())
                .add("typ", fahrzeug.getTyp())
                .add("hoechstgeschwindigkeit", fahrzeug.getHoechstgeschwindigkeit())
                .add("wagnisskennziffer", fahrzeug.getWagnisskennziffer())
                .build();
    }

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

    private Fahrzeug createFahrzeugFromJson(JsonObject jsonObject) {
        return new Fahrzeug(
                jsonObject.getString("amtlichesKennzeichen"),
                jsonObject.getString("hersteller"),
                jsonObject.getString("typ"),
                jsonObject.getInt("hoechstgeschwindigkeit"),
                jsonObject.getInt("wagnisskennziffer")
        );
    }

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

    public JsonObject ladeFaktoren() {
        try (JsonReader reader = Json.createReader(new FileReader(setup.getJson_preisPath()))) {
            return reader.readObject();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fehler beim Laden der Faktoren", e);
        }
        return null;
    }

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

    private Fahrzeug convertToBackendFahrzeug(de.axa.robin.vertragsverwaltung.model.Fahrzeug fahrzeug) {
        return new Fahrzeug(
                fahrzeug.getAmtlichesKennzeichen(),
                fahrzeug.getHersteller(),
                fahrzeug.getTyp(),
                fahrzeug.getHoechstgeschwindigkeit(),
                fahrzeug.getWagnisskennziffer()
        );
    }

    private Partner convertToBackendPartner(de.axa.robin.vertragsverwaltung.model.Partner partner) {
        return new Partner(
                partner.getVorname(),
                partner.getNachname(),
                partner.getGeschlecht().charAt(0),
                partner.getGeburtsdatum(),
                partner.getLand(),
                partner.getStrasse(),
                partner.getHausnummer(),
                partner.getPlz(),
                partner.getStadt(),
                partner.getBundesland()
        );
    }
}
