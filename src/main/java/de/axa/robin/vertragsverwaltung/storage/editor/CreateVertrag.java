package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.VertragsService;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.user_interaction.MenuSpring;
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
    private CreateData createData;
    @Autowired
    private MenuSpring menuSpring;
    @Autowired
    private InputValidator inputValidator;

    /**
     * Handles GET requests for creating a new insurance contract.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/createVertrag")
    public String createVertrag(Model model) {
        menuSpring.setVsnr(createData.createvsnr());
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
        logger.info("Creating new contract: {}", vertrag);
        // Custom validation logic
        validateVertrag(vertrag, result);

        // Check for errors
        if (result.hasErrors()) {
            logger.warn("Validation errors found: {}", result.getAllErrors());
            model.addAttribute("vsnr", menuSpring.getVsnr());
            return "createVertrag";
        }

        // Proceed with creating the contract if no errors
        Vertrag vertragsave = createVertragEntity(vertrag);
        vertragsService.vertragAnlegen(vertragsave);
        model.addAttribute("confirm", "Vertrag mit VSNR " + vertragsave.getVsnr() + " erfolgreich erstellt! Preis: " + String.valueOf(vertragsave.getPreis()).replace('.', ',') + "€");
        logger.info("Contract successfully created with VSNR: {}", vertragsave.getVsnr());
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
     * Validates the given insurance contract.
     *
     * @param vertrag the insurance contract to validate
     * @param result  the binding result for validation
     */
    private void validateVertrag(Vertrag vertrag, BindingResult result) {
        inputValidator.validateVertrag(vertrag, result);
        if (vertrag.getVersicherungsbeginn().isBefore(LocalDate.now())) {
            result.rejectValue("versicherungsbeginn", "error.versicherungsbeginn", "Ungültiger Versicherungsbeginn");
            logger.warn("Invalid insurance start date: {}", vertrag.getVersicherungsbeginn());
        }
        if (vertragsService.kennzeichenExistiert(vertrag.getFahrzeug().getAmtlichesKennzeichen())) {
            result.rejectValue("fahrzeug.amtlichesKennzeichen", "error.fahrzeug.amtlichesKennzeichen", "Ungültiges Kennzeichen");
            logger.warn("Invalid vehicle registration number: {}", vertrag.getFahrzeug().getAmtlichesKennzeichen());
        }
    }

    /**
     * Creates a new insurance contract entity.
     *
     * @param vertrag the insurance contract to create
     * @return the created insurance contract entity
     */
    private Vertrag createVertragEntity(Vertrag vertrag) {
        int vsnr = createData.createvsnr();
        double preis = createData.createPreis(vertrag.isMonatlich(), vertrag.getPartner().getGeburtsdatum(), vertrag.getFahrzeug().getHoechstgeschwindigkeit());
        logger.info("Creating contract entity with VSNR: {} and price: {} €", vsnr, preis);

        return new Vertrag(vsnr, vertrag.isMonatlich(), preis, vertrag.getVersicherungsbeginn(), vertrag.getVersicherungsablauf(), vertrag.getAntragsDatum(), vertrag.getFahrzeug(), vertrag.getPartner());
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
        double preis = createData.createPreis(vertrag.isMonatlich(), partner.getGeburtsdatum(), fahrzeug.getHoechstgeschwindigkeit());
        logger.debug("Calculated price for contract: {} €", preis);
        return preis;
    }
}