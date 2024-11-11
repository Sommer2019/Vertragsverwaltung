package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.util.List;

public class Delete {
    ////Klassen einlesen////
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung();
    private final Output output = new Output();
    private final Input input = new Input();

    public void delete(int vsnrdelete) {
        char inputresult = input.getChar(vertragsverwaltung.getVertrag(vsnrdelete), "gelöscht");
        switch (Character.toLowerCase(inputresult)) {
            case 'y':
                vertragsverwaltung.vertragLoeschen(vsnrdelete);
                output.done("Vertrag erfolgreich gelöscht.");
                break;
            case 'n':
                output.cancel();
                break;
            default:
                output.error("Ungültige Eingabe!");
                break;
        }
    }
}
