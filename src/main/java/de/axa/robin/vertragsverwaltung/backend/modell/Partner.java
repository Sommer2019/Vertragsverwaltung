package de.axa.robin.vertragsverwaltung.backend.modell;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Partner{
    private String vorname;
    private String nachname;
    private String geschlecht;
    private LocalDate geburtsdatum;
    private String land;
    private String strasse;
    private String hausnummer;
    private String plz;
    private String stadt;
    private String bundesland;
    // Konstruktor
    public Partner(String vorname, String nachname, char geschlecht, LocalDate geburtsdatum,
                   String land, String strasse, String hausnummer, String plz, String stadt, String bundesland) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.geschlecht = String.valueOf(geschlecht);
        this.geburtsdatum = geburtsdatum;
        this.land = land;
        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.plz = plz;
        this.stadt = stadt;
        this.bundesland = bundesland;
    }

    public Partner(){
        super();
    }

    @Override
    public String toString() {
        return "\nPartner: " +
                "\n\tVorname: " + vorname +
                "\n\tNachname: " + nachname +
                "\n\tGeschlecht: " + geschlecht +
                "\n\tGeburtsdatum: " + geburtsdatum +
                "\n\tStraße: " + strasse +
                "\n\tHausnummer: " + hausnummer +
                "\n\tPLZ: " + plz +
                "\n\tStadt: " + stadt +
                "\n\tBundesland: " + bundesland +
                "\n\tLand: " + land;
    }
}
