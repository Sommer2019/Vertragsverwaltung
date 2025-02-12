package de.axa.robin.vertragsverwaltung.services;

import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.validators.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Component responsible for creating insurance contracts.
 */
@Component
public class CreateUnsetableData {
    private static final Logger logger = LoggerFactory.getLogger(CreateUnsetableData.class);

    @Autowired
    private InputValidator inputValidator;

    /**
     * Generates a unique insurance number (vsnr).
     *
     * @return the generated insurance number
     * @throws IllegalStateException if no free insurance numbers are available
     */
    public int createvsnr(List<Vertrag> vertrage) {
        logger.info("Generating unique insurance number (VSNR)");
        int vsnr = 10000000;
        while (inputValidator.vertragExistiert(vertrage, vsnr)) {
            vsnr++;
        }
        if (vsnr > 99999999) {
            logger.error("No free insurance numbers available");
            throw new IllegalStateException("Keine freien Versicherungsnummern mehr!");
        }
        logger.debug("Generated VSNR: {}", vsnr);
        return vsnr;
    }

    /**
     * Calculates the insurance price based on various factors.
     *
     * @param monatlich              whether the payment is monthly
     * @param geburtsDatum           the birthdate of the insured person
     * @param hoechstGeschwindigkeit the maximum speed of the vehicle
     * @return the calculated insurance price
     * @throws IllegalStateException if an error occurs during price calculation
     */
    public double createPreis(boolean monatlich, LocalDate geburtsDatum, int hoechstGeschwindigkeit, Preis preismodell) {
        logger.info("Calculating insurance price");
        double preis;
        int alter = LocalDate.now().getYear() - geburtsDatum.getYear();
        try {
            preis = (alter * preismodell.getAge() + hoechstGeschwindigkeit * preismodell.getSpeed()) * preismodell.getFaktor();
            if (!monatlich) {
                preis = preis * 11;
            }
        } catch (Exception e) {
            logger.error("Error calculating price", e);
            throw new IllegalStateException(e);
        }
        double roundedPreis = Math.round(preis * 100.0) / 100.0;
        logger.debug("Calculated price: {}", roundedPreis);
        return roundedPreis;
    }
}