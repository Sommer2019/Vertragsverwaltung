package de.axa.robin.vertragsverwaltung.frontend.spring.storage;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.model.Fahrzeug;
import de.axa.robin.vertragsverwaltung.model.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Create;
import de.axa.robin.vertragsverwaltung.backend.storage.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.frontend.spring.user_interaction.MenuSpring;
import jakarta.validation.Valid;
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

@Controller
public class CreateVertrag {
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung(setup);
    private final Create create = new Create(vertragsverwaltung);
    private final MenuSpring menuSpring = new MenuSpring();
    private final InputValidator inputValidator = new InputValidator();

    @GetMapping("/createVertrag")
    public String createVertrag(Model model) {
        menuSpring.setVsnr(create.createvsnr());
        Vertrag vertrag = initializeVertrag();

        model.addAttribute("vertrag", vertrag);
        model.addAttribute("vsnr", menuSpring.getVsnr());
        return "createVertrag";
    }

    @PostMapping("/createVertrag")
    public String createVertrag(@ModelAttribute @Valid Vertrag vertrag, BindingResult result, Model model) {
        // Custom validation logic
        validateVertrag(vertrag, result);

        // Check for errors
        if (result.hasErrors()) {
            model.addAttribute("vsnr", menuSpring.getVsnr());
            return "createVertrag";
        }

        // Proceed with creating the contract if no errors
        Vertrag vertragsave = createVertragEntity(vertrag);
        vertragsverwaltung.vertragAnlegen(vertragsave);
        model.addAttribute("confirm", "Vertrag mit VSNR " + vertragsave.getVsnr() + " erfolgreich erstellt! Preis: " + String.valueOf(vertragsave.getPreis()).replace('.', ',') + "€");
        return "home";
    }

    @PostMapping("/createPreis")
    @ResponseBody
    public Map<String, Object> createPreis(@ModelAttribute Vertrag vertrag) {
        Map<String, Object> response = new HashMap<>();

        if (!vertrag.getPartner().getPlz().isEmpty()) {
            double preis = calculatePreis(vertrag);
            response.put("preis", String.format(Locale.GERMANY, "%.2f €", preis));
        } else if (vertrag.getVersicherungsablauf()==null) {
            response.put("preis", "--,-- €");
        } else if (vertrag.getVersicherungsablauf().isBefore(LocalDate.now())) {
            response.put("preis", "0,00 €");
        } else {
            response.put("preis", "--,-- €");
        }
        return response;
    }

    private Vertrag initializeVertrag() {
        Partner partner = new Partner();
        Fahrzeug fahrzeug = new Fahrzeug();
        Vertrag vertrag = new Vertrag(fahrzeug, partner);
        vertrag.setVersicherungsbeginn(LocalDate.now());
        vertrag.setVersicherungsablauf(LocalDate.now());
        vertrag.setAntragsDatum(LocalDate.now());
        vertrag.getPartner().setGeburtsdatum(LocalDate.now().minusYears(18));
        vertrag.setGender('M');
        vertrag.getFahrzeug().setHoechstgeschwindigkeit(200);
        vertrag.getFahrzeug().setWagnisskennziffer(112);
        vertrag.getPartner().setLand("Deutschland");
        vertrag.setMonatlich(true);
        return vertrag;
    }

    private void validateVertrag(Vertrag vertrag, BindingResult result) {
        inputValidator.validateVertrag(vertrag, result);
        if (vertrag.getVersicherungsbeginn().isBefore(LocalDate.now())) {
            result.rejectValue("versicherungsbeginn", "error.versicherungsbeginn", "Ungültiger Versicherungsbeginn");
        }
        if (vertragsverwaltung.kennzeichenExistiert(vertrag.getFahrzeug().getAmtlichesKennzeichen())) {
            result.rejectValue("fahrzeug.amtlichesKennzeichen", "error.fahrzeug.amtlichesKennzeichen", "Ungültiges Kennzeichen");
        }
    }

    private Vertrag createVertragEntity(Vertrag vertrag) {
        int vsnr = create.createvsnr();
        double preis = create.createPreis(vertrag.getMonatlich(), vertrag.getPartner().getGeburtsdatum(), vertrag.getFahrzeug().getHoechstgeschwindigkeit());

        return new Vertrag(vsnr, vertrag.getMonatlich(), preis, vertrag.getVersicherungsbeginn(), vertrag.getVersicherungsablauf(), vertrag.getAntragsDatum(), vertrag.getFahrzeug(), vertrag.getPartner());
    }

    private double calculatePreis(Vertrag vertrag) {
        Partner partner = vertrag.getPartner();
        Fahrzeug fahrzeug = vertrag.getFahrzeug();
        return create.createPreis(vertrag.getMonatlich(), partner.getGeburtsdatum(), fahrzeug.getHoechstgeschwindigkeit());
    }
}
