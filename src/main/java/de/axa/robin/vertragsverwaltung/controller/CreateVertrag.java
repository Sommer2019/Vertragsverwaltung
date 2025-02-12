package de.axa.robin.vertragsverwaltung.controller;

import de.axa.robin.vertragsverwaltung.services.CreateUnsetableData;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.services.PreisService;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller class for creating insurance contracts.
 */
@Controller
public class CreateVertrag {
    private static final Logger logger = LoggerFactory.getLogger(CreateVertrag.class);

    @Autowired
    private VertragsService vertragsService;
    @Autowired
    private CreateUnsetableData createUnsetableData;
    @Autowired
    private MenuSpring menuSpring;
    @Autowired
    private PreisService preisService;

    /**
     * Handles GET requests for creating a new insurance contract.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/createVertrag")
    public String createVertrag(Model model) {
        menuSpring.setVsnr(createUnsetableData.createvsnr(vertragsService.getVertrage()));
        Vertrag vertrag = initializeVertrag();

        model.addAttribute("vertrag", vertrag);
        model.addAttribute("vsnr", menuSpring.getVsnr());
        logger.info("Initialized new contract creation with VSNR: {}", menuSpring.getVsnr());
        return "createVertrag";
    }

    /**
     * Handles POST requests for creating a new insurance contract.
     *
     * @param vertrag the insurance contract to create
     * @param result  the binding result for validation
     * @param model   the model to add attributes to
     * @return the name of the view to render
     */
    @PostMapping("/createVertrag")
    public String createVertrag(@ModelAttribute @Valid Vertrag vertrag, BindingResult result, Model model) {
        try {
            vertragsService.vertragAnlegen(vertrag, preisService.getPreismodell(), result);
        } catch (IllegalArgumentException e) {
            model.addAttribute("vsnr", menuSpring.getVsnr());
            return "createVertrag";
        }
        model.addAttribute("confirm", "Vertrag mit VSNR " + vertrag.getVsnr() + " erfolgreich erstellt! Preis: " + String.valueOf(vertrag.getPreis()).replace('.', ',') + "€");
        logger.info("Contract successfully created with VSNR: {}", vertrag.getVsnr());
        return "home";
    }

    /**
     * Handles POST requests for calculating the price of an insurance contract.
     *
     * @param vertrag the insurance contract to calculate the price for
     * @return a map containing the calculated price
     */
    @PostMapping("/createPreis")
    @ResponseBody
    public Map<String, Object> createPreis(@ModelAttribute Vertrag vertrag) {
        logger.info("Calculating price for contract: {}", vertrag);
        Map<String, Object> response = new HashMap<>();

        if (!vertrag.getPartner().getPlz().isEmpty()) {
            double preis = calculatePreis(vertrag);
            response.put("preis", String.format(Locale.GERMANY, "%.2f €", preis));
            logger.debug("Calculated price: {} €", preis);
        } else if (vertrag.getVersicherungsablauf() == null) {
            response.put("preis", "--,-- €");
            logger.warn("Insurance expiry date is null");
        } else if (vertrag.getVersicherungsablauf().isBefore(LocalDate.now())) {
            response.put("preis", "0,00 €");
            logger.warn("Insurance contract has expired");
        } else {
            response.put("preis", "--,-- €");
            logger.warn("Postal code is empty");
        }
        return response;
    }

    /**
     * Initializes a new insurance contract with default values.
     *
     * @return the initialized insurance contract
     */
    private Vertrag initializeVertrag() {
        Partner partner = new Partner();
        Fahrzeug fahrzeug = new Fahrzeug();
        Vertrag vertrag = new Vertrag(fahrzeug, partner);
        vertrag.setVersicherungsbeginn(LocalDate.now());
        vertrag.setVersicherungsablauf(LocalDate.now());
        vertrag.setAntragsDatum(LocalDate.now());
        vertrag.getPartner().setGeburtsdatum(LocalDate.now().minusYears(18));
        vertrag.getPartner().setGeschlecht(String.valueOf('M'));
        vertrag.getFahrzeug().setHoechstgeschwindigkeit(200);
        vertrag.getFahrzeug().setWagnisskennziffer(112);
        vertrag.getPartner().setLand("Deutschland");
        vertrag.setMonatlich(true);
        logger.info("Initialized new contract with default values");
        return vertrag;
    }

    /**
     * Calculates the price of the given insurance contract.
     *
     * @param vertrag the insurance contract to calculate the price for
     * @return the calculated price
     */
    private double calculatePreis(Vertrag vertrag) {
        Partner partner = vertrag.getPartner();
        Fahrzeug fahrzeug = vertrag.getFahrzeug();
        double preis = createUnsetableData.createPreis(vertrag.isMonatlich(), partner.getGeburtsdatum(), fahrzeug.getHoechstgeschwindigkeit(), preisService.getPreismodell());
        logger.debug("Calculated price for contract: {} €", preis);
        return preis;
    }
}