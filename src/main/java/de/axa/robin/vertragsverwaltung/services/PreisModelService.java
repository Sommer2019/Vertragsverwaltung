package de.axa.robin.vertragsverwaltung.services;

import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import jakarta.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Service class for handling pricing models and updating contract prices.
 */
@Service
public class PreisModelService {
    private static final Logger logger = LoggerFactory.getLogger(PreisModelService.class);
    @Autowired
    private Repository repository;
    @Autowired
    private VertragsService vertragsService;

    /**
     * Retrieves the current pricing model from the repository.
     *
     * @return the current pricing model
     */
    public Preis getPreismodell() {
        logger.info("Retrieving pricing model");
        JsonObject jsonObject = repository.ladeFaktoren();
        double factor = jsonObject.getJsonNumber("factor").doubleValue();
        double factoralter = jsonObject.getJsonNumber("factorage").doubleValue();
        double factorspeed = jsonObject.getJsonNumber("factorspeed").doubleValue();
        Preis preis = new Preis();
        preis.setAge(factoralter);
        preis.setSpeed(factorspeed);
        preis.setFaktor(factor);
        logger.debug("Retrieved pricing model: {}", preis);
        return preis;
    }

    /**
     * Updates the pricing model and recalculates the prices of all contracts.
     *
     * @param preis    the new pricing model
     * @param preview  if true, only preview the changes without saving
     * @param vertrage the list of contracts to update
     * @return the total price of all contracts per year
     */
    public BigDecimal updatePreisAndModell(Preis preis, boolean preview, List<Vertrag> vertrage) {
        logger.info("Setting new pricing model: {}", preis);
        repository.speichereFaktoren(preis.getFaktor(), preis.getAge(), preis.getSpeed());
        logger.info("Recalculating prices with factors: factor={}, factorage={}, factorspeed={}", preis.getFaktor(), preis.getAge(), preis.getSpeed());
        repository.speichereFaktoren(preis.getFaktor(), preis.getAge(), preis.getSpeed());
        BigDecimal summe = BigDecimal.ZERO;
        for (Vertrag v : vertragsService.getVertrage()) {
            v.setPreis(vertragsService.createPreis(v.isMonatlich(), v.getPartner().getGeburtsdatum(), v.getFahrzeug().getHoechstgeschwindigkeit(), getPreismodell()));
            if (!v.getVersicherungsablauf().isBefore(LocalDate.now())) {
                if (!v.isMonatlich()) {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis()));
                } else {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis() * 12));
                }
            }
            if (!preview) {
                vertragsService.vertragLoeschen(v.getVsnr(), vertrage);
                vertragsService.vertragAnlegen(v, getPreismodell(), null);
            }
            logger.debug("Updated contract: {}", v);
        }
        logger.info("New total price of all contracts per year: {}€", summe.setScale(2, RoundingMode.HALF_DOWN));
        logger.info("Pricing model successfully set. New total revenue: {}€", summe.setScale(2, RoundingMode.HALF_DOWN));
        return summe.setScale(2, RoundingMode.HALF_DOWN);
    }
}