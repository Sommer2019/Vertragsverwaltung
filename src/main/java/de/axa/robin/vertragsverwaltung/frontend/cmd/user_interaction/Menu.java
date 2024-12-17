package de.axa.robin.vertragsverwaltung.frontend.cmd.user_interaction;

import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.frontend.cmd.storage.CreateFrontend;
import de.axa.robin.vertragsverwaltung.frontend.cmd.storage.DeleteFrontend;
import de.axa.robin.vertragsverwaltung.frontend.cmd.storage.EditFrontend;

import java.util.List;

public class Menu {
    ////Klassen einlesen////
    private Output output = new Output();
    private final Setup setup = new Setup();
    private Input input;
    private DeleteFrontend deleteFrontend;
    private Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung(setup);
    private CreateFrontend createFrontend = new CreateFrontend(input, vertragsverwaltung, output);
    private EditFrontend editFrontend = new EditFrontend(input, vertragsverwaltung, output);

    public Menu(Input input) {
        this.input = input;
        deleteFrontend = new DeleteFrontend(input);
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public void setDelete(DeleteFrontend deleteFrontend) {
        this.deleteFrontend = deleteFrontend;
    }

    public void setCreate(CreateFrontend createFrontend) {
        this.createFrontend = createFrontend;
    }

    public void setEdit(EditFrontend editFrontend) {
        this.editFrontend = editFrontend;
    }

    public void setVertragsverwaltung(Vertragsverwaltung vertragsverwaltung) {
        this.vertragsverwaltung = vertragsverwaltung;
    }

    public void menu() {
        while (true) {
            output.menu();
            List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
            int number = input.getNumber(Integer.class, "", 1, 6, -1, false);

            // Handle menu actions
            handleMenuAction(number, vertrage);
        }
    }

    public void handleMenuAction(int number, List<Vertrag> vertrage) {
        int vsnr;
        switch (number) {
            case 1:
                output.druckeVertrage(vertrage);
                break;
            case 2:
                vsnr = input.getNumber(Integer.class, "8-stellige VSNR oder 0 zum abbrechen", -1, -1, -1, true);
                if (vsnr != 0) {
                    Vertrag vertrag = vertragsverwaltung.getVertrag(vsnr);
                    output.druckeVertrag(vertrag);
                }
                break;
            case 3:
                createFrontend.createVertrag();
                break;
            case 4:
                editFrontend.editmenu(vertrage);
                break;
            case 5:
                vsnr = input.getNumber(Integer.class, "8-stellige VSNR oder 0 zum abbrechen", -1, -1, -1, true);
                if (vsnr != 0) {
                    deleteFrontend.delete(vsnr);
                }
                break;
            case 6:
                System.exit(0);
                break;
            default:
                // Handle invalid input if necessary
                break;
        }
    }
}