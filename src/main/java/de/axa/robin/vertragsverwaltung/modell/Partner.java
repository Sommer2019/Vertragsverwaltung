package de.axa.robin.vertragsverwaltung.modell;

import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class Partner {
    @Pattern(regexp="^[a-zA-Z0-9\\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ'-]+$")
    private String vorname;
    @Pattern(regexp="^[a-zA-Z0-9\\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ'-]+$")
    private String nachname;
    private char geschlecht;
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
        this.geschlecht = geschlecht;
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

    // Getter und Setter
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public char getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(char geschlecht) {
        this.geschlecht = geschlecht;
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    public void setGeburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getStadt() {
        return stadt;
    }

    public void setStadt(String stadt) {
        this.stadt = stadt;
    }

    public String getBundesland() {
        return bundesland;
    }

    public void setBundesland(String bundesland) {
        this.bundesland = bundesland;
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
