package de.axa.robin.vertragsverwaltung.backend.modell;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

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

    // Getter und Setter
    public int getVsnr() {
        return vsnr;
    }

    public void setVsnr(int vsnr) {
        this.vsnr = vsnr;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public boolean getMonatlich() {
        return monatlich;
    }

    public void setMonatlich(boolean monatlich) {
        this.monatlich = monatlich;
    }

    public LocalDate getVersicherungsbeginn() {
        return versicherungsbeginn;
    }

    public void setVersicherungsbeginn(LocalDate versicherungsbeginn) {
        this.versicherungsbeginn = versicherungsbeginn;
    }

    public LocalDate getVersicherungsablauf() {
        return versicherungsablauf;
    }

    public void setVersicherungsablauf(LocalDate versicherungsablauf) {
        this.versicherungsablauf = versicherungsablauf;
    }

    public LocalDate getAntragsDatum() {
        return antragsDatum;
    }

    public void setAntragsDatum(LocalDate antragsDatum) {
        this.antragsDatum = antragsDatum;
    }

    public Fahrzeug getFahrzeug() {
        return fahrzeug;
    }

    public void setFahrzeug(Fahrzeug fahrzeug) {
        this.fahrzeug = fahrzeug;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public String getFormattedPreis() {
        return formattedPreis;
    }

    public void setFormattedPreis(String formattedPreis) {
        this.formattedPreis = formattedPreis;
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


