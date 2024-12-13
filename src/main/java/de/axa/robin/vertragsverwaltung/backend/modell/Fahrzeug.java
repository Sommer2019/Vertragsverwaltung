package de.axa.robin.vertragsverwaltung.backend.modell;

import jakarta.validation.constraints.Pattern;

public class Fahrzeug {
    @Pattern(regexp= "^\\p{Lu}{1,3}-\\p{Lu}{1,2}\\d{1,4}[EH]?$")
    private String amtlichesKennzeichen;
    private String hersteller;
    @Pattern(regexp = "^[a-zA-Z0-9\\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ]+$")
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

    // Getter und Setter
    public String getAmtlichesKennzeichen() {
        return amtlichesKennzeichen;
    }

    public void setAmtlichesKennzeichen(String amtlichesKennzeichen) {
        this.amtlichesKennzeichen = amtlichesKennzeichen;
    }

    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public int getHoechstgeschwindigkeit() {
        return hoechstgeschwindigkeit;
    }

    public void setHoechstgeschwindigkeit(int hoechstgeschwindigkeit) {
        this.hoechstgeschwindigkeit = hoechstgeschwindigkeit;
    }

    public int getWagnisskennziffer() {
        return wagnisskennziffer;
    }

    public void setWagnisskennziffer(int wagnisskennziffer) {
        this.wagnisskennziffer = wagnisskennziffer;
    }

    @Override
    public String toString() {
        return "\nFahrzeug: " +
                "\n\tAmtliches Kennzeichen: " + amtlichesKennzeichen +
                "\n\tHersteller: " + hersteller +
                "\n\tTyp: " + typ +
                "\n\tHöchstgeschwindigkeit: " + hoechstgeschwindigkeit +
                "\n\tWagnisskennziffer: " + wagnisskennziffer;
    }
}

