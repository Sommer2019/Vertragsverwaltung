package de.axa.robin.vertragsverwaltung.backend.modell;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;
@Getter
@Setter
public class Vertrag {
    private int vsnr;
    private double preis;
    private String formattedPreis;
    boolean monatlich;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate versicherungsbeginn;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate versicherungsablauf;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate antragsDatum;
    @Valid
    private Fahrzeug fahrzeug;
    @Valid
    private Partner partner;

    // Konstruktor
    public Vertrag(int vsnr, boolean monatlich, double preis, LocalDate versicherungsbeginn, LocalDate versicherungsablauf, LocalDate antragsDatum, Fahrzeug fahrzeug, Partner partner) {
        this.vsnr = vsnr;
        this.preis = preis;
        this.monatlich = monatlich;
        this.versicherungsbeginn = versicherungsbeginn;
        this.versicherungsablauf = versicherungsablauf;
        this.antragsDatum = antragsDatum;
        this.fahrzeug = fahrzeug;
        this.partner = partner;
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
        return vsnr == vertrag.vsnr;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(vsnr);
    }
}


