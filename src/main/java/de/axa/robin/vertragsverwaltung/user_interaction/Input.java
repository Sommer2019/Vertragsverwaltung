package de.axa.robin.vertragsverwaltung.user_interaction;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Validator;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Input {
    private final Output output = new Output();
    private final Validator validator = new Validator();
    private final Scanner scanner = new Scanner(System.in);

    public String getString(String prompt, String regex, boolean checkExistence, Vertragsverwaltung vertragsverwaltung, boolean isStringCheck, boolean isCountryCheck, boolean isBrandCheck) {
        boolean rerun = true;
        String input="";
        Pattern pattern = regex != null ? Pattern.compile(regex) : null;

        while (rerun) {
            input="";
            rerun = false;
            output.create(prompt);
            while (input.isEmpty()) {
                input = scanner.nextLine();
            }
            if (pattern != null && !pattern.matcher(input).matches()) {
                output.invalidinput();
                rerun = true;
            } else if (checkExistence && vertragsverwaltung.kennzeichenExistiert(input)) {
                output.error("Das Kennzeichen");
                rerun = true;
            } else if (isStringCheck && validator.string(input)) {
                output.invalidinput();
                rerun = true;
            } else if (isCountryCheck && !input.equals("Deutschland")) {
                output.invalidinput();
                rerun = true;
            } else if (isBrandCheck && !validator.isStringInJsonFile("'" + input + "'")) {
                output.eventuell();
                output.invalidinput();
                if (getChar(null, "") == 'y') {
                    return input;
                }
                else {
                    rerun=true;
                }
            }
        }
        return input;
    }

    public char getChar(Vertrag vertrag, String beschreibung) {
        boolean rerun = true;
        char input = 'q';
        while (rerun) {
            rerun = false;
            input = 'q';
            if (beschreibung.equals("das Geschlecht des Partners")) {
                output.create("das Geschlecht des Partners");
            } else if (beschreibung.equals("Abbuchung monatlich oder jährlich? (y/m): ")) {
                output.preisMJ();
            } else if (vertrag == null && beschreibung.isEmpty()) {
                output.skip();
            }  else {
                output.confirm(vertrag, beschreibung);
            }
            try {
                while(input=='q'){
                    input = Character.toLowerCase(scanner.next(".").charAt(0));
                }
                if (beschreibung.equals("das Geschlecht des Partners")) {
                    if (input != 'm' && input != 'w' && input != 'd') {
                        output.invalidinput();
                        rerun = true;
                    }
                } else if (beschreibung.equals("Preis monatlich oder jährlich")) {
                    if (input != 'y' && input != 'm') {
                        output.invalidinput();
                        rerun = true;
                    }
                } else {
                    if (input != 'y' && input != 'n') {
                        output.invalidinput();
                        rerun = true;
                    }
                }
            } catch (Exception e) {
                output.invalidinput();
                rerun = true;
            }
        }
        return input;
    }

    public <T> T getNumber(Class<T> type, String prompt, int min, int max, int exact, Vertragsverwaltung vertragsverwaltung, boolean checkExistence) {
        boolean rerun = true;
        T input = null;

        while (rerun) {
            rerun = false;
            if (!prompt.isEmpty()) {
                output.create(prompt);
            }
            try {
                if (type == Integer.class) {
                    input = type.cast(scanner.nextInt());
                    int value = (Integer) input;
                    if ((min != -1 && value < min) || (max != -1 && value > max) || (exact != -1 && value != exact)) {
                        output.invalidinput();
                        rerun = true;
                    } else if (checkExistence && !vertragsverwaltung.vertragExistiert(value)) {
                        output.invalidinput();
                        rerun = true;
                    } else if (prompt.equals("die PLZ") && String.valueOf(value).length() != 5) {
                        output.invalidinput();
                        rerun = true;
                    }
                } else if (type == Double.class) {
                    input = type.cast(scanner.nextDouble());
                    double value = (Double) input;
                    if ((min != -1 && value < min) || (max != -1 && value > max) || (exact != -1 && value != exact)) {
                        output.invalidinput();
                        rerun = true;
                    }
                }
            } catch (InputMismatchException e) {
                output.invalidinput();
                rerun = true;
                scanner.next();
            }
            scanner.nextLine();
        }
        return input;
    }

    public LocalDate getDate(String beschreibung, LocalDate minDatum, LocalDate maxDatum) {
        boolean rerun = true;
        LocalDate datum = LocalDate.now();
        while (rerun) {
            output.editTime(beschreibung);
            try {
                datum = LocalDate.parse(scanner.next());
                if ((minDatum == null || !datum.isBefore(minDatum)) && (maxDatum == null || !datum.isAfter(maxDatum))) {
                    rerun = false;
                } else {
                    output.invalidinput();
                }
            } catch (Exception e) {
                output.invalidinput();
            }
        }
        return datum;
    }
}
