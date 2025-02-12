package de.axa.robin.vertragsverwaltung.controller;

import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.services.PreisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides methods to edit the price model of insurance contracts.
 */
@Controller
public class EditPreis {
    private static final Logger logger = LoggerFactory.getLogger(EditPreis.class);

    @Autowired
    private PreisService preisService;

    /**
     * Displays the edit price page with the current price factors.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/editPreis")
    public String editPreis(Model model) {
        logger.info("Displaying edit price page");
        Preis preismodell = preisService.getPreismodell();
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
        logger.debug("Loaded current factors from repository: factor={}, factorage={}, factorspeed={}", preismodell.getFaktor(), preismodell.getAge(), preismodell.getSpeed());
        Map<String, Object> response = new HashMap<>();
        response.put("preis", preisService.updatePreismodell(preismodell, true) + " €");
        logger.debug("New price calculated: {}", response.get("preis"));
        return response;
    }

    /**
     * Updates the price model and recalculates the prices of all contracts.
     *
     * @param preismodell the price model containing the new factors
     * @param model       the model to add attributes to
     * @return the name of the view to render
     */
    @PostMapping("/editPreis")
    public String editPreis(@ModelAttribute Preis preismodell, Model model) {
        model.addAttribute("confirm", "Preise erfolgreich angepasst. Neue Einnahmensumme: "+preisService.updatePreismodell(preismodell, false)+"€.");
        logger.info("Prices successfully updated.");
        return "home";
    }
}