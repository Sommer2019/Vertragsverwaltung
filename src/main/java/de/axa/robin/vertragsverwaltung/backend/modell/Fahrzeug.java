package de.axa.robin.vertragsverwaltung.backend.modell;

public class Fahrzeug extends de.axa.robin.vertragsverwaltung.model.Fahrzeug {

    // Konstruktor
    public Fahrzeug(String amtlichesKennzeichen, String hersteller, String typ, int hoechstgeschwindigkeit, int wagnisskennziffer) {
        super.setAmtlichesKennzeichen(amtlichesKennzeichen);
        super.setHersteller(hersteller);
        super.setTyp(typ);
        super.setHoechstgeschwindigkeit(hoechstgeschwindigkeit);
        super.setWagnisskennziffer(wagnisskennziffer);
    }
    public Fahrzeug() {
        super();
    }

    @Override
    public String toString() {
        return "\nFahrzeug: " +
                "\n\tAmtliches Kennzeichen: " + super.getAmtlichesKennzeichen() +
                "\n\tHersteller: " + super.getHersteller() +
                "\n\tTyp: " + super.getTyp() +
                "\n\tHöchstgeschwindigkeit: " + super.getHoechstgeschwindigkeit() +
                "\n\tWagnisskennziffer: " + super.getWagnisskennziffer();
    }
}

