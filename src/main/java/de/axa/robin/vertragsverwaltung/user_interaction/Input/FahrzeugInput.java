package de.axa.robin.vertragsverwaltung.user_interaction.Input;

import de.axa.robin.vertragsverwaltung.storage.Checker.Checker;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FahrzeugInput {
    public static String kennzeichen() {
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String kennzeichen = "";
        Pattern pattern = Pattern.compile("^[\\p{Lu}]{1,3}-[\\p{Lu}]{1,2}\\d{1,4}$");

        while (rerun) {
            rerun = false;
            Output.create("das amtliche Kennzeichen");
            kennzeichen = scanner.nextLine();
            if (!pattern.matcher(kennzeichen).matches()) {
                Output.invalidinput();
                rerun = true;
            } else if (Vertragsverwaltung.kennzeichenExistiert(kennzeichen)) {
                Output.error("Das Kennzeichen");
                rerun = true;
            }
        }
        return kennzeichen;
    }
    public static String marke(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String marke = "";
        while (rerun) {
            rerun = false;
            Output.create("die Marke");
            marke = scanner.nextLine();
            if(!Checker.isStringInJsonFile(marke)) {
                Output.eventuell();
                Output.invalidinput();
                rerun = !Allgemein.skip();
            }
        }
        return marke;
    }
    public static String typ() {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        boolean rerun = true;
        while (input.isEmpty()||rerun) {
            rerun = false;
            Output.create("den Typ");
            input = scanner.nextLine();
            if(!input.matches("^[a-zA-Z0-9\\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ]+$")){
                Output.invalidinput();
                rerun = !Allgemein.skip();
            }
        }
        return input;
    }
    public static int maxspeed(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        int hoechstgeschwindigkeit = 0;
        while (rerun) {
            rerun = false;
            Output.create("die Höchstgeschwindigkeit");
            try {
                hoechstgeschwindigkeit = scanner.nextInt();
                if (hoechstgeschwindigkeit <50 || hoechstgeschwindigkeit >250){
                    Output.invalidinput();
                    rerun = true;
                }
            } catch (InputMismatchException e) {
                Output.invalidinput();
                rerun = true;
                scanner.next();
            }
        }
        return hoechstgeschwindigkeit;
    }

    public static int wkz(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        int wkz = 0;
        while (rerun) {
            rerun = false;
            Output.create("die Wagnisskennziffer");
            try {
                wkz = scanner.nextInt();
                if(wkz!=112){
                    Output.invalidinput();
                    rerun = true;
                }
            } catch (InputMismatchException e) {
                Output.invalidinput();
                rerun = true;
                scanner.next();
            }

        }
        return wkz;
    }
}
