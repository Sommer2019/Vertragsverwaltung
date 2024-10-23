package de.axa.robin.vertragsverwaltung.modell;

import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.time.LocalDate;

public class Vertrag {
    private int vsnr;
    private static double preis;
    boolean monatlich;
    private LocalDate versicherungsbeginn;
    private LocalDate versicherungsablauf;
    private LocalDate antragsDatum;
    private Fahrzeug fahrzeug;
    private Partner partner;

    // Konstruktor
    public Vertrag(int vsnr, boolean monatlich, double preis, LocalDate versicherungsbeginn, LocalDate versicherungsablauf, LocalDate antragsDatum, Fahrzeug fahrzeug, Partner partner) {
        this.vsnr = vsnr;
        Vertrag.preis = preis;
        this.monatlich = monatlich;
        this.versicherungsbeginn = versicherungsbeginn;
        this.versicherungsablauf = versicherungsablauf;
        this.antragsDatum = antragsDatum;
        this.fahrzeug = fahrzeug;
        this.partner = partner;
    }

    // Getter und Setter
    public int getVsnr() {
        return vsnr;
    }

    public void setVsnr(int vsnr) {
        this.vsnr = vsnr;
    }

    public static double getPreis() {
        return preis;
    }

    public static void setPreis(boolean monatlich, Partner partner, Fahrzeug fahrzeug) {
        double preis = 0;
        int alter = LocalDate.now().getYear() - partner.getGeburtsdatum().getYear();
        try {
            if(!monatlich){
                preis = (alter * 0.1 + fahrzeug.getHoechstgeschwindigkeit()  * 0.4)*24;
            } else{
                preis = (alter * 0.1 + fahrzeug.getHoechstgeschwindigkeit() * 0.4)*1.5;
            }
        } catch (Exception e) {
            Output.invalidinput();
        }
        Vertrag.preis = (Math.round(preis * 100) / 100.0);
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
}


