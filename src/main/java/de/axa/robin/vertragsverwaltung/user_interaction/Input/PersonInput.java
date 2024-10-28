package de.axa.robin.vertragsverwaltung.user_interaction.Input;

import de.axa.robin.vertragsverwaltung.storage.Checker.Checker;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PersonInput {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final Checker checker = new Checker();

    public String name(String prefix){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String name = "";
        while (rerun) {
            rerun = false;
            output.create("den "+prefix+"namen des Partners");
            name = scanner.nextLine();
            if(checker.string(name)){
                output.invalidinput();
                rerun = true;
            }
        }
        return name;
    }

    public char geschlecht(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        char gender = 'm';
        while (rerun) {
            rerun = false;
            output.create("das Geschlecht des Partners");
            try{
                gender = Character.toLowerCase(scanner.next(".").charAt(0));
                if(gender != 'm'&& gender != 'w'&& gender != 'd'){
                    output.invalidinput();
                    rerun = true;
                }
            }
            catch (InputMismatchException e){
                output.invalidinput();
                rerun = true;
            }
        }
        return gender;
    }

    public String land(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String land = "";
        while (rerun) {
            rerun = false;
            output.create("das Land");
            land = scanner.nextLine();
            if(checker.string(land)||!land.equals("Deutschland")){
                output.invalidinput();
                rerun = true;
            }
        }
        return land;
    }

    public String bundesland(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String bundesland = "";
        while (rerun) {
            rerun = false;
            output.create("das Bundesland");
            bundesland = scanner.nextLine();
            if(checker.string(bundesland)){
                output.invalidinput();
                rerun = true;
            }
        }
        return bundesland;
    }

    public String stadt(){
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        String stadt = "";
        while (rerun) {
            rerun = false;
            output.create("die Stadt");
            stadt = scanner.nextLine();
            if(checker.string(stadt)){
                output.invalidinput();
                rerun = true;
            }
        }
        return stadt;
    }

    public String strasse(){
        Scanner scanner = new Scanner(System.in);
        String input = "";
        output.create("die Straße");
        while (input.isEmpty()) {
            input = scanner.nextLine();
        }
        return input;
    }

    public String hausnummer() {
        Scanner scanner = new Scanner(System.in);
        while(true){
            output.create("die Hausnummer");
            String input = scanner.nextLine();

            if (input.matches("[a-zA-Z0-9]+")) {
                return input;
            } else {
                output.eventuell();
                output.invalidinput();
            }
        }
    }

    public int plz() {
        Scanner scanner = new Scanner(System.in);
        int plz = 0;
        boolean rerun = true;
        while (rerun) {
            rerun = false;
            output.create("die PLZ");
            try {
                plz = scanner.nextInt();
                if (String.valueOf(plz).length() != 5) {
                    output.invalidinput();
                    rerun = true;
                }
            } catch (InputMismatchException e) {
                output.invalidinput();
                rerun = true;
                scanner.next();
            }
            scanner.nextLine();
        }
        return plz;
    }

    public LocalDate geburtsdatum(){
        Scanner scanner = new Scanner(System.in);
        LocalDate geburtsdatum = LocalDate.now();
        while (checker.geburtsdatum(geburtsdatum)) {
            output.create("das Geburtsdatum (YYYY-MM-DD)");
            try{
                geburtsdatum = LocalDate.parse(scanner.nextLine());
                if(checker.geburtsdatum(geburtsdatum)){
                    output.invalidinput();
                }
            } catch(Exception e) {
                output.invalidinput();
            }
        }
        return geburtsdatum;
    }

}
