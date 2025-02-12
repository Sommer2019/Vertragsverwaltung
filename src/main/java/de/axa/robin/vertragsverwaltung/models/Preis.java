package de.axa.robin.vertragsverwaltung.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the price model with speed, age, and factor attributes.
 */
@Getter
@Setter
public class Preis{
    private double speed;
    private double age;
    private double faktor;

    /**
     * Default constructor for Preis.
     */
    public Preis() {
    }
}
