package de.axa.robin.vertragsverwaltung.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Partner class represents a partner with their details.
 */
@Getter
@Setter
public class Partner {
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

    /**
     * Constructs a new Partner with the specified details.
     *
     * @param vorname the first name of the partner
     * @param nachname the last name of the partner
     * @param geschlecht the gender of the partner
     * @param geburtsdatum the birth date of the partner
     * @param land the country of the partner
     * @param strasse the street of the partner
     * @param hausnummer the house number of the partner
     * @param plz the postal code of the partner
     * @param stadt the city of the partner
     * @param bundesland the state of the partner
     */
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

    /**
     * Default constructor for Partner.
     */
    public Partner() {
        super();
    }

    /**
     * Returns a string representation of the Partner.
     *
     * @return a string representation of the Partner
     */
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
