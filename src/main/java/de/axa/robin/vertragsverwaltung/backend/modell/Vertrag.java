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
public class Vertrag extends de.axa.robin.vertragsverwaltung.model.Vertrag { //ToDo: Mapping von Generierten
    private int vsnr;
    private double preis;
    @JsonIgnore
    private String formattedPreis;

    // Konstruktor
    public Vertrag(int vsnr, boolean monatlich, double preis, LocalDate versicherungsbeginn, LocalDate versicherungsablauf, LocalDate antragsDatum, Fahrzeug fahrzeug, Partner partner) {
        this.vsnr = vsnr;
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

    @Override
    public String toString() {
        return "Vertragsdaten: " +
                "\n\tVertragsnummer: " + vsnr +
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
        return vsnr == vertrag.getVsnr();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(vsnr);
    }
}


