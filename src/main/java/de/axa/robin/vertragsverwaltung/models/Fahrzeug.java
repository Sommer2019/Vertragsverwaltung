/**
 * Represents a vehicle with its details.
 */
package de.axa.robin.vertragsverwaltung.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Fahrzeug class represents a vehicle with its details.
 */
@Getter
@Setter
public class Fahrzeug {
    private String amtlichesKennzeichen;
    private String hersteller;
    private String typ;
    private int hoechstgeschwindigkeit;
    private int wagnisskennziffer;

    /**
     * Constructs a new Fahrzeug with the specified details.
     *
     * @param amtlichesKennzeichen the official license plate
     * @param hersteller the manufacturer of the vehicle
     * @param typ the type or model of the vehicle
     * @param hoechstgeschwindigkeit the maximum speed of the vehicle
     * @param wagnisskennziffer the risk classification number
     */
    public Fahrzeug(String amtlichesKennzeichen, String hersteller, String typ, int hoechstgeschwindigkeit, int wagnisskennziffer) {
        this.amtlichesKennzeichen = amtlichesKennzeichen;
        this.hersteller = hersteller;
        this.typ = typ;
        this.hoechstgeschwindigkeit = hoechstgeschwindigkeit;
        this.wagnisskennziffer = wagnisskennziffer;
    }
    /**
     * Default constructor for Fahrzeug.
     */
    public Fahrzeug() {
        super();
    }


    /**
     * Returns a string representation of the Fahrzeug.
     *
     * @return a string representation of the Fahrzeug
     */
    @Override
    public String toString() {
        return "\nFahrzeug: " +
                "\n\tAmtliches Kennzeichen: " + this.amtlichesKennzeichen +
                "\n\tHersteller: " + this.hersteller +
                "\n\tTyp: " + this.typ +
                "\n\tHöchstgeschwindigkeit: " + this.hoechstgeschwindigkeit +
                "\n\tWagnisskennziffer: " + this.wagnisskennziffer;
    }
}

