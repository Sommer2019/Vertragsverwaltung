package de.axa.robin.vertragsverwaltung.backend.modell;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class Partner {
    @Pattern(regexp="^[a-zA-Z0-9\\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ'-]+$")
    private String vorname;
    @Pattern(regexp="^[a-zA-Z0-9\\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ'-]+$")
    private String nachname;
    private char geschlecht;
    @DateTimeFormat(pattern="yyyy-MM-dd")
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
