package de.axa.robin.vertragsverwaltung.modell;
import java.time.LocalDate;

public class Partner {
    private String vorname;
    private String nachname;
    private String land;
    private String bundesland;
    private String stadt;
    private String strasse;
    private int hausnummer;
    private int plz;
    private LocalDate geburtsdatum;

    // Konstruktor
    public Partner(String vorname, String nachname, String land, String bundesland, String stadt, String strasse, int hausnummer, int plz, LocalDate geburtsdatum) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.land = land;
        this.bundesland = bundesland;
        this.stadt = stadt;
        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.plz = plz;
        this.geburtsdatum = geburtsdatum;
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

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getBundesland() {
        return bundesland;
    }

    public void setBundesland(String bundesland) {
        this.bundesland = bundesland;
    }

    public String getStadt() {
        return stadt;
    }

    public void setStadt(String stadt) {
        this.stadt = stadt;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public int getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(int hausnummer) {
        this.hausnummer = hausnummer;
    }

    public int getPlz() {
        return plz;
    }

    public void setPlz(int plz) {
        this.plz = plz;
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    public void setGeburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }
}
