package de.axa.robin.vertragsverwaltung.backend.modell;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import jakarta.json.JsonObject;

public class Preis extends de.axa.robin.vertragsverwaltung.model.Preis {
    public Preis(Setup setup) {
        super();
        Repository repository = new Repository(setup);
        JsonObject jsonObject = repository.ladeFaktoren();
        super.setFaktor(jsonObject.getJsonNumber("factor").doubleValue());
        super.setAge(jsonObject.getJsonNumber("factorage").doubleValue());
        super.setSpeed(jsonObject.getJsonNumber("factorspeed").doubleValue());
    }
}
