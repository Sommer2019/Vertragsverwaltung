package de.axa.robin.vertragsverwaltung.user_interaction.Input;

import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.util.InputMismatchException;
import java.util.Scanner;

public class AllgemeinInput {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung();

    public int menu() {
        boolean rerun = true;
        int menunumber = 0;
        Scanner number = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            output.menu();
            try {
                menunumber = number.nextInt();
            } catch (InputMismatchException e) {
                output.invalidinput();
                rerun = true;
                number.next(); // Clear the invalid input
            }
        }
        return menunumber;
    }

    public int getvsnr() {
        boolean rerun = true;
        int vsnr = 0;
        Scanner number = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            output.getvsnr();
            try {
                vsnr = number.nextInt();
                if (vsnr == 0) {
                    return 0;
                }
                if (String.valueOf(vsnr).length() != 8 || !vertragsverwaltung.vertragExistiert(vsnr)) {
                    output.invalidinput();
                    rerun = true;
                }
            } catch (InputMismatchException e) {
                output.invalidinput();
                rerun = true;
                number.next(); // Clear the invalid input
            }
        }
        return vsnr;
    }

    public char delete(int vsnrdelete) {
        boolean rerun = true;
        char delete = 'n';
        Scanner character = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            output.confirm(vertragsverwaltung.getVertrag(vsnrdelete), "gelöscht");
            try {
                delete = character.next(".").charAt(0);
                if (delete != 'y' && delete != 'Y' && delete != 'n' && delete != 'N') {
                    output.invalidinput();
                    rerun = true;
                }
            } catch (Exception e) {
                output.invalidinput();
                rerun = true;
            }
        }
        return delete;
    }

    public boolean skip() {
        boolean rerun = true;
        char confirm;
        Scanner character = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            output.skip();
            try {
                confirm = character.next(".").charAt(0);
                if (confirm != 'y' && confirm != 'Y' && confirm != 'n' && confirm != 'N') {
                    output.invalidinput();
                    rerun = true;
                } else if (confirm == 'y' || confirm == 'Y') {
                    return true;
                }
            } catch (Exception e) {
                output.invalidinput();
                rerun = true;
            }
        }
        return false;
    }

    public void createconfirm(Vertrag vertrag) {
        boolean rerun = true;
        char confirm;
        Scanner character = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            output.confirm(vertrag, "erstellt");
            try {
                confirm = character.next(".").charAt(0);
                if (confirm != 'y' && confirm != 'Y' && confirm != 'n' && confirm != 'N') {
                    output.invalidinput();
                    rerun = true;
                } else if (confirm == 'y' || confirm == 'Y') {
                    vertragsverwaltung.vertragAnlegen(vertrag);
                    output.done("erfolgreich erstellt.");
                }
                else{
                    output.done("wurde nicht erstellt.");
                }
            } catch (Exception e) {
                output.invalidinput();
                rerun = true;
            }
        }
    }

    public int getnumberinput() {
        boolean rerun = true;
        int number = 0;
        Scanner scanner = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            try {
                number = scanner.nextInt();
            } catch (InputMismatchException e) {
                output.invalidinput();
                rerun = true;
                scanner.next(); // Clear the invalid input
            }
        }
        return number;
    }

    public double getdoubleinput() {
        boolean rerun = true;
        double number = 0;
        Scanner scanner = new Scanner(System.in);

        while (rerun) {
            rerun = false;
            try {
                number = scanner.nextDouble();
            } catch (InputMismatchException e) {
                output.invalidinput();
                rerun = true;
                scanner.next(); // Clear the invalid input
            }
        }
        return number;
    }
}
