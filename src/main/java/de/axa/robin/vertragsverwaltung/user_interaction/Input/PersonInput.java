package de.axa.robin.vertragsverwaltung.user_interaction.Input;

import de.axa.robin.vertragsverwaltung.storage.Checker.Checker;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PersonInput {
    public static String name(String prefix){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String name = "";
        while (rerun) {
            rerun = false;
            Output.create("den "+prefix+"namen des Partners");
            name = scanner.nextLine();
            if(Checker.string(name)){
                Output.invalidinput();
                rerun = true;
            }
        }
        return name;
    }


    public static String land(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String land = "";
        while (rerun) {
            rerun = false;
            Output.create("das Land");
            land = scanner.nextLine();
            if(Checker.string(land)||!land.equals("DE")){
                Output.invalidinput();
                rerun = true;
            }
        }
        return land;
    }

    public static String bundesland(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String bundesland = "";
        while (rerun) {
            rerun = false;
            Output.create("das Bundesland");
            bundesland = scanner.nextLine();
            if(Checker.string(bundesland)){
                Output.invalidinput();
                rerun = true;
            }
        }
        return bundesland;
    }

    public static String stadt(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String stadt = "";
        while (rerun) {
            rerun = false;
            Output.create("die Stadt");
            stadt = scanner.nextLine();
            if(Checker.string(stadt)){
                Output.invalidinput();
                rerun = true;
            }
        }
        return stadt;
    }

    public static String strasse(){
        Scanner scanner = new Scanner(System.in);
        String input = "";
        Output.create("die Straße");
        while (input.isEmpty()) {
            input = scanner.nextLine();
        }
        return input;
    }

    public static int hausnummer(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        int hausnummer = 0;
        while (rerun) {
            rerun = false;
            Output.create("die Hausnummer");
            try {
                hausnummer = scanner.nextInt();
            } catch (InputMismatchException e) {
                Output.invalidinput();
                rerun = true;
                scanner.next();
            }
        }
        return hausnummer;
    }

    public static int plz() {
        Scanner scanner = new Scanner(System.in);
        int plz = 0;
        boolean rerun = true;
        while (rerun) {
            rerun = false;
            Output.create("die PLZ");
            try {
                plz = scanner.nextInt();
                if (String.valueOf(plz).length() != 5) {
                    Output.invalidinput();
                    rerun = true;
                }
            } catch (InputMismatchException e) {
                Output.invalidinput();
                rerun = true;
                scanner.next();
            }
            scanner.nextLine();
        }
        return plz;
    }

    public static LocalDate geburtsdatum(){
        Scanner scanner = new Scanner(System.in);
        LocalDate geburtsdatum = LocalDate.now();
        while (Checker.geburtsdatum(geburtsdatum)) {
            Output.create("das Geburtsdatum (YYYY-MM-DD)");
            try{
                geburtsdatum = LocalDate.parse(scanner.nextLine());
                if(Checker.geburtsdatum(geburtsdatum)){
                    Output.invalidinput();
                }
            } catch(Exception e) {
                Output.invalidinput();
            }
        }
        return geburtsdatum;
    }

}
