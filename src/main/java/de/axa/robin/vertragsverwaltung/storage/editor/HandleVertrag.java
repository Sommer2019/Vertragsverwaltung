package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.user_interaction.MenuSpring;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * This class provides methods to handle insurance contracts (Vertrag).
 */
@Controller
public class HandleVertrag {
    @Autowired
    private Vertragsverwaltung vertragsverwaltung;
    @Autowired
    private MenuSpring menuSpring;
    @Autowired
    private CreateData createData;
    @Autowired
    private InputValidator inputValidator;
    private int handledVertrag = 0;

    /**
     * Processes the request to print the contract details.
     *
     * @param vsnr the insurance contract number
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
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

    /**
     * Displays the delete contract page.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/showDelete")
    public String showDelete(Model model) {
        model.addAttribute("showFields", true);
        return "handleVertrag";
    }

    /**
     * Deletes the contract.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @PostMapping("/showDelete")
    public String deleteVertrag(Model model) {
        vertragsverwaltung.vertragLoeschen(menuSpring.getVsnr());
        model.addAttribute("confirm", "Vertrag erfolgreich gelöscht!");
        return "home";
    }

    /**
     * Edits the contract.
     *
     * @param vertrag the contract to edit
     * @param result the binding result for validation
     * @param editVisible whether the edit fields are visible
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
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
        boolean monatlich = vertrag.isMonatlich();
        int vsnr = menuSpring.getVsnr();
        model.addAttribute("confirm", "Vertrag mit VSNR " + vsnr + " erfolgreich bearbeitet! Neuer Preis: " + String.valueOf(createData.createVertragAndSave(vertrag, monatlich, vsnr)).replace('.', ',') + "€");
        return "home";
    }

    /**
     * Sets up the contract model.
     * @param model the model to add attributes to
     * @param v the contract to set up the model for
     */

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
        }
    }

    /**
     * Initializes the contract.
     * @param v the contract to initialize
     * @param vertrag the contract to initialize from
     */

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