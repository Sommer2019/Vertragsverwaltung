package de.axa.robin.vertragsverwaltung.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a contract (Vertrag) with various details such as insurance start and end dates,
 * vehicle and partner information, and pricing details.
 */
@Getter
@Setter
public class Vertrag {
    @JsonIgnore
    private int vsnr;
    private boolean monatlich;
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
    private double preis;
    @JsonIgnore
    private String formattedPreis;

    /**
     * Constructs a new Vertrag with the specified details.
     *
     * @param vsnr                the contract number
     * @param monatlich           indicates if the billing is monthly
     * @param preis               the price of the contract
     * @param versicherungsbeginn the insurance start date
     * @param versicherungsablauf the insurance end date
     * @param antragsDatum        the application date
     * @param fahrzeug            the vehicle information
     * @param partner             the partner information
     */
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

    /**
     * Constructs a new Vertrag with the specified vehicle and partner information.
     *
     * @param fahrzeug the vehicle information
     * @param partner  the partner information
     */
    public Vertrag(Fahrzeug fahrzeug, Partner partner) {
        this.fahrzeug = fahrzeug;
        this.partner = partner;
    }

    /**
     * Default constructor for Vertrag.
     */
    public Vertrag() {
        super();
    }

    /**
     * Returns a string representation of the Vertrag.
     *
     * @return a string representation of the Vertrag
     */
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

    /**
     * Checks if this Vertrag is equal to another object.
     *
     * @param o the object to compare with
     * @return true if this Vertrag is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertrag vertrag = (Vertrag) o;
        return vsnr == vertrag.getVsnr();
    }

    /**
     * Returns the hash code value for this Vertrag.
     *
     * @return the hash code value for this Vertrag
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(vsnr);
    }
}


