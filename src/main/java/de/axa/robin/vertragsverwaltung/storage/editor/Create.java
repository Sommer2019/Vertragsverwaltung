package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.FileReader;
import java.time.LocalDate;

public class Create {
    ////Klassen einlesen////
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung;

    public Create(Vertragsverwaltung vertragsverwaltung) {
        this.vertragsverwaltung = vertragsverwaltung;
    }

    public int createvsnr() {
        int vsnr = 10000000;
        while (vertragsverwaltung.getVertrag(vsnr) != null) {
            vsnr++;
        }
        if (vsnr > 99999999) {
            System.err.println("Keine freien Versicherungsnummern mehr!");
            System.exit(1);
        }
        return vsnr;
    }

    public double createPreis(boolean monatlich, Partner partner, Fahrzeug fahrzeug) {
        double preis = 0;
        double factor = 1.5;
        double factoralter = 0.1;
        double factorspeed = 0.4;
        int alter = LocalDate.now().getYear() - partner.getGeburtsdatum().getYear();
        try (JsonReader reader = Json.createReader(new FileReader(setup.getPreisPath()))) {
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
            System.err.println("Ungültige Eingabe!");
        }
        return Math.round(preis * 100.0) / 100.0;
    }
}
