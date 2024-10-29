package de.axa.robin.vertragsverwaltung.user_interaction.Input;

import de.axa.robin.vertragsverwaltung.storage.Checker.Checker;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FahrzeugInput {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final Checker checker = new Checker();
    private final AllgemeinInput allgemeinInput = new AllgemeinInput();

    public String kennzeichen(Vertragsverwaltung vertragsverwaltung) {
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String kennzeichen = "";
        Pattern pattern = Pattern.compile("^[\\p{Lu}]{1,3}-[\\p{Lu}]{1,2}\\d{1,4}$");

        while (rerun) {
            rerun = false;
            output.create("das amtliche Kennzeichen");
            kennzeichen = scanner.nextLine();
            if (!pattern.matcher(kennzeichen).matches()) {
                output.invalidinput();
                rerun = true;
            } else if (vertragsverwaltung.kennzeichenExistiert(kennzeichen)) {
                output.error("Das Kennzeichen");
                rerun = true;
            }
        }
        return kennzeichen;
    }
    public String marke(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String marke = "";
        while (rerun) {
            rerun = false;
            output.create("die Marke");
            marke = scanner.nextLine();
            if(!checker.isStringInJsonFile(marke)) {
                output.eventuell();
                output.invalidinput();
                rerun = !allgemeinInput.skip();
            }
        }
        return marke;
    }
    public String typ() {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        boolean rerun = true;
        while (input.isEmpty()||rerun) {
            rerun = false;
            output.create("den Typ");
            input = scanner.nextLine();
            if(!input.matches("^[a-zA-Z0-9\\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ]+$")){
                output.invalidinput();
                rerun = !allgemeinInput.skip();
            }
        }
        return input;
    }
    public int maxspeed(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        int hoechstgeschwindigkeit = 0;
        while (rerun) {
            rerun = false;
            output.create("die Höchstgeschwindigkeit");
            try {
                hoechstgeschwindigkeit = scanner.nextInt();
                if (hoechstgeschwindigkeit <50 || hoechstgeschwindigkeit >250){
                    output.invalidinput();
                    rerun = true;
                }
            } catch (InputMismatchException e) {
                output.invalidinput();
                rerun = true;
                scanner.next();
            }
        }
        return hoechstgeschwindigkeit;
    }

    public int wkz(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        int wkz = 0;
        while (rerun) {
            rerun = false;
            output.create("die Wagnisskennziffer");
            try {
                wkz = scanner.nextInt();
                if(wkz!=112){
                    output.invalidinput();
                    rerun = true;
                }
            } catch (InputMismatchException e) {
                output.invalidinput();
                rerun = true;
                scanner.next();
            }

        }
        return wkz;
    }
}
