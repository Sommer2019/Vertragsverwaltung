package de.axa.robin.vertragsverwaltung.user_interaction.Input;

import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.Vertrag;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Allgemein {

    public static int menu() {
        boolean rerun = true;
        int menunumber = 0;
        Scanner number = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            Output.menu();
            try {
                menunumber = number.nextInt();
            } catch (InputMismatchException e) {
                Output.invalidinput();
                rerun = true;
                number.next(); // Clear the invalid input
            }
        }
        return menunumber;
    }

    public static int getvsnr() {
        boolean rerun = true;
        int vsnr = 0;
        Scanner number = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            Output.getvsnr();
            try {
                vsnr = number.nextInt();
                if (vsnr == 0) {
                    return 0;
                }
                if (String.valueOf(vsnr).length() != 8 || !Vertragsverwaltung.vertragExistiert(vsnr)) {
                    Output.invalidinput();
                    rerun = true;
                }
            } catch (InputMismatchException e) {
                Output.invalidinput();
                rerun = true;
                number.next(); // Clear the invalid input
            }
        }
        return vsnr;
    }

    public static char delete(int vsnrdelete) {
        boolean rerun = true;
        char delete = 'n';
        Scanner character = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            Output.confirm(Vertragsverwaltung.getVertrag(vsnrdelete), "gelöscht");
            try {
                delete = character.next(".").charAt(0);
                if (delete != 'y' && delete != 'Y' && delete != 'n' && delete != 'N') {
                    Output.invalidinput();
                    rerun = true;
                }
            } catch (Exception e) {
                Output.invalidinput();
                rerun = true;
            }
        }
        return delete;
    }

    public static void createconfirm(Vertrag vertrag) {
        boolean rerun = true;
        char confirm;
        Scanner character = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            Output.confirm(vertrag, "erstellt");
            try {
                confirm = character.next(".").charAt(0);
                if (confirm != 'y' && confirm != 'Y' && confirm != 'n' && confirm != 'N') {
                    Output.invalidinput();
                    rerun = true;
                } else if (confirm == 'y' || confirm == 'Y') {
                    Vertragsverwaltung.vertragAnlegen(vertrag);
                    Output.done("erfolgreich erstellt.");
                }
            } catch (Exception e) {
                Output.invalidinput();
                rerun = true;
            }
        }
    }

    public static int getnumberinput() {
        boolean rerun = true;
        int number = 0;
        Scanner scanner = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            try {
                number = scanner.nextInt();
            } catch (InputMismatchException e) {
                Output.invalidinput();
                rerun = true;
                scanner.next(); // Clear the invalid input
            }
        }
        return number;
    }
}
