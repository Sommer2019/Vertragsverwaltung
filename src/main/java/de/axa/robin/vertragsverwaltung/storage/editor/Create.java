package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.storage.Fahrzeug;
import de.axa.robin.vertragsverwaltung.storage.Partner;
import de.axa.robin.vertragsverwaltung.storage.Vertrag;
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
        boolean monatlich = VertragInput.preisym();

        Vertrag.setPreis(monatlich, partner, fahrzeug);
        Output.preis(monatlich, Vertrag.getPreis());

        Vertrag vertrag = new Vertrag(
                VertragInput.setvsnr(),
                monatlich,
                Vertrag.getPreis(),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                LocalDate.now(),
                fahrzeug,
                partner
        );

        Allgemein.createconfirm(vertrag);
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
        return new Partner(
                PersonInput.name("Vor"),
                PersonInput.name("Nach"),
                PersonInput.land(),
                PersonInput.bundesland(),
                PersonInput.stadt(),
                PersonInput.strasse(),
                PersonInput.hausnummer(),
                PersonInput.plz(),
                PersonInput.geburtsdatum()
        );
    }
}
