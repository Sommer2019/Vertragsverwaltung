package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Checker.AddressValidator;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.FahrzeugInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.AllgemeinInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.PersonInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Input.VertragInput;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import java.time.LocalDate;

public class Create {
    ////Klassen einlesen////
    private final Output output = new Output();
    private final VertragInput vertragInput = new VertragInput();
    private final PersonInput personInput = new PersonInput();
    private final FahrzeugInput fahrzeugInput = new FahrzeugInput();
    private final AllgemeinInput allgemeinInput = new AllgemeinInput();
    private final AddressValidator addressValidator = new AddressValidator();

    public void createVertrag(Vertragsverwaltung vertragsverwaltung) {
        Fahrzeug fahrzeug = createFahrzeug(vertragsverwaltung);
        Partner partner = createPartner();
        boolean monatlich = vertragInput.preisYM();
        double preis = vertragsverwaltung.calcPreis(monatlich, partner, fahrzeug);
        output.preis(monatlich, preis);
        LocalDate beginn = vertragInput.beginn();

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

        allgemeinInput.createconfirm(vertrag, vertragsverwaltung);
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
                fahrzeugInput.kennzeichen(vertragsverwaltung),
                fahrzeugInput.marke(),
                fahrzeugInput.typ(),
                fahrzeugInput.maxspeed(),
                fahrzeugInput.wkz()
        );
    }

    private Partner createPartner() {
        String vorname = personInput.name("Vor");
        String nachname = personInput.name("Nach");
        char geschlecht = personInput.geschlecht();
        LocalDate geburtsdatum = personInput.geburtsdatum();
        String land;
        String strasse;
        String hausnummer;
        int plz;
        String stadt;
        String bundesland;

        do {
            land = personInput.land();
            strasse = personInput.strasse();
            hausnummer = personInput.hausnummer();
            plz = personInput.plz();
            stadt = personInput.stadt();
            bundesland = personInput.bundesland();
        } while (!addressValidator.validateAddress(strasse, hausnummer, String.valueOf(plz), stadt, bundesland, land));
        return new Partner(vorname, nachname, geschlecht, geburtsdatum,
                land, strasse, hausnummer, plz, stadt, bundesland);
    }
}
