package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.user_interaction.MenuSpring;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.*;

@Controller
public class CreateVertrag {
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung(setup);
    private final Create create = new Create( vertragsverwaltung);
    private final MenuSpring menuSpring = new MenuSpring();
    private final InputValidator inputValidator = new InputValidator();

    @GetMapping("/createVertrag")
    public String createVertrag(Model model) {
        menuSpring.setVsnr(create.createvsnr());
        Partner partner = new Partner();
        Fahrzeug fahrzeug = new Fahrzeug();
        Vertrag vertrag = new Vertrag(fahrzeug,partner);
        vertrag.getPartner().setGeschlecht('M');
        vertrag.getFahrzeug().setHoechstgeschwindigkeit(200);
        vertrag.getFahrzeug().setWagnisskennziffer(112);
        vertrag.getPartner().setLand("Deutschland");
        vertrag.setMonatlich(true);
        model.addAttribute("vertrag", vertrag);
        model.addAttribute("vsnr", menuSpring.getVsnr());
        return "createVertrag";
    }

    @PostMapping("/createVertrag")
    public String createVertrag(@ModelAttribute @Valid Vertrag vertrag, BindingResult result, Model model) {
        // Custom validation logic
        inputValidator.validateVertrag(vertrag, result);
        if (vertrag.getVersicherungsbeginn().isBefore(LocalDate.now())) {
            result.rejectValue("versicherungsbeginn", "error.versicherungsbeginn", "Ungültiger Versicherungsbeginn");
        }
        if (vertragsverwaltung.kennzeichenExistiert(vertrag.getFahrzeug().getAmtlichesKennzeichen())) {
            result.rejectValue("fahrzeug.amtlichesKennzeichen", "error.fahrzeug.amtlichesKennzeichen", "Ungültiges Kennzeichen");
        }
        // Check for errors
        if (result.hasErrors()) {
            model.addAttribute("vsnr", menuSpring.getVsnr());
            return "createVertrag";
        }

        // Proceed with creating the contract if no errors
        boolean monatlich = Objects.equals(vertrag.getMonatlich(), true);
        int vsnr = create.createvsnr();
        Partner partner = new Partner(vertrag.getPartner().getVorname(), vertrag.getPartner().getNachname(), vertrag.getPartner().getGeschlecht(), vertrag.getPartner().getGeburtsdatum(), vertrag.getPartner().getLand(), vertrag.getPartner().getStrasse(), vertrag.getPartner().getHausnummer(), vertrag.getPartner().getPlz(), vertrag.getPartner().getStadt(), vertrag.getPartner().getBundesland());
        Fahrzeug fahrzeug = new Fahrzeug(vertrag.getFahrzeug().getAmtlichesKennzeichen(), vertrag.getFahrzeug().getHersteller(), vertrag.getFahrzeug().getTyp(), vertrag.getFahrzeug().getHoechstgeschwindigkeit(), vertrag.getFahrzeug().getWagnisskennziffer());
        double preis = create.createPreis(monatlich, partner, fahrzeug);
        Vertrag vertragsave = new Vertrag(vsnr, monatlich, preis, vertrag.getVersicherungsbeginn(), vertrag.getVersicherungsablauf(), vertrag.getAntragsDatum(), fahrzeug, partner);

        vertragsverwaltung.vertragAnlegen(vertragsave);
        model.addAttribute("confirm", "Vertrag mit VSNR " + vsnr + " erfolgreich erstellt! Preis: " + String.valueOf(preis).replace('.', ',') + "€");
        return "index";
    }

    @PostMapping("/createPreis")
    @ResponseBody
    public Map<String, Object> createPreis(@ModelAttribute Vertrag vertrag) {
        boolean monatlich = Objects.equals(vertrag.getMonatlich(), true);
        Map<String, Object> response = new HashMap<>();
        if (!vertrag.getPartner().getPlz().isEmpty()) {
            Partner partner = new Partner(vertrag.getPartner().getVorname(), vertrag.getPartner().getNachname(), vertrag.getPartner().getGeschlecht(), vertrag.getPartner().getGeburtsdatum(), vertrag.getPartner().getLand(), vertrag.getPartner().getStrasse(), vertrag.getPartner().getHausnummer(), vertrag.getPartner().getPlz(), vertrag.getPartner().getStadt(), vertrag.getPartner().getBundesland());
            Fahrzeug fahrzeug = new Fahrzeug(vertrag.getFahrzeug().getAmtlichesKennzeichen(), vertrag.getFahrzeug().getHersteller(), vertrag.getFahrzeug().getTyp(), vertrag.getFahrzeug().getHoechstgeschwindigkeit(), vertrag.getFahrzeug().getWagnisskennziffer());
            double preis = create.createPreis(monatlich, partner, fahrzeug);
            response.put("preis", String.format(Locale.GERMANY, "%.2f €", preis));
        } else if (vertrag.getVersicherungsablauf().isBefore(LocalDate.now())) {
            response.put("preis", "0,00 €");
        } else {
            response.put("preis", "--,-- €");
        }
        return response;
    }
}
