package de.axa.robin.vertragsverwaltung.backend.modell;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Preis extends de.axa.robin.vertragsverwaltung.model.Preis {
    private double betrag;
    private String waehrung;

    public Preis() {
        this.betrag = betrag;
        this.waehrung = waehrung;
    }
}
