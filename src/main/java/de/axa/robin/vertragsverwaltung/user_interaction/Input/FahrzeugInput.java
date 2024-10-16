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
        Pattern pattern = Pattern.compile("^[A-Z]{1,3}-[A-Z]{1,2}\\d{1,4}$");

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
            if(Checker.string(marke)||!Checker.isStringInJsonFile(marke)) {
                Output.invalidinput();
                rerun = true;
            }
        }
        return marke;
    }
    public static String typ() {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        Output.create("den Typ");
        while (input.isEmpty()) {
            input = scanner.nextLine();
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
                if (hoechstgeschwindigkeit <50 || hoechstgeschwindigkeit >300){
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
