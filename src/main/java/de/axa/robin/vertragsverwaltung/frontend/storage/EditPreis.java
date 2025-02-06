package de.axa.robin.vertragsverwaltung.frontend.storage;

import de.axa.robin.vertragsverwaltung.backend.modell.Preis;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Edit;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Controller
public class EditPreis {
    @Autowired
    private Vertragsverwaltung vertragsverwaltung;
    @Autowired
    private Repository repository;
    @Autowired
    private Edit edit;

    @GetMapping("/editPreis")
    public String editPreis(Model model) {
        Preis preismodell = new Preis();
        JsonObject jsonObject = repository.ladeFaktoren();
        preismodell.setFaktor(jsonObject.getJsonNumber("factor").doubleValue());
        preismodell.setAge(jsonObject.getJsonNumber("factorage").doubleValue());
        preismodell.setSpeed(jsonObject.getJsonNumber("factorspeed").doubleValue());
        model.addAttribute("preismodell", preismodell);
        return "editPreis";
    }

    @PostMapping("/precalcPreis")
    @ResponseBody
    public Map<String, Object> editPreis(@ModelAttribute Preis preismodell) {
        double factor, factoralter, factorspeed;
        JsonObject jsonObject = repository.ladeFaktoren();
        factor = jsonObject.getJsonNumber("factor").doubleValue();
        factoralter = jsonObject.getJsonNumber("factorage").doubleValue();
        factorspeed = jsonObject.getJsonNumber("factorspeed").doubleValue();
        BigDecimal preis = edit.recalcPrice(preismodell.getFaktor(), preismodell.getAge(), preismodell.getSpeed(), vertragsverwaltung.getVertrage());
        Map<String, Object> response = new HashMap<>();
        response.put("preis", preis.setScale(2, RoundingMode.HALF_DOWN).toString().replace('.', ',') + " €");
        edit.recalcPrice(factor, factoralter, factorspeed, vertragsverwaltung.getVertrage());
        return response;
    }

    @PostMapping("/editPreis")
    public String editPreis(@ModelAttribute Preis preismodell, Model model) {
        BigDecimal preis = edit.recalcPrice(preismodell.getFaktor(), preismodell.getAge(), preismodell.getSpeed(), vertragsverwaltung.getVertrage());
        model.addAttribute("confirm", "Preise erfolgreich aktualisiert! neue Preissumme: " + preis.setScale(2, RoundingMode.HALF_DOWN).toString().replace('.', ',') + "€ pro Jahr");
        return "home";
    }
}
