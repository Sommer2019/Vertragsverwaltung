package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Preis;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.storage.VertragsService;
import jakarta.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides methods to edit the price model of insurance contracts.
 */
@Controller
public class EditPreis {
    private static final Logger logger = LoggerFactory.getLogger(EditPreis.class);

    @Autowired
    private VertragsService vertragsService;
    @Autowired
    private Repository repository;
    @Autowired
    private CreateData createData;

    /**
     * Displays the edit price page with the current price factors.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/editPreis")
    public String editPreis(Model model) {
        logger.info("Displaying edit price page");
        Preis preismodell = new Preis();
        JsonObject jsonObject = repository.ladeFaktoren();
        preismodell.setFaktor(jsonObject.getJsonNumber("factor").doubleValue());
        preismodell.setAge(jsonObject.getJsonNumber("factorage").doubleValue());
        preismodell.setSpeed(jsonObject.getJsonNumber("factorspeed").doubleValue());
        model.addAttribute("preismodell", preismodell);
        logger.debug("Current price factors loaded: {}", preismodell);
        return "editPreis";
    }

    /**
     * Calculates the new price based on the provided price model and returns it as a JSON response.
     *
     * @param preismodell the price model containing the new factors
     * @return a map containing the new price
     */
    @PostMapping("/precalcPreis")
    @ResponseBody
    public Map<String, Object> editPreis(@ModelAttribute Preis preismodell) {
        logger.info("Calculating new price with provided factors: {}", preismodell);
        double factor = 0, factoralter = 0, factorspeed = 0;
        JsonObject jsonObject;
        try {
            jsonObject = repository.ladeFaktoren();
            factor = jsonObject.getJsonNumber("factor").doubleValue();
            factoralter = jsonObject.getJsonNumber("factorage").doubleValue();
            factorspeed = jsonObject.getJsonNumber("factorspeed").doubleValue();
            logger.debug("Loaded current factors from repository: factor={}, factorage={}, factorspeed={}", factor, factoralter, factorspeed);
        } catch (Exception e) {
            logger.error("Error loading factors from repository", e);
        }
        BigDecimal preis = recalcPrice(preismodell.getFaktor(), preismodell.getAge(), preismodell.getSpeed(), vertragsService.getVertrage());
        Map<String, Object> response = new HashMap<>();
        response.put("preis", preis.setScale(2, RoundingMode.HALF_DOWN).toString().replace('.', ',') + " €");
        logger.debug("New price calculated: {}", response.get("preis"));
        recalcPrice(factor, factoralter, factorspeed, vertragsService.getVertrage());
        return response;
    }

    /**
     * Updates the price model and recalculates the prices of all contracts.
     *
     * @param preismodell the price model containing the new factors
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @PostMapping("/editPreis")
    public String editPreis(@ModelAttribute Preis preismodell, Model model) {
        logger.info("Updating price model with new factors: {}", preismodell);
        BigDecimal preis = recalcPrice(preismodell.getFaktor(), preismodell.getAge(), preismodell.getSpeed(), vertragsService.getVertrage());
        model.addAttribute("confirm", "Preise erfolgreich aktualisiert! neue Preissumme: " + preis.setScale(2, RoundingMode.HALF_DOWN).toString().replace('.', ',') + "€ pro Jahr");
        logger.info("Prices successfully updated. New total price: {}€ per year", preis.setScale(2, RoundingMode.HALF_DOWN));
        return "home";
    }

    /**
     * Recalculates the prices of all contracts based on the provided factors.
     *
     * @param factor the general factor
     * @param factoralter the age factor
     * @param factorspeed the speed factor
     * @param vertrage the list of contracts to update
     * @return the total price of all contracts
     */
    public BigDecimal recalcPrice(double factor, double factoralter, double factorspeed, List<Vertrag> vertrage) {
        logger.info("Recalculating prices with factors: factor={}, factorage={}, factorspeed={}", factor, factoralter, factorspeed);
        repository.speichereFaktoren(factor, factoralter, factorspeed);
        BigDecimal summe = BigDecimal.ZERO;
        for (Vertrag v : vertrage) {
            v.setPreis(createData.createPreis(v.isMonatlich(), v.getPartner().getGeburtsdatum(), v.getFahrzeug().getHoechstgeschwindigkeit()));
            if (!v.getVersicherungsablauf().isBefore(LocalDate.now())) {
                if (!v.isMonatlich()) {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis()));
                } else {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis() * 12));
                }
            }
            vertragsService.vertragLoeschen(v.getVsnr());
            vertragsService.vertragAnlegen(v);
            logger.debug("Updated contract: {}", v);
        }
        logger.info("New total price of all contracts per year: {}€", summe.setScale(2, RoundingMode.HALF_DOWN));
        return summe;
    }
}