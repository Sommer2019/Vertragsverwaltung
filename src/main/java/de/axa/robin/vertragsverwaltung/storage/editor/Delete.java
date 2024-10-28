package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.AllgemeinInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

public class Delete {
    ////Klassen einlesen////
    private static final Output output = new Output();
    private static final AllgemeinInput allgemeinInput = new AllgemeinInput();
    private static final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung();

    public void delete(int vsnrdelete) {
        char input = allgemeinInput.delete(vsnrdelete);
        switch (Character.toLowerCase(input)) {
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
