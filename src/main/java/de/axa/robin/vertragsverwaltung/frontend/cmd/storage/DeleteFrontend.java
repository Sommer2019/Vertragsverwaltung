package de.axa.robin.vertragsverwaltung.frontend.cmd.storage;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.frontend.cmd.user_interaction.Input;
import de.axa.robin.vertragsverwaltung.frontend.cmd.user_interaction.Output;


public class DeleteFrontend {
    ////Klassen einlesen////
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung(setup);
    private final Output output = new Output();
    private final Input input;
    public static final String del = "gelöscht";

    public DeleteFrontend(Input input) {
        this.input = input;
    }

    public void delete(int vsnrdelete) {
        char inputresult = input.getChar(vertragsverwaltung.getVertrag(vsnrdelete), del);
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
