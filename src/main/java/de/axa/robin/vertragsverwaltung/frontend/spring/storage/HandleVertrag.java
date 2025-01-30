package de.axa.robin.vertragsverwaltung.frontend.spring.storage;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class HandleVertrag {
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung(setup);
    private final MenuSpring menuSpring = new MenuSpring();
    private final Create create = new Create(vertragsverwaltung);
    private final InputValidator inputValidator = new InputValidator();
    private int handledVertrag = 0;

    @PostMapping("/home")
    public String processPrintVertrag(@RequestParam String vsnr, Model model) {
        if (vsnr == null || vsnr.isEmpty()) {
            return "home";
        }
        try {
            int vsnrInt = Integer.parseInt(vsnr);
            menuSpring.setVsnr(vsnrInt);
            handledVertrag = vsnrInt;
            Vertrag v = vertragsverwaltung.getVertrag(vsnrInt);
            if (v == null) {
                model.addAttribute("result", "Vertrag nicht gefunden!");
                return "home";
            }
            setupVertragModel(model, v);
            return "handleVertrag";
        } catch (NumberFormatException e) {
            model.addAttribute("result", "Ungültige VSNR!");
            return "home";
        }
    }

    @GetMapping("/showDelete")
    public String showDelete(Model model) {
        model.addAttribute("showFields", true);
        return "handleVertrag";
    }

    @PostMapping("/showDelete")
    public String deleteVertrag(Model model) {
        vertragsverwaltung.vertragLoeschen(menuSpring.getVsnr());
        model.addAttribute("confirm", "Vertrag erfolgreich gelöscht!");
        return "home";
    }

    @PostMapping("/showEdit")
    public String editVertrag(@ModelAttribute @Valid Vertrag vertrag, BindingResult result, @RequestParam("editVisible") boolean editVisible, Model model) {
        inputValidator.validateVertrag(vertrag, result);
        if (result.hasErrors()) {
            Vertrag v = vertragsverwaltung.getVertrag(handledVertrag);
            setupVertragModel(model, v);
            model.addAttribute("editVisible", editVisible);
            return "handleVertrag";
        }
        vertragsverwaltung.vertragLoeschen(menuSpring.getVsnr());
        boolean monatlich = vertrag.getMonatlich();
        int vsnr = menuSpring.getVsnr();
        model.addAttribute("confirm", "Vertrag mit VSNR " + vsnr + " erfolgreich bearbeitet! Neuer Preis: " + String.valueOf(create.createVertragAndSave(vertrag, monatlich, vsnr)).replace('.', ',') + "€");
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
        model.addAttribute("abrechnungszeitraumMonatlich", v.getMonatlich());
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
        }
    }

    private void initializeVertrag(Vertrag v, Vertrag vertrag) {
        vertrag.setMonatlich(v.getMonatlich());
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
