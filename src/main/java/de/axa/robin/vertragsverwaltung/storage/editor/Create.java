package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Checker.AddressValidator;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.FahrzeugInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.Allgemein;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.PersonInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.VertragInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.time.LocalDate;

public class Create {
    public static void createVertrag() {
        Fahrzeug fahrzeug = createFahrzeug();
        Partner partner = createPartner();
        boolean monatlich = VertragInput.preisYM();
        double preis = Vertragsverwaltung.calcPreis(monatlich, partner, fahrzeug);
        Output.preis(monatlich, preis);
        LocalDate beginn = VertragInput.beginn();

        Vertrag vertrag = new Vertrag(
                createvsnr(),
                monatlich,
                preis,
                beginn,
                beginn.plusYears(1),
                LocalDate.now(),
                fahrzeug,
                partner
        );

        Allgemein.createconfirm(vertrag);
    }

    public static int createvsnr() {
        int vsnr = 10000000;
        while(Vertragsverwaltung.getVertrag(vsnr)!=null) {
            vsnr++;
        }
        if (vsnr>99999999) {
            Output.errorvalidate("Keine freien Versicherungsnummern mehr!");
            System.exit(1);
        }
        return vsnr;
    }

    private static Fahrzeug createFahrzeug() {
        return new Fahrzeug(
                FahrzeugInput.kennzeichen(),
                FahrzeugInput.marke(),
                FahrzeugInput.typ(),
                FahrzeugInput.maxspeed(),
                FahrzeugInput.wkz()
        );
    }

    private static Partner createPartner() {
        String vorname = PersonInput.name("Vor");
        String nachname = PersonInput.name("Nach");
        char geschlecht = PersonInput.geschlecht();
        LocalDate geburtsdatum = PersonInput.geburtsdatum();
        String land;
        String strasse;
        String hausnummer;
        int plz;
        String stadt;
        String bundesland;

        do {
            land = PersonInput.land();
            strasse = PersonInput.strasse();
            hausnummer = PersonInput.hausnummer();
            plz = PersonInput.plz();
            stadt = PersonInput.stadt();
            bundesland = PersonInput.bundesland();
        } while (!AddressValidator.validateAddress(strasse, hausnummer, String.valueOf(plz), stadt, bundesland));
        return new Partner(vorname, nachname, geschlecht, geburtsdatum,
                land, strasse, hausnummer, plz, stadt, bundesland);
    }
}
