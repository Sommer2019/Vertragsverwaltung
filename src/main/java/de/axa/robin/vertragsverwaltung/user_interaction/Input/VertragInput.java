package de.axa.robin.vertragsverwaltung.user_interaction.Input;

import de.axa.robin.vertragsverwaltung.storage.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class VertragInput {
    public static int setvsnr() {
        Scanner scanner = new Scanner(System.in);
        int vsnr = 0;
        boolean rerun = true;
        while (rerun) {
            rerun = false;
            Output.create("die 8-stellige Versicherungsnummer");
            try {
                vsnr = scanner.nextInt();
                if (String.valueOf(vsnr).length() != 8) {
                    Output.invalidinput();
                    rerun = true;
                } else if (String.valueOf(vsnr).charAt(0) == '0') {
                    Output.invalidinput();
                    rerun = true;
                } else if (Vertragsverwaltung.vertragExistiert(vsnr)) {
                    Output.error("Die Versicherungsnummer");
                    rerun = true;
                }
            } catch (InputMismatchException e) {
                Output.invalidinput();
                rerun = true;
                scanner.next(); // Clear the invalid input
            }
        }
        return vsnr;
    }

    public static boolean preisym() {
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        char ym;
        boolean monatlich = false;
        while (rerun) {
            rerun = false;
            Output.preisMJ();
            try {
                ym = scanner.next(".").charAt(0);
                if (ym != 'y' && ym != 'Y' && ym != 'm' && ym != 'M') {
                    Output.invalidinput();
                    rerun = true;
                }
                if (ym == 'm' || ym == 'M') {
                    monatlich = true;
                }
            } catch (Exception e) {
                Output.invalidinput();
                rerun = true;
            }
        }
        return monatlich;
    }

    public static void beginn(Vertrag vertrag) {
        boolean rerun = true;
        Scanner scanner = new Scanner(System.in);
        while(rerun){
            Output.editTime("den neuen Versicherungsbeginn");
            try{
                vertrag.setVersicherungsbeginn(LocalDate.parse(scanner.nextLine()));
                rerun = false;
            } catch(Exception e) {
                Output.invalidinput();
            }
        }
    }
    public static void ende(Vertrag vertrag){
        boolean rerun = true;
        Scanner scanner = new Scanner(System.in);
        while(rerun) {
            Output.editTime("den neuen Versicherungsablauf");
            try {
                vertrag.setVersicherungsablauf(LocalDate.parse(scanner.nextLine()));
                rerun = false;
            } catch (Exception e) {
                Output.invalidinput();
            }
        }
    }
    public static void erstelltam(Vertrag vertrag){
        boolean rerun = true;
        Scanner scanner = new Scanner(System.in);
        while(rerun) {
            Output.editTime("das neue Antragsdatum");
            try {
                vertrag.setAntragsDatum(LocalDate.parse(scanner.nextLine()));
                rerun = false;
            } catch (Exception e) {
                Output.invalidinput();
            }
        }
    }
}
