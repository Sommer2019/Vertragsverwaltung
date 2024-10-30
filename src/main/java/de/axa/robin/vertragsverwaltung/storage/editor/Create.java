package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.validators.AdressValidator;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.time.LocalDate;

public class Create {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final Input input = new Input();
    private final AdressValidator addressAdressValidator = new AdressValidator();

    public void createVertrag(Vertragsverwaltung vertragsverwaltung) {
        Fahrzeug fahrzeug = createFahrzeug(vertragsverwaltung);
        Partner partner = createPartner(vertragsverwaltung);
        boolean monatlich = false;
        char booking= input.getChar(null,"Abbuchung monatlich oder jährlich? (y/m): ");
        if (booking=='m'){
            monatlich=true;
        }
        double preis = vertragsverwaltung.calcPreis(monatlich, partner, fahrzeug);
        output.preis(monatlich, preis);
        LocalDate beginn = input.getDate("den Versicherungsbeginn", LocalDate.now(), null);

        Vertrag vertrag = new Vertrag(
                createvsnr(vertragsverwaltung),
                monatlich,
                preis,
                beginn,
                beginn.plusYears(1),
                LocalDate.now(),
                fahrzeug,
                partner
        );

        char confirm = input.getChar(vertrag, "erstellt");
        if (confirm == 'y' || confirm == 'Y') {
            vertragsverwaltung.vertragAnlegen(vertrag);
            output.done("erfolgreich erstellt.");
        } else {
            output.done("wurde nicht erstellt.");
        }
    }

    public int createvsnr(Vertragsverwaltung vertragsverwaltung) {
        int vsnr = 10000000;
        while(vertragsverwaltung.getVertrag(vsnr)!=null) {
            vsnr++;
        }
        if (vsnr>99999999) {
            output.errorvalidate("Keine freien Versicherungsnummern mehr!");
            System.exit(1);
        }
        return vsnr;
    }

    private Fahrzeug createFahrzeug(Vertragsverwaltung vertragsverwaltung) {
        return new Fahrzeug(
                input.getString("das amtliche Kennzeichen", "^[\\p{Lu}]{1,3}-[\\p{Lu}]{1,2}\\d{1,4}[EH]?$", true, vertragsverwaltung, false, false, false),
                input.getString("die Marke",null, false, vertragsverwaltung, true,false, true),
                input.getString("den Typ", "^[a-zA-Z0-9\\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ]+$", false, vertragsverwaltung, false, false, false),
                input.getNumber(Integer.class,"die Höchstgeschwindigkeit", 50, 250, -1, vertragsverwaltung, false),
                input.getNumber(Integer.class,"die Wagnisskennziffer", -1, -1, 112, vertragsverwaltung, false)
        );
    }

    private Partner createPartner(Vertragsverwaltung vertragsverwaltung) {
        String vorname = input.getString("den Vornamen des Partners",null,false,vertragsverwaltung,true,false,false);
        String nachname = input.getString("den Nachnamen des Partners",null,false,vertragsverwaltung,true,false,false);
        char geschlecht = input.getChar(null,"das Geschlecht des Partners");
        LocalDate geburtsdatum = input.getDate("das Geburtsdatum", LocalDate.now().minusYears(110),LocalDate.now().minusYears(18));
        String land;
        String strasse;
        String hausnummer;
        int plz;
        String stadt;
        String bundesland;

        do {
            land = input.getString("das Land",null,false,vertragsverwaltung,true,true,false);
            strasse = input.getString("die Straße",null,false,vertragsverwaltung,false,false,false);
            hausnummer = input.getString("die Hausnummer", "[a-zA-Z0-9]+", false, vertragsverwaltung, false, false, false);
            plz = input.getNumber(Integer.class,"die PLZ",-1,-1,-1,vertragsverwaltung,false);
            stadt = input.getString("die Stadt",null,false,vertragsverwaltung,true,false,false);
            bundesland = input.getString("das Bundesland", null,false,vertragsverwaltung,true,false,false);
        } while (!addressAdressValidator.validateAddress(strasse, hausnummer, String.valueOf(plz), stadt, bundesland, land));
        return new Partner(vorname, nachname, geschlecht, geburtsdatum,
                land, strasse, hausnummer, plz, stadt, bundesland);
    }
}
