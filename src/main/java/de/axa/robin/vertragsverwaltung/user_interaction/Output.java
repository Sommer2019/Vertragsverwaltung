package de.axa.robin.vertragsverwaltung.user_interaction;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class Output {
    public static void menu() {
        System.out.println("Was soll als nächstes getan werden? ");
        System.out.println("1 -- alle Verträge anzeigen");
        System.out.println("2 -- bestimmten Vertrag anzeigen");
        System.out.println("3 -- Vertrag erstellen");
        System.out.println("4 -- Bearbeiten");
        System.out.println("5 -- Vertrag löschen");
        System.out.println("6 -- beenden");
    }

    public static void newsum(BigDecimal summe) {
        System.out.println("Neue Summe aller Beiträge im Jahr: "+summe.setScale(2, RoundingMode.HALF_DOWN)+"€");
        System.out.println("-------------------------------");
    }
    public static void eventuell() {
        System.out.println();
        System.err.print("Eventuell ");
    }
    public static void invalidinput() {
        System.err.println("Ungültige Eingabe!");
    }
    public static void getvsnr(){
        System.out.println("Bitte gebe die 8-stellige VSNR ein: (oder 0 zum abbrechen) ");
    }
    public static void druckeVertrage(List<Vertrag> vertrage) {
        BigDecimal summe=BigDecimal.ZERO;
        for (Vertrag v : vertrage) {
            druckeVertrag(v);
            if(!v.getMonatlich()){
                summe = summe.add(BigDecimal.valueOf(v.getPreis()));
            }
            else{
                summe = summe.add(BigDecimal.valueOf(v.getPreis()*12));
            }
        }
        System.out.println("Summe aller Beiträge im Jahr: "+summe.setScale(2, RoundingMode.HALF_DOWN)+"€");
        System.out.println("-------------------------------");
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
    public static void skip(){
        System.err.println("Überprüfung überspringen? (y/n): ");
    }
    public static void done(String verarbeitet){
        System.out.println("Vertrag "+verarbeitet);
    }
    public static void cancel(){
        System.out.println("Abgebrochen");
    }

    public static void druckeVertrag(Vertrag v) {
        System.out.println(v.toString());
        if(v.getVersicherungsablauf().isBefore(LocalDate.now())){
            System.err.println("Vertrag abgelaufen!");
        }
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
    public static void editwhat(){
        System.out.println("Was möchten Sie bearbeiten?");
        System.out.println("1. Bestimmten Vertrag");
        System.out.println("2. Preisneuberechnung (alle Verträge)");
        System.out.println("3. Abbrechen");
    }
    public static void editMenu(){
        System.out.println("Was möchten Sie bearbeiten?");
        System.out.println("1. Allgemeine Daten");
        System.out.println("2. Personendaten");
        System.out.println("3. Fahrzeugdaten");
        System.out.println("4. Bearbeitung abschließen");
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
