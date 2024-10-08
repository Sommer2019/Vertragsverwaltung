package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.Allgemein;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

public class Delete {

    public static void delete(int vsnrdelete) {
        char input = Allgemein.delete(vsnrdelete);
        switch (Character.toLowerCase(input)) {
            case 'y':
                Vertragsverwaltung.vertragLoeschen(vsnrdelete);
                Output.done("Vertrag erfolgreich gelöscht.");
                break;
            case 'n':
                Output.cancel();
                break;
            default:
                Output.invalidinput();
                break;
        }
    }
}
