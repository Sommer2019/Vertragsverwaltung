package de.axa.robin.vertragsverwaltung.backend.modell;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fahrzeug {
    private String amtlichesKennzeichen;
    private String hersteller;
    private String typ;
    private int hoechstgeschwindigkeit;
    private int wagnisskennziffer;

    // Konstruktor
    public Fahrzeug(String amtlichesKennzeichen, String hersteller, String typ, int hoechstgeschwindigkeit, int wagnisskennziffer) {
        this.amtlichesKennzeichen = amtlichesKennzeichen;
        this.hersteller = hersteller;
        this.typ = typ;
        this.hoechstgeschwindigkeit = hoechstgeschwindigkeit;
        this.wagnisskennziffer = wagnisskennziffer;
    }
    public Fahrzeug() {
        super();
    }

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

