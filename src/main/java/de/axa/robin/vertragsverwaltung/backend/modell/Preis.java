package de.axa.robin.vertragsverwaltung.backend.modell;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import jakarta.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
