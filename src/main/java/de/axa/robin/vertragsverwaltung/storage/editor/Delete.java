package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

public class Delete {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final Input input = new Input();

    public void delete(int vsnrdelete, Vertragsverwaltung vertragsverwaltung) {
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
                output.invalidinput();
                break;
        }
    }
}
