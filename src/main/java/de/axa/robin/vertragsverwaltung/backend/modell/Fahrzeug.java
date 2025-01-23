package de.axa.robin.vertragsverwaltung.backend.modell;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

