package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.storage.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.FahrzeugInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.Allgemein;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.PersonInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.VertragInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

public class Edit {
    public static void editVertrag(Vertrag vertrag) {
        while (true) {
            Vertrag.setPreis(vertrag.getMonatlich(), vertrag.getPartner(), vertrag.getFahrzeug());
            Output.druckeVertrag(vertrag);
            Output.editMenu();
            int choice = Allgemein.getnumberinput();

            switch (choice) {
                case 1:
                    editAllgemeineDaten(vertrag);
                    break;
                case 2:
                    editPersonendaten(vertrag);
                    break;
                case 3:
                    editFahrzeugdaten(vertrag);
                    break;
                case 4:
                    vertrag.setVsnr(VertragInput.setvsnr());
                    break;
                case 5:
                    Vertragsverwaltung.vertragLoeschen(vertrag.getVsnr());
                    Vertragsverwaltung.vertragAnlegen(vertrag);
                    Output.done("erfolgreich aktualisiert.");
                    return; // Zurück zum Hauptmenü
                default:
                    Output.invalidinput();
                    break;
            }
        }
    }

    private static void editAllgemeineDaten(Vertrag vertrag) {
        boolean rerun = true;
        while (rerun) {
            rerun = false;
            Output.editDates();
            int choice = Allgemein.getnumberinput();

            switch (choice) {
                case 1:
                    VertragInput.beginn(vertrag);
                    break;
                case 2:
                    VertragInput.ende(vertrag);
                    break;
                case 3:
                    VertragInput.erstelltam(vertrag);
                    break;
                case 4:
                    vertrag.setMonatlich(VertragInput.preisym());
                    break;
                default:
                    Output.invalidinput();
                    rerun = true;
                    break;
            }
        }
    }

    private static void editPersonendaten(Vertrag vertrag) {
        boolean rerun = true;
        while (rerun) {
            rerun = false;
            Output.editPerson();
            int choice = Allgemein.getnumberinput();

            switch (choice) {
                case 1:
                    vertrag.getPartner().setVorname(PersonInput.name("Vor"));
                    break;
                case 2:
                    vertrag.getPartner().setNachname(PersonInput.name("Nach"));
                    break;
                case 3:
                    vertrag.getPartner().setLand(PersonInput.land());
                    break;
                case 4:
                    vertrag.getPartner().setBundesland(PersonInput.bundesland());
                    break;
                case 5:
                    vertrag.getPartner().setStadt(PersonInput.stadt());
                    break;
                case 6:
                    vertrag.getPartner().setStrasse(PersonInput.strasse());
                    break;
                case 7:
                    vertrag.getPartner().setHausnummer(PersonInput.hausnummer());
                    break;
                case 8:
                    vertrag.getPartner().setPlz(PersonInput.plz());
                    break;
                case 9:
                    vertrag.getPartner().setGeburtsdatum(PersonInput.geburtsdatum());
                    break;
                default:
                    Output.invalidinput();
                    rerun = true;
                    break;
            }
        }
    }

    private static void editFahrzeugdaten(Vertrag vertrag) {
        boolean rerun = true;
        while (rerun) {
            rerun = false;
            Output.editFahrzeug();
            int choice = Allgemein.getnumberinput();

            switch (choice) {
                case 1:
                    vertrag.getFahrzeug().setAmtlichesKennzeichen(FahrzeugInput.kennzeichen());
                    break;
                case 2:
                    vertrag.getFahrzeug().setHersteller(FahrzeugInput.marke());
                    break;
                case 3:
                    vertrag.getFahrzeug().setTyp(FahrzeugInput.typ());
                    break;
                case 4:
                    vertrag.getFahrzeug().setHoechstgeschwindigkeit(FahrzeugInput.maxspeed());
                    break;
                case 5:
                    vertrag.getFahrzeug().setWagnisskennziffer(FahrzeugInput.wkz());
                    break;
                default:
                    Output.invalidinput();
                    rerun = true;
                    break;
            }
        }
    }
}

