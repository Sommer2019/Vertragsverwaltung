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
import java.util.Objects;

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
            Partner partner = new Partner();
            Fahrzeug fahrzeug = new Fahrzeug();
            Vertrag vertrag = new Vertrag(fahrzeug, partner);
            vertrag.getFahrzeug().setHoechstgeschwindigkeit(200);
            vertrag.getFahrzeug().setWagnisskennziffer(112);
            vertrag.getPartner().setLand("Deutschland");
            model.addAttribute("vertrag", vertrag);
            model.addAttribute("vsnr", vsnrInt);
            model.addAttribute("preis", String.valueOf(v.getPreis()).replace('.', ','));
            model.addAttribute("preisnew", String.valueOf(v.getPreis()).replace('.', ','));
            model.addAttribute("abrechnungszeitraumMonatlich", v.getMonatlich());
            vertrag.setMonatlich(v.getMonatlich());
            model.addAttribute("start", v.getVersicherungsbeginn());
            vertrag.setVersicherungsbeginn(v.getVersicherungsbeginn());
            model.addAttribute("end", v.getVersicherungsablauf());
            vertrag.setVersicherungsablauf(v.getVersicherungsablauf());
            model.addAttribute("create", v.getAntragsDatum());
            vertrag.setAntragsDatum(v.getAntragsDatum());
            model.addAttribute("kennzeichen", v.getFahrzeug().getAmtlichesKennzeichen());
            vertrag.getFahrzeug().setAmtlichesKennzeichen(v.getFahrzeug().getAmtlichesKennzeichen());
            model.addAttribute("hersteller", v.getFahrzeug().getHersteller());
            vertrag.getFahrzeug().setHersteller(v.getFahrzeug().getHersteller());
            model.addAttribute("typ", v.getFahrzeug().getTyp());
            vertrag.getFahrzeug().setTyp(v.getFahrzeug().getTyp());
            model.addAttribute("maxspeed", v.getFahrzeug().getHoechstgeschwindigkeit());
            vertrag.getFahrzeug().setHoechstgeschwindigkeit(v.getFahrzeug().getHoechstgeschwindigkeit());
            model.addAttribute("wkz", v.getFahrzeug().getWagnisskennziffer());
            vertrag.getFahrzeug().setWagnisskennziffer(v.getFahrzeug().getWagnisskennziffer());
            model.addAttribute("vorname", v.getPartner().getVorname());
            vertrag.getPartner().setVorname(v.getPartner().getVorname());
            model.addAttribute("nachname", v.getPartner().getNachname());
            vertrag.getPartner().setNachname(v.getPartner().getNachname());
            model.addAttribute("geschlecht", v.getPartner().getGeschlecht());
            vertrag.getPartner().setGeschlecht(v.getPartner().getGeschlecht());
            model.addAttribute("birth", v.getPartner().getGeburtsdatum());
            vertrag.getPartner().setGeburtsdatum(v.getPartner().getGeburtsdatum());
            model.addAttribute("strasse", v.getPartner().getStrasse());
            vertrag.getPartner().setStrasse(v.getPartner().getStrasse());
            model.addAttribute("hausnummer", v.getPartner().getHausnummer());
            vertrag.getPartner().setHausnummer(v.getPartner().getHausnummer());
            model.addAttribute("plz", v.getPartner().getPlz());
            vertrag.getPartner().setPlz(v.getPartner().getPlz());
            model.addAttribute("stadt", v.getPartner().getStadt());
            vertrag.getPartner().setStadt(v.getPartner().getStadt());
            model.addAttribute("bundesland", v.getPartner().getBundesland());
            vertrag.getPartner().setBundesland(v.getPartner().getBundesland());
            model.addAttribute("land", v.getPartner().getLand());
            vertrag.getPartner().setLand(v.getPartner().getLand());
            if (v.getVersicherungsablauf().isBefore(LocalDate.now())) {
                model.addAttribute("gueltig", "Vertrag abgelaufen!");
            }
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
            model.addAttribute("geschlecht", v.getPartner().getGeschlecht());
            model.addAttribute("birth", v.getPartner().getGeburtsdatum());
            model.addAttribute("strasse", v.getPartner().getStrasse());
            model.addAttribute("hausnummer", v.getPartner().getHausnummer());
            model.addAttribute("plz", v.getPartner().getPlz());
            model.addAttribute("stadt", v.getPartner().getStadt());
            model.addAttribute("bundesland", v.getPartner().getBundesland());
            model.addAttribute("land", v.getPartner().getLand());
            model.addAttribute("editVisible", editVisible);
            return "handleVertrag";
        }
        vertragsverwaltung.vertragLoeschen(menuSpring.getVsnr());
        boolean monatlich = Objects.equals(vertrag.getMonatlich(), true);
        int vsnr = menuSpring.getVsnr();
        model.addAttribute("confirm", "Vertrag mit VSNR " + menuSpring.getVsnr() + " erfolgreich bearbeitet! Neuer Preis: " + String.valueOf(create.createVertragtoSave(vertrag, monatlich, vsnr)).replace('.', ',') + "€");
        return "home";
    }

}
