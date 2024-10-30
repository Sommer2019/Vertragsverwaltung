package de.axa.robin.vertragsverwaltung.user_interaction;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.editor.Create;
import de.axa.robin.vertragsverwaltung.storage.editor.Delete;
import de.axa.robin.vertragsverwaltung.storage.editor.Edit;

import java.util.List;

public class Menu {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final Input input = new Input();
    private final Delete delete = new Delete();
    private final Create create = new Create();
    private final Edit edit = new Edit();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung();

    public void menu() {
        while (true) {
            output.menu();
            int number = input.getNumber(Integer.class,"",-1,-1,-1, vertragsverwaltung, false);
            List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
            if (number == 1) {
                output.druckeVertrage(vertrage);
            }
            if (number == 2) {
                int vsnr = input.getNumber(Integer.class,"8-stellige VSNR oder 0 zum abbrechen",-1,-1,-1, vertragsverwaltung, true);
                if(vsnr!=0){
                    Vertrag vertrag = vertragsverwaltung.getVertrag(vsnr);
                    output.druckeVertrag(vertrag);
                }
            }
            if (number == 3) {
                create.createVertrag(vertragsverwaltung);
            }
            if (number == 4) {
                edit.editmenu(vertragsverwaltung);
            }
            if (number == 5) {
                int vsnr = input.getNumber(Integer.class,"8-stellige VSNR oder 0 zum abbrechen",-1,-1,-1, vertragsverwaltung, true);
                if(vsnr!=0){
                    delete.delete(vsnr,vertragsverwaltung);
                    }
            }
            if (number == 6) {
                System.exit(0);
            }
        }
    }
}