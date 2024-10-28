package de.axa.robin.vertragsverwaltung.user_interaction.Input;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.time.LocalDate;
import java.util.Scanner;

public class VertragInput {
    ////Klassen einlesen////
    private final Output output = new Output();

    public boolean preisYM() {
        Scanner scanner = new Scanner(System.in);
        boolean rerun = true;
        char ym;
        boolean monatlich = false;
        while (rerun) {
            rerun = false;
            output.preisMJ();
            try {
                ym = scanner.next(".").charAt(0);
                if (ym != 'y' && ym != 'Y' && ym != 'm' && ym != 'M') {
                    output.invalidinput();
                    rerun = true;
                }
                if (ym == 'm' || ym == 'M') {
                    monatlich = true;
                }
            } catch (Exception e) {
                output.invalidinput();
                rerun = true;
            }
        }
        return monatlich;
    }
    public LocalDate beginn() {
        boolean rerun = true;
        Scanner scanner = new Scanner(System.in);
        LocalDate start = LocalDate.now();
        while(rerun){
            output.editTime("den Versicherungsbeginn");
            try{
                start = LocalDate.parse(scanner.next());
                if(!start.isBefore(LocalDate.now())){
                    rerun = false;
                }
                else{
                    output.invalidinput();
                }
            } catch(Exception e) {
                output.invalidinput();
            }
        }
        return start;
    }

    public void editbeginn(Vertrag vertrag) {
        boolean rerun = true;
        Scanner scanner = new Scanner(System.in);
        while(rerun){
            output.editTime("den Versicherungsbeginn");
            try{
                LocalDate start = LocalDate.parse(scanner.next());
                if(start.isBefore(vertrag.getVersicherungsablauf())&&!start.isBefore(vertrag.getAntragsDatum())){
                    vertrag.setVersicherungsbeginn(start);
                    rerun = false;
                }
                else{
                    output.invalidinput();
                }
            } catch(Exception e) {
                output.invalidinput();
            }
        }
    }
    public void ende(Vertrag vertrag){
        boolean rerun = true;
        Scanner scanner = new Scanner(System.in);
        while(rerun) {
            output.editTime("das neue Versicherungsende");
            try {
                LocalDate start = LocalDate.parse(scanner.next());
                if(!start.isBefore(LocalDate.now())&&start.isAfter(vertrag.getVersicherungsbeginn())){
                    vertrag.setVersicherungsablauf(start);
                    rerun = false;
                }
                else{
                    output.invalidinput();
                }
            } catch (Exception e) {
                output.invalidinput();
            }
        }
    }
    public void erstelltam(Vertrag vertrag){
        boolean rerun = true;
        Scanner scanner = new Scanner(System.in);
        while(rerun) {
            output.editTime("das neue Antragsdatum");
            try {
                LocalDate start = LocalDate.parse(scanner.next());
                if(!start.isAfter(vertrag.getVersicherungsbeginn())){
                    vertrag.setAntragsDatum(start);
                    rerun = false;
                }
                else{
                    output.invalidinput();
                }
            } catch (Exception e) {
                output.invalidinput();
            }
        }
    }
}
