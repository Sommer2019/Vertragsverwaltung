package de.axa.robin.vertragsverwaltung.backend.modell;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class Vertrag {
    @JsonIgnore
    private int vsnr;
    private boolean monatlich;
    private LocalDate versicherungsbeginn;
    private LocalDate versicherungsablauf;
    private LocalDate antragsDatum;
    private Fahrzeug fahrzeug;
    private Partner partner;
    private double preis;
    @JsonIgnore
    private String formattedPreis;

    // Konstruktor
    public Vertrag(int vsnr, boolean monatlich, double preis, LocalDate versicherungsbeginn, LocalDate versicherungsablauf, LocalDate antragsDatum, Fahrzeug fahrzeug, Partner partner) {
        this.vsnr = vsnr;
        this.monatlich = monatlich;
        this.versicherungsbeginn = versicherungsbeginn;
        this.versicherungsablauf = versicherungsablauf;
        this.antragsDatum = antragsDatum;
        this.fahrzeug = fahrzeug;
        this.partner = partner;
        this.preis = preis;
    }
    public Vertrag(Fahrzeug fahrzeug, Partner partner) {
        this.fahrzeug = fahrzeug;
        this.partner = partner;
    }


    public Vertrag() {
        super();
    }

    @Override
    public String toString() {
        return "Vertragsdaten: " +
                "\n\tVertragsnummer: " + vsnr +
                "\n\tPreis: " + preis + "€" +
                "\n\tAbrechnungszeitraum monatlich: " + monatlich +
                "\n\tVersicherungsbeginn: " + versicherungsbeginn +
                "\n\tVersicherungsablauf: " + versicherungsablauf +
                "\n\tAntragsdatum: " + antragsDatum +
                fahrzeug +
                partner +
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


