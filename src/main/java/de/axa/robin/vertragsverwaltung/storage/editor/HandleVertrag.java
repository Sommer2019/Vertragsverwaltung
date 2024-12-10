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

    @PostMapping("/")
    public String processPrintVertrag(@RequestParam int VSNR, Model model) {
        menuSpring.setVsnr(VSNR);
        handledVertrag=VSNR;
        Vertrag v = vertragsverwaltung.getVertrag(VSNR);
        if (v == null) {
            model.addAttribute("result", "Vertrag nicht gefunden!");
            return "index";
        }
        Partner partner = new Partner();
        Fahrzeug fahrzeug = new Fahrzeug();
        Vertrag vertrag = new Vertrag(fahrzeug,partner);
        vertrag.getFahrzeug().setHoechstgeschwindigkeit(200);
        vertrag.getFahrzeug().setWagnisskennziffer(112);
        vertrag.getPartner().setLand("Deutschland");
        model.addAttribute("vertrag", vertrag);
        model.addAttribute("vsnr", VSNR);
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
    }

    @GetMapping("/showVertrag")
    public String showVertrag() {
        return "handleVertrag";
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
        return "index";
    }

    @PostMapping("/showEdit")
    public String editVertrag(@ModelAttribute @Valid Vertrag vertrag, BindingResult result, Model model) {
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
            return "handleVertrag";
        }
        vertragsverwaltung.vertragLoeschen(menuSpring.getVsnr());
        boolean monatlich = Objects.equals(vertrag.getMonatlich(), true);
        Partner partner = new Partner(vertrag.getPartner().getVorname(), vertrag.getPartner().getNachname(), vertrag.getPartner().getGeschlecht(), vertrag.getPartner().getGeburtsdatum(), vertrag.getPartner().getLand(), vertrag.getPartner().getStrasse(), vertrag.getPartner().getHausnummer(), vertrag.getPartner().getPlz(), vertrag.getPartner().getStadt(), vertrag.getPartner().getBundesland());
        Fahrzeug fahrzeug = new Fahrzeug(vertrag.getFahrzeug().getAmtlichesKennzeichen(), vertrag.getFahrzeug().getHersteller(), vertrag.getFahrzeug().getTyp(), vertrag.getFahrzeug().getHoechstgeschwindigkeit(), vertrag.getFahrzeug().getWagnisskennziffer());
        double preis = create.createPreis(monatlich, partner, fahrzeug);
        Vertrag vertragsave = new Vertrag(menuSpring.getVsnr(), monatlich, preis, vertrag.getVersicherungsbeginn(), vertrag.getVersicherungsablauf(), vertrag.getAntragsDatum(), fahrzeug, partner);
        vertragsverwaltung.vertragAnlegen(vertragsave);
        model.addAttribute("confirm", "Vertrag mit VSNR " + menuSpring.getVsnr() + " erfolgreich bearbeitet! Neuer Preis: " + String.valueOf(preis).replace('.', ',') + "€");
        return "index";
    }

}
