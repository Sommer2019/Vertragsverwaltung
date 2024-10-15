package de.axa.robin.vertragsverwaltung.user_interaction;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Output {
    public static void menu() {
        System.out.println("Was soll als nächstes getan werden? ");
        System.out.println("1 -- alle Verträge anzeigen");
        System.out.println("2 -- bestimmten Vertrag anzeigen");
        System.out.println("3 -- Vertrag erstellen");
        System.out.println("4 -- Vertrag bearbeiten");
        System.out.println("5 -- Vertrag löschen");
        System.out.println("6 -- beenden");
    }

    public static void invalidinput() {
        System.err.println("Ungültige Eingabe!");
    }
    public static void getvsnr(){
        System.out.println("Bitte gebe die 8-stellige VSNR ein: (oder 0 zum abbrechen) ");
    }
    public static void druckeVertrage(List<Vertrag> vertrage) {
        Set<Vertrag> printedContracts = new HashSet<>();
        for (Vertrag v : vertrage) {
            if (!printedContracts.contains(v)) {
                druckeVertrag(v);
                printedContracts.add(v);
            }
        }
    }

    public static void create(String input){
        System.out.println("Geben Sie "+input+" ein:");
    }
    public static void error(String errorMessage) {
        System.err.println(errorMessage+" existiert bereits.");
    }
    public static void confirm(Vertrag v,String handle){
        druckeVertrag(v);
        System.out.println("Soll dieser Vertrag wirklich "+handle+" werden? (y/n): ");
    }
    public static void done(String verarbeitet){
        System.out.println("Vertrag "+verarbeitet);
    }
    public static void cancel(){
        System.out.println("Abgebrochen");
    }

    public static void druckeVertrag(Vertrag v) {
        System.out.println("Vertragsnummer: " + v.getVsnr());
        System.out.println("Preis: " + Vertrag.getPreis());
        System.out.print("Abrechnungszeitraum: ");
        if (v.getMonatlich()) {
            System.out.print("monatlich");
        } else {
            System.out.print("jährlich");
        }
        System.out.println();
        System.out.println("Versicherungsbeginn: " + v.getVersicherungsbeginn());
        System.out.println("Versicherungsablauf: " + v.getVersicherungsablauf());
        System.out.println("Antragsdatum: " + v.getAntragsDatum());
        System.out.println("Fahrzeug:");
        System.out.println("  Amtliches Kennzeichen: " + v.getFahrzeug().getAmtlichesKennzeichen());
        System.out.println("  Hersteller: " + v.getFahrzeug().getHersteller());
        System.out.println("  Typ: " + v.getFahrzeug().getTyp());
        System.out.println("  Hoechstgeschwindigkeit: " + v.getFahrzeug().getHoechstgeschwindigkeit());
        System.out.println("  Wagnisskennziffer: " + v.getFahrzeug().getWagnisskennziffer());
        System.out.println("Partner:");
        System.out.println("  Vorname: " + v.getPartner().getVorname());
        System.out.println("  Nachname: " + v.getPartner().getNachname());
        System.out.println("  Bundesland: " + v.getPartner().getBundesland());
        System.out.println("  Stadt: " + v.getPartner().getStadt());
        System.out.println("  Strasse: " + v.getPartner().getStrasse());
        System.out.println("  Hausnummer: " + v.getPartner().getHausnummer());
        System.out.println("  PLZ: " + v.getPartner().getPlz());
        System.out.println("  Geburtsdatum: " + v.getPartner().getGeburtsdatum());
        System.out.println("-------------------------------");
    }
    public static void preisMJ(){
        System.out.println("Abbuchung monatlich oder jährlich? (y/m): ");
    }
    public static void preis(boolean monatlich, double preis){
        System.out.print("Berechneter Preis: "+preis+"€");
        if (monatlich){
            System.out.print(" im Monat.");
        }
        else{
            System.out.print(" im Jahr.");
        }
        System.out.println();
    }
    public static void editMenu(){
        System.out.println("Was möchten Sie bearbeiten?");
        System.out.println("1. Allgemeine Daten");
        System.out.println("2. Personendaten");
        System.out.println("3. Fahrzeugdaten");
        System.out.println("4. Versicherungsnummer");
        System.out.println("5. Bearbeitung abschließen");
    }
    public static void editDates(){
        System.out.println("Welche allgemeinen Daten möchten Sie bearbeiten?");
        System.out.println("1. Versicherungsbeginn");
        System.out.println("2. Versicherungsablauf");
        System.out.println("3. Antragsdatum");
        System.out.println("4. Abrechnungszeitraum");
    }
    public static void editTime(String thing){
        System.out.println("Geben Sie "+thing+" ein (YYYY-MM-DD):");
    }
    public static void editPerson() {
        System.out.println("Welche Personendaten möchten Sie bearbeiten?");
        System.out.println("1. Vorname");
        System.out.println("2. Nachname");
        System.out.println("3. Geschlecht");
        System.out.println("4. Geburtsdatum");
        System.out.println("5. Adresse");
    }
    public static void editFahrzeug() {
        System.out.println("Welche Fahrzeugdaten möchten Sie bearbeiten?");
        System.out.println("1. Kennzeichen");
        System.out.println("2. Marke");
        System.out.println("3. Typ");
        System.out.println("4. Höchstgeschwindigkeit");
        System.out.println("5. Wagnisskennziffer");
    }

    public static void connection(String errormessage) {
        System.err.println("Connection timed out: " + errormessage);
    }
    public static void errorvalidate(String message) {
        System.err.println("Error: "+message);
    }
}
