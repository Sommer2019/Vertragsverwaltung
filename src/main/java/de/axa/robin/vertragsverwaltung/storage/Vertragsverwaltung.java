package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;

import de.axa.robin.vertragsverwaltung.user_interaction.Output;
import jakarta.json.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;


public class Vertragsverwaltung {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final Repository repository = new Repository();

    public void vertragAnlegen(Vertrag vertrag) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        vertrage.add(vertrag);
        repository.speichereVertrage(vertrage);
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
            preis = (alter * factoralter + fahrzeug.getHoechstgeschwindigkeit() * factorspeed) * factor;
            if (!monatlich) {
                preis = preis * 11;
            }
        } catch (Exception e) {
            output.error("Ungültige Eingabe!");
        }
        return Math.round(preis * 100.0) / 100.0;
    }

    public void vertragLoeschen(int vsnr) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        vertrage.removeIf(v -> v.getVsnr() == vsnr);
        repository.speichereVertrage(vertrage);
    }

    public List<Vertrag> getVertrage() {
        return repository.ladeVertrage();
    }

    public Vertrag getVertrag(int vsnr) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        return vertrage.stream().filter(v -> v.getVsnr() == vsnr).findFirst().orElse(null);
    }

    public boolean vertragExistiert(int vsnr) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        return vertrage.stream().anyMatch(v -> v.getVsnr() == vsnr);
    }

    public boolean kennzeichenExistiert(String kennzeichen) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        return vertrage.stream().anyMatch(v -> v.getFahrzeug().getAmtlichesKennzeichen().equals(kennzeichen));
    }
}
