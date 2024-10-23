package de.axa.robin.vertragsverwaltung.user_interaction;

import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.editor.Create;
import de.axa.robin.vertragsverwaltung.storage.editor.Delete;
import de.axa.robin.vertragsverwaltung.storage.editor.Edit;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.Allgemein;

public class Menu {
    public static void menu() {
        while (true) {
            int number = Allgemein.menu();
            if (number == 1) {
                Output.druckeVertrage(Vertragsverwaltung.getVertrage());
            }
            if (number == 2) {
                int vsnr = Allgemein.getvsnr();
                if(vsnr!=0){
                    Output.druckeVertrag(Vertragsverwaltung.getVertrag(vsnr));
                }
            }
            if (number == 3) {
                Create.createVertrag();
            }
            if (number == 4) {
                Edit.editmenu();
            }
            if (number == 5) {
                int vsnr = Allgemein.getvsnr();
                if(vsnr!=0){
                    Delete.delete(vsnr);
                    }
            }
            if (number == 6) {
                System.exit(0);
            }
        }
    }
}