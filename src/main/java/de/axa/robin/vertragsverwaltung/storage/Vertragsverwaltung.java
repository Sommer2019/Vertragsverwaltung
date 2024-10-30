package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;

import de.axa.robin.vertragsverwaltung.user_interaction.Output;
import jakarta.json.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Vertragsverwaltung {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final List<Vertrag> vertrage = new ArrayList<>();
    private final Logger logger = Logger.getLogger(Vertragsverwaltung.class.getName());
    {
        ladeVertrage();
    }
    public void vertragAnlegen(Vertrag vertrag) {
        vertrage.add(vertrag);
        speichereVertrage();
    }
    public double calcPreis(boolean monatlich, Partner partner, Fahrzeug fahrzeug) {
        double preis = 0;
        double factor = 1.5;
        double factoralter = 0.1;
        double factorspeed = 0.4;
        int alter = LocalDate.now().getYear() - partner.getGeburtsdatum().getYear();
        try (JsonReader reader = Json.createReader(new FileReader("src/main/resources/preiscalc.json"))) {
            JsonObject jsonObject = reader.readObject();
            factor = jsonObject.getJsonNumber("factor").doubleValue();
            factoralter = jsonObject.getJsonNumber("factorage").doubleValue();
            factorspeed = jsonObject.getJsonNumber("factorspeed").doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            preis = (alter * factoralter + fahrzeug.getHoechstgeschwindigkeit()  * factorspeed)*factor;
            if(!monatlich){
                preis = preis*11;
            }
        } catch (Exception e) {
            output.invalidinput();
        }
        return Math.round(preis * 100.0) / 100.0;
    }
    public void vertragLoeschen(int vsnr) {
        vertrage.removeIf(v -> v.getVsnr() == vsnr);
        speichereVertrage();
    }

    public List<Vertrag> getVertrage() {
        return vertrage;
    }

    public Vertrag getVertrag(int vsnr) {
        return vertrage.stream().filter(v -> v.getVsnr() == vsnr).findFirst().orElse(null);
    }
    public boolean vertragExistiert(int vsnr) {
        return vertrage.stream().anyMatch(v -> v.getVsnr() == vsnr);
    }
    public boolean kennzeichenExistiert(String kennzeichen) {
        return vertrage.stream().anyMatch(v -> v.getFahrzeug().getAmtlichesKennzeichen().equals(kennzeichen));
    }
    public void speichereVertrage() {
        try (FileWriter file = new FileWriter("src/main/resources/vertrage.json", false)) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Vertrag v : vertrage) {
                arrayBuilder.add(Json.createObjectBuilder()
                        .add("vsnr", v.getVsnr())
                        .add("abrechnungszeitraum monatlich", v.getMonatlich())
                        .add("preis", v.getPreis())
                        .add("versicherungsbeginn", v.getVersicherungsbeginn().toString())
                        .add("versicherungsablauf", v.getVersicherungsablauf().toString())
                        .add("antragsDatum", v.getAntragsDatum().toString())
                        .add("fahrzeug", Json.createObjectBuilder()
                                .add("amtlichesKennzeichen", v.getFahrzeug().getAmtlichesKennzeichen())
                                .add("hersteller", v.getFahrzeug().getHersteller())
                                .add("typ", v.getFahrzeug().getTyp())
                                .add("hoechstgeschwindigkeit", v.getFahrzeug().getHoechstgeschwindigkeit())
                                .add("wagnisskennziffer", v.getFahrzeug().getWagnisskennziffer()))
                        .add("partner", Json.createObjectBuilder()
                                .add("vorname", v.getPartner().getVorname())
                                .add("nachname", v.getPartner().getNachname())
                                .add("geschlecht", Character.toString(v.getPartner().getGeschlecht()))
                                .add("geburtsdatum", v.getPartner().getGeburtsdatum().toString())
                                .add("land", v.getPartner().getLand())
                                .add("strasse", v.getPartner().getStrasse())
                                .add("hausnummer", v.getPartner().getHausnummer())
                                .add("plz", v.getPartner().getPlz())
                                .add("stadt", v.getPartner().getStadt())
                                .add("bundesland", v.getPartner().getBundesland())
                                ));
            }
            JsonArray jsonArray = arrayBuilder.build();
            JsonWriter writer = Json.createWriter(file);
            writer.writeArray(jsonArray);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Fehler beim speichern", e);
        }
    }

    public void ladeVertrage() {
        try (FileReader file = new FileReader("src/main/resources/vertrage.json")) {
            JsonReader reader = Json.createReader(file);
            JsonArray jsonArray = reader.readArray();
            for (JsonValue jsonValue : jsonArray) {
                JsonObject jsonObject = jsonValue.asJsonObject();
                Vertrag vertrag = new Vertrag(
                        jsonObject.getInt("vsnr"),
                        jsonObject.getBoolean("abrechnungszeitraum monatlich"),
                        jsonObject.getJsonNumber("preis").doubleValue(),
                        LocalDate.parse(jsonObject.getString("versicherungsbeginn")),
                        LocalDate.parse(jsonObject.getString("versicherungsablauf")),
                        LocalDate.parse(jsonObject.getString("antragsDatum")),
                        new Fahrzeug(
                                jsonObject.getJsonObject("fahrzeug").getString("amtlichesKennzeichen"),
                                jsonObject.getJsonObject("fahrzeug").getString("hersteller"),
                                jsonObject.getJsonObject("fahrzeug").getString("typ"),
                                jsonObject.getJsonObject("fahrzeug").getInt("hoechstgeschwindigkeit"),
                                jsonObject.getJsonObject("fahrzeug").getInt("wagnisskennziffer")
                        ),
                        new Partner(
                                jsonObject.getJsonObject("partner").getString("vorname"),
                                jsonObject.getJsonObject("partner").getString("nachname"),
                                jsonObject.getJsonObject("partner").getString("geschlecht").charAt(0),
                                LocalDate.parse(jsonObject.getJsonObject("partner").getString("geburtsdatum")),
                                jsonObject.getJsonObject("partner").getString("land"),
                                jsonObject.getJsonObject("partner").getString("strasse"),
                                jsonObject.getJsonObject("partner").getString("hausnummer"),
                                jsonObject.getJsonObject("partner").getInt("plz"),
                                jsonObject.getJsonObject("partner").getString("stadt"),
                                jsonObject.getJsonObject("partner").getString("bundesland")
                        )
                );
                vertrage.add(vertrag);
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "Datei 'vertrage.json' nicht gefunden", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Fehler beim laden", e);
        }
    }
}
