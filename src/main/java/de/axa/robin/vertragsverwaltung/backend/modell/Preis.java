package de.axa.robin.vertragsverwaltung.backend.modell;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import jakarta.json.JsonObject;

public class Preis extends de.axa.robin.vertragsverwaltung.model.Preis {
    public Preis() {
        super();
        Setup setup = new Setup();
        Repository repo = new Repository(setup);
        JsonObject jsonObject = repo.ladeFaktoren();
        super.setFaktor(jsonObject.getJsonNumber("factor").doubleValue());
        super.setAge(jsonObject.getJsonNumber("factorage").doubleValue());
        super.setSpeed(jsonObject.getJsonNumber("factorspeed").doubleValue());
    }
}
