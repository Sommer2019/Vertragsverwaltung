package de.axa.robin.vertragsverwaltung.backend.modell;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.FileReader;

public class Preis {
    private double speed;
    private double age;
    private double faktor;

    public Preis() {
        Setup setup = new Setup();
        Repository repo = new Repository(setup);
        JsonObject jsonObject = repo.ladeFaktoren();
        this.faktor = jsonObject.getJsonNumber("factor").doubleValue();
        this.age = jsonObject.getJsonNumber("factorage").doubleValue();
        this.speed = jsonObject.getJsonNumber("factorspeed").doubleValue();
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public double getFaktor() {
        return faktor;
    }

    public void setFaktor(double faktor) {
        this.faktor = faktor;
    }
}
