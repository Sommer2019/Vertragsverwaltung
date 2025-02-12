package de.axa.robin.vertragsverwaltung.controller;

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
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

import static java.lang.Integer.parseInt;

@Controller
public class HandleVertrag {
    private static final Logger logger = LoggerFactory.getLogger(HandleVertrag.class);

    @Autowired
    private VertragsService vertragsService;
    @Autowired
    private MenuSpring menuSpring;
    @Autowired
    private PreisService preisService;

    private int handledVertrag = 0;

    @PostMapping("/home")
    public String processPrintVertrag(@RequestParam String vsnr, Model model) {
        try {
            int vsnrInt = parseInt(vsnr);
            menuSpring.setVsnr(vsnrInt);
            handledVertrag = vsnrInt;
            Vertrag v;
            try {
                v = vertragsService.getVertrag(vsnrInt);
            } catch (IllegalArgumentException e) {
                logger.warn("Contract not found for VSNR: {}", vsnrInt);
                model.addAttribute("result", "Vertrag nicht gefunden!");
                return "home";
            }
            setupVertragModel(model, v);
            logger.info("Contract found and model set up for VSNR: {}", vsnrInt);
            return "handleVertrag";
        } catch (NumberFormatException e) {
            logger.error("Invalid VSNR format: {}", vsnr, e);
            model.addAttribute("result", "Ungültige VSNR!");
            return "home";
        }
    }

    @GetMapping("/showDelete")
    public String showDelete(Model model) {
        model.addAttribute("showFields", true);
        logger.info("Show delete fields");
        return "handleVertrag";
    }

    @PostMapping("/showDelete")
    public String deleteVertrag(Model model) {
        vertragsService.vertragLoeschen(menuSpring.getVsnr(), vertragsService.getVertrage());
        model.addAttribute("confirm", "Vertrag erfolgreich gelöscht!");
        logger.info("Contract successfully deleted for VSNR: {}", menuSpring.getVsnr());
        return "home";
    }

    @PostMapping("/showEdit")
    public String editVertrag(@ModelAttribute @Valid Vertrag vertrag, BindingResult result, @RequestParam("editVisible") boolean editVisible, Model model) {
        vertrag.setVsnr(menuSpring.getVsnr());
        logger.info("Editing contract: {}", vertrag);
        try{
            vertragsService.vertragBearbeiten(vertrag, vertrag.getVsnr(), preisService.getPreismodell(), result);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation errors found: {}", result.getAllErrors());
            Vertrag v = vertragsService.getVertrag(handledVertrag);
            setupVertragModel(model, v);
            model.addAttribute("editVisible", editVisible);
            return "handleVertrag";
        }
        model.addAttribute("confirm", "Vertrag mit VSNR " + vertrag.getVsnr() + " erfolgreich bearbeitet! Neuer Preis: " + String.valueOf(vertrag.getPreis()).replace('.', ',') + "€");
        logger.info("Contract successfully edited for VSNR: {}. New price: {}€", vertrag.getVsnr(), vertrag.getPreis());
        return "home";
    }

    private void setupVertragModel(Model model, Vertrag v) {
        Partner partner = new Partner();
        Fahrzeug fahrzeug = new Fahrzeug();
        Vertrag vertrag = new Vertrag(fahrzeug, partner);
        initializeVertrag(v, vertrag);

        model.addAttribute("vertrag", vertrag);
        model.addAttribute("vsnr", v.getVsnr());
        model.addAttribute("preis", String.valueOf(v.getPreis()).replace('.', ','));
        model.addAttribute("preisnew", String.valueOf(v.getPreis()).replace('.', ','));
        model.addAttribute("abrechnungszeitraumMonatlich", v.isMonatlich());
        model.addAttribute("start", v.getVersicherungsbeginn());
        model.addAttribute("end", v.getVersicherungsablauf());
        model.addAttribute("create", v.getAntragsDatum());
        model.addAttribute("kennzeichen", v.getFahrzeug().getAmtlichesKennzeichen());
        model.addAttribute("hersteller", v.getFahrzeug().getHersteller());
        model.addAttribute("typ", v.getFahrzeug().getTyp());
        model.addAttribute("maxspeed", v.getFahrzeug().getHoechstgeschwindigkeit());
        model.addAttribute("wkz", v.getFahrzeug().getWagnisskennziffer());
        model.addAttribute("vorname", v.getPartner().getVorname());
        model.addAttribute("nachname", v.getPartner().getNachname());
        model.addAttribute("geschlecht", v.getPartner().getGeschlecht().charAt(0));
        model.addAttribute("birth", v.getPartner().getGeburtsdatum());
        model.addAttribute("strasse", v.getPartner().getStrasse());
        model.addAttribute("hausnummer", v.getPartner().getHausnummer());
        model.addAttribute("plz", v.getPartner().getPlz());
        model.addAttribute("stadt", v.getPartner().getStadt());
        model.addAttribute("bundesland", v.getPartner().getBundesland());
        model.addAttribute("land", v.getPartner().getLand());

        if (v.getVersicherungsablauf().isBefore(LocalDate.now())) {
            model.addAttribute("gueltig", "Vertrag abgelaufen!");
            logger.warn("Contract expired for VSNR: {}", v.getVsnr());
        }
    }

    private void initializeVertrag(Vertrag v, Vertrag vertrag) {
        vertrag.setMonatlich(v.isMonatlich());
        vertrag.setVersicherungsbeginn(v.getVersicherungsbeginn());
        vertrag.setVersicherungsablauf(v.getVersicherungsablauf());
        vertrag.setAntragsDatum(v.getAntragsDatum());
        vertrag.getFahrzeug().setAmtlichesKennzeichen(v.getFahrzeug().getAmtlichesKennzeichen());
        vertrag.getFahrzeug().setHersteller(v.getFahrzeug().getHersteller());
        vertrag.getFahrzeug().setTyp(v.getFahrzeug().getTyp());
        vertrag.getFahrzeug().setHoechstgeschwindigkeit(v.getFahrzeug().getHoechstgeschwindigkeit());
        vertrag.getFahrzeug().setWagnisskennziffer(v.getFahrzeug().getWagnisskennziffer());
        vertrag.getPartner().setVorname(v.getPartner().getVorname());
        vertrag.getPartner().setNachname(v.getPartner().getNachname());
        vertrag.getPartner().setGeschlecht(v.getPartner().getGeschlecht());
        vertrag.getPartner().setGeburtsdatum(v.getPartner().getGeburtsdatum());
        vertrag.getPartner().setStrasse(v.getPartner().getStrasse());
        vertrag.getPartner().setHausnummer(v.getPartner().getHausnummer());
        vertrag.getPartner().setPlz(v.getPartner().getPlz());
        vertrag.getPartner().setStadt(v.getPartner().getStadt());
        vertrag.getPartner().setBundesland(v.getPartner().getBundesland());
        vertrag.getPartner().setLand(v.getPartner().getLand());
    }
}