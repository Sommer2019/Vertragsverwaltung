package de.axa.robin.vertragsverwaltung.backend.modell;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import de.axa.robin.vertragsverwaltung.model.Fahrzeug;
import de.axa.robin.vertragsverwaltung.model.Partner;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class Vertrag extends de.axa.robin.vertragsverwaltung.model.Vertrag {
    private double preis;
    @JsonIgnore
    private String formattedPreis;

    // Konstruktor
    public Vertrag(int vsnr, boolean monatlich, double preis, LocalDate versicherungsbeginn, LocalDate versicherungsablauf, LocalDate antragsDatum, Fahrzeug fahrzeug, Partner partner) {
        super.setVsnr(vsnr);
        this.preis = preis;
        super.setMonatlich(monatlich);
        super.setVersicherungsbeginn(versicherungsbeginn);
        super.setVersicherungsablauf(versicherungsablauf);
        super.setAntragsDatum(antragsDatum);
        super.setFahrzeug(fahrzeug);
        super.setPartner(partner);
    }

    public Vertrag(Fahrzeug fahrzeug, Partner partner) {
        super.setFahrzeug(fahrzeug);
        super.setPartner(partner);
    }

    public Vertrag() {
        super();
    }
    @JsonIgnore
    public char getGender() {
        return super.getPartner().getGeschlecht().charAt(0);
    }
    @JsonIgnore
    public void setGender (char gender) {
        super.getPartner().setGeschlecht(String.valueOf(gender));
    }

    @Override
    public String toString() {
        return "Vertragsdaten: " +
                "\n\tVertragsnummer: " + super.getVsnr() +
                "\n\tPreis: " + preis + "€" +
                "\n\tAbrechnungszeitraum monatlich: " + super.getMonatlich() +
                "\n\tVersicherungsbeginn: " + super.getVersicherungsbeginn() +
                "\n\tVersicherungsablauf: " + super.getVersicherungsablauf() +
                "\n\tAntragsdatum: " + super.getAntragsDatum() +
                super.getFahrzeug() +
                super.getPartner() +
                "\n\t-------------------------------";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertrag vertrag = (Vertrag) o;
        return super.getVsnr().equals(vertrag.getVsnr());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.getVsnr());
    }
}


