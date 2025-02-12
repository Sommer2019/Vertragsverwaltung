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

@Service
public class PreisService {
    private static final Logger logger = LoggerFactory.getLogger(PreisService.class);
    @Autowired
    private Repository repository;
    @Autowired
    private CreateUnsetableData createUnsetableData;
    @Autowired
    private VertragsService vertragsService;

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

    public BigDecimal updatePreismodell(Preis preis, boolean preview) {
        logger.info("Setting new pricing model: {}", preis);
        repository.speichereFaktoren(preis.getFaktor(), preis.getAge(), preis.getSpeed());
        logger.info("Recalculating prices with factors: factor={}, factorage={}, factorspeed={}", preis.getFaktor(), preis.getAge(), preis.getSpeed());
        repository.speichereFaktoren(preis.getFaktor(), preis.getAge(), preis.getSpeed());
        BigDecimal summe = BigDecimal.ZERO;
        for (Vertrag v : vertragsService.getVertrage()) {
            v.setPreis(createUnsetableData.createPreis(v.isMonatlich(), v.getPartner().getGeburtsdatum(), v.getFahrzeug().getHoechstgeschwindigkeit(), getPreismodell()));
            if (!v.getVersicherungsablauf().isBefore(LocalDate.now())) {
                if (!v.isMonatlich()) {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis()));
                } else {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis() * 12));
                }
            }
            if(!preview){
                vertragsService.vertragLoeschen(v.getVsnr(), vertragsService.getVertrage());
                vertragsService.vertragAnlegen(v, getPreismodell(),null);
            }
            logger.debug("Updated contract: {}", v);
        }
        logger.info("New total price of all contracts per year: {}€", summe.setScale(2, RoundingMode.HALF_DOWN));
        logger.info("Pricing model successfully set. New total revenue: {}€", summe.setScale(2, RoundingMode.HALF_DOWN));
        return summe.setScale(2, RoundingMode.HALF_DOWN);
    }
}
