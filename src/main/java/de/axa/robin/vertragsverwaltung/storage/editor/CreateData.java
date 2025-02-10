package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Component responsible for creating insurance contracts.
 */
@Component
public class CreateData {
    @Autowired
    private Vertragsverwaltung vertragsverwaltung;
    @Autowired
    private Repository repository;

    /**
     * Generates a unique insurance number (vsnr).
     *
     * @return the generated insurance number
     * @throws IllegalStateException if no free insurance numbers are available
     */
    public int createvsnr() {
        int vsnr = 10000000;
        while (vertragsverwaltung.getVertrag(vsnr) != null) {
            vsnr++;
        }
        if (vsnr > 99999999) {
            throw new IllegalStateException("Keine freien Versicherungsnummern mehr!");
        }
        return vsnr;
    }

    /**
     * Calculates the insurance price based on various factors.
     *
     * @param monatlich whether the payment is monthly
     * @param geburtsDatum the birthdate of the insured person
     * @param hoechstGeschwindigkeit the maximum speed of the vehicle
     * @return the calculated insurance price
     * @throws IllegalStateException if an error occurs during price calculation
     */
    public double createPreis(boolean monatlich, LocalDate geburtsDatum, int hoechstGeschwindigkeit) {
        double preis, factor, factoralter, factorspeed;
        int alter = LocalDate.now().getYear() - geburtsDatum.getYear();
        JsonObject jsonObject = repository.ladeFaktoren();
        factor = jsonObject.getJsonNumber("factor").doubleValue();
        factoralter = jsonObject.getJsonNumber("factorage").doubleValue();
        factorspeed = jsonObject.getJsonNumber("factorspeed").doubleValue();
        try {
            preis = (alter * factoralter + hoechstGeschwindigkeit * factorspeed) * factor;
            if (!monatlich) {
                preis = preis * 11;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return Math.round(preis * 100.0) / 100.0;
    }

    /**
     * Creates and saves an insurance contract.
     *
     * @param vertrag the insurance contract to be created
     * @param monatlich whether the payment is monthly
     * @param vsnr the insurance number
     * @return the calculated insurance price
     */
    public double createVertragAndSave(Vertrag vertrag, boolean monatlich, int vsnr) {
        Partner partner = new Partner(vertrag.getPartner().getVorname(), vertrag.getPartner().getNachname(), vertrag.getPartner().getGeschlecht().charAt(0), vertrag.getPartner().getGeburtsdatum(), vertrag.getPartner().getLand(), vertrag.getPartner().getStrasse(), vertrag.getPartner().getHausnummer(), vertrag.getPartner().getPlz(), vertrag.getPartner().getStadt(), vertrag.getPartner().getBundesland());
        Fahrzeug fahrzeug = new Fahrzeug(vertrag.getFahrzeug().getAmtlichesKennzeichen(), vertrag.getFahrzeug().getHersteller(), vertrag.getFahrzeug().getTyp(), vertrag.getFahrzeug().getHoechstgeschwindigkeit(), vertrag.getFahrzeug().getWagnisskennziffer());
        double preis = createPreis(monatlich, partner.getGeburtsdatum(), fahrzeug.getHoechstgeschwindigkeit());
        Vertrag vertragsave = new Vertrag(vsnr, monatlich, preis, vertrag.getVersicherungsbeginn(), vertrag.getVersicherungsablauf(), vertrag.getAntragsDatum(), fahrzeug, partner);
        vertragsverwaltung.vertragAnlegen(vertragsave);
        return preis;
    }
}
