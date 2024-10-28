package de.axa.robin.vertragsverwaltung.user_interaction;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.editor.Create;
import de.axa.robin.vertragsverwaltung.storage.editor.Delete;
import de.axa.robin.vertragsverwaltung.storage.editor.Edit;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.Allgemein;

import java.util.List;

public class Menu {
    public static void menu() {
        while (true) {
            int number = Allgemein.menu();
            List vertrage = Vertragsverwaltung.getVertrage();
            if (number == 1) {
                Output.druckeVertrage(vertrage);
            }
            if (number == 2) {
                int vsnr = Allgemein.getvsnr();
                Vertrag vertrag = Vertragsverwaltung.getVertrag(vsnr);
                if(vsnr!=0){
                    Output.druckeVertrag(vertrag);
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