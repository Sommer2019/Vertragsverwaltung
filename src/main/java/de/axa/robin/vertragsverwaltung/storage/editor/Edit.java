package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Checker.AddressValidator;
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
                    Output.preis(vertrag.getMonatlich(),Vertrag.getPreis());
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
                    VertragInput.editbeginn(vertrag);
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
                    vertrag.getPartner().setGeschlecht(PersonInput.geschlecht());
                    break;
                case 4:
                    vertrag.getPartner().setGeburtsdatum(PersonInput.geburtsdatum());
                    break;
                case 5:
                    while(true){
                        String land = PersonInput.land();
                        String strasse = PersonInput.strasse();
                        String hausnummer = PersonInput.hausnummer();
                        int plz = PersonInput.plz();
                        String stadt = PersonInput.stadt();
                        String bundesland = PersonInput.bundesland();
                        if(AddressValidator.validateAddress(strasse, String.valueOf(hausnummer), String.valueOf(plz), stadt)){
                            vertrag.getPartner().setLand(land);
                            vertrag.getPartner().setStrasse(strasse);
                            vertrag.getPartner().setHausnummer(hausnummer);
                            vertrag.getPartner().setPlz(plz);
                            vertrag.getPartner().setStadt(stadt);
                            vertrag.getPartner().setBundesland(bundesland);
                            break;
                        }
                    }
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

