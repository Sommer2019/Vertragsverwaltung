package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Checker.AddressValidator;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.FahrzeugInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.AllgemeinInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.PersonInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.VertragInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class Edit {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final VertragInput vertragInput = new VertragInput();
    private final PersonInput personInput = new PersonInput();
    private final FahrzeugInput fahrzeugInput = new FahrzeugInput();
    private final AllgemeinInput allgemeinInput = new AllgemeinInput();
    private final AddressValidator addressValidator = new AddressValidator();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung();

    public void editVertrag(Vertrag vertrag) {
        while (true) {
            vertrag.setPreis(vertrag.getMonatlich(), vertrag.getPartner(), vertrag.getFahrzeug());
            output.druckeVertrag(vertrag);
            output.editMenu();
            int choice = allgemeinInput.getnumberinput();

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
                    vertragsverwaltung.vertragLoeschen(vertrag.getVsnr());
                    vertragsverwaltung.vertragAnlegen(vertrag);
                    output.done("erfolgreich aktualisiert.");
                    output.preis(vertrag.getMonatlich(),vertrag.getPreis());
                    return; // Zurück zum Hauptmenü
                default:
                    output.invalidinput();
                    break;
            }
        }
    }

    private void editAllgemeineDaten(Vertrag vertrag) {
        boolean rerun = true;
        while (rerun) {
            rerun = false;
            output.editDates();
            int choice = allgemeinInput.getnumberinput();

            switch (choice) {
                case 1:
                    vertragInput.editbeginn(vertrag);
                    break;
                case 2:
                    vertragInput.ende(vertrag);
                    break;
                case 3:
                    vertragInput.erstelltam(vertrag);
                    break;
                case 4:
                    vertrag.setMonatlich(vertragInput.preisYM());
                    break;
                default:
                    output.invalidinput();
                    rerun = true;
                    break;
            }
        }
    }

    private void editPersonendaten(Vertrag vertrag) {
        boolean rerun = true;
        while (rerun) {
            rerun = false;
            output.editPerson();
            int choice = allgemeinInput.getnumberinput();

            switch (choice) {
                case 1:
                    vertrag.getPartner().setVorname(personInput.name("Vor"));
                    break;
                case 2:
                    vertrag.getPartner().setNachname(personInput.name("Nach"));
                    break;
                case 3:
                    vertrag.getPartner().setGeschlecht(personInput.geschlecht());
                    break;
                case 4:
                    vertrag.getPartner().setGeburtsdatum(personInput.geburtsdatum());
                    break;
                case 5:
                    while(true){
                        String land = personInput.land();
                        String strasse = personInput.strasse();
                        String hausnummer = personInput.hausnummer();
                        int plz = personInput.plz();
                        String stadt = personInput.stadt();
                        String bundesland = personInput.bundesland();
                        if(addressValidator.validateAddress(strasse, hausnummer, String.valueOf(plz), stadt, bundesland, land)){
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
                    output.invalidinput();
                    rerun = true;
                    break;
            }
        }
    }

    private void editFahrzeugdaten(Vertrag vertrag) {
        boolean rerun = true;
        while (rerun) {
            rerun = false;
            output.editFahrzeug();
            int choice = allgemeinInput.getnumberinput();

            switch (choice) {
                case 1:
                    vertrag.getFahrzeug().setAmtlichesKennzeichen(fahrzeugInput.kennzeichen());
                    break;
                case 2:
                    vertrag.getFahrzeug().setHersteller(fahrzeugInput.marke());
                    break;
                case 3:
                    vertrag.getFahrzeug().setTyp(fahrzeugInput.typ());
                    break;
                case 4:
                    vertrag.getFahrzeug().setHoechstgeschwindigkeit(fahrzeugInput.maxspeed());
                    break;
                case 5:
                    vertrag.getFahrzeug().setWagnisskennziffer(fahrzeugInput.wkz());
                    break;
                default:
                    output.invalidinput();
                    rerun = true;
                    break;
            }
        }
    }

    public void editmenu() {
        while (true) {
            output.editwhat();
            int choice = allgemeinInput.getnumberinput();
            switch (choice) {
                case 1:
                    int vsnr = allgemeinInput.getvsnr();
                    if (vsnr != 0) {
                        editVertrag(vertragsverwaltung.getVertrag(vsnr));
                    }
                    return;
                case 2:
                    recalcprice();
                    return;
                case 3:
                    return; // Zurück zum Hauptmenü
                default:
                    output.invalidinput();
                    break;
            }
        }
    }

    public void recalcprice() {
        output.create("den neuen allgemeinen Faktor");
        double factor = allgemeinInput.getdoubleinput();
        output.create("den neuen Altersfaktor");
        double factoralter = allgemeinInput.getdoubleinput();
        output.create("den neuen Geschwindigkeitsfaktor");
        double factorspeed = allgemeinInput.getdoubleinput();

        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", factor)
                .add("factorage", factoralter)
                .add("factorspeed", factorspeed)
                .build();

        try (JsonWriter writer = Json.createWriter(new FileWriter("preiscalc.json"))) {
            writer.writeObject(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
        BigDecimal summe=BigDecimal.ZERO;
        for (Vertrag v : vertrage) {
            v.setPreis(v.getMonatlich(), v.getPartner(), v.getFahrzeug());
            if(!v.getMonatlich()){
                summe = summe.add(BigDecimal.valueOf(v.getPreis()));
            }
            else{
                summe = summe.add(BigDecimal.valueOf(v.getPreis()*12));
            }
        }
        output.newsum(summe);
    }
}

