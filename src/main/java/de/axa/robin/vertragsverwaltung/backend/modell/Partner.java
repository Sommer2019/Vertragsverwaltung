package de.axa.robin.vertragsverwaltung.backend.modell;

import java.time.LocalDate;

public class Partner extends de.axa.robin.vertragsverwaltung.model.Partner {

    // Konstruktor
    public Partner(String vorname, String nachname, char geschlecht, LocalDate geburtsdatum,
                   String land, String strasse, String hausnummer, String plz, String stadt, String bundesland) {
        super.setVorname(vorname);
        super.setNachname(nachname);
        super.setGeschlecht(String.valueOf(geschlecht));
        super.setGeburtsdatum(geburtsdatum);
        super.setLand(land);
        super.setStrasse(strasse);
        super.setHausnummer(hausnummer);
        super.setPlz(plz);
        super.setStadt(stadt);
        super.setBundesland(bundesland);
    }

    public Partner(){
        super();
    }

    @Override
    public String toString() {
        return "\nPartner: " +
                "\n\tVorname: " + super.getVorname() +
                "\n\tNachname: " + super.getNachname() +
                "\n\tGeschlecht: " + super.getGeschlecht() +
                "\n\tGeburtsdatum: " + super.getGeburtsdatum() +
                "\n\tStraße: " + super.getStrasse() +
                "\n\tHausnummer: " + super.getHausnummer() +
                "\n\tPLZ: " + super.getPlz() +
                "\n\tStadt: " + super.getStadt() +
                "\n\tBundesland: " + super.getBundesland() +
                "\n\tLand: " + super.getLand();
    }
}
