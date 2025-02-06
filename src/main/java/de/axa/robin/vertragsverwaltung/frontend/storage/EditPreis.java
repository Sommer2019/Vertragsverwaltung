package de.axa.robin.vertragsverwaltung.frontend.storage;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Preis;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Edit;
import jakarta.json.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EditPreis {
    private final Setup setup;
    private final Vertragsverwaltung vertragsverwaltung;
    private final Repository repository;
    private final Edit edit;
    private final List<Vertrag> vertrage;

    public EditPreis(Setup setup) {
        this.setup = setup;
        vertragsverwaltung = new Vertragsverwaltung(setup);
        repository = new Repository(setup);
        edit = new Edit(vertragsverwaltung, setup);
        vertrage = vertragsverwaltung.getVertrage();
    }

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
        BigDecimal preis = edit.recalcPrice(preismodell.getFaktor(), preismodell.getAge(), preismodell.getSpeed(), vertrage);
        Map<String, Object> response = new HashMap<>();
        response.put("preis", preis.setScale(2, RoundingMode.HALF_DOWN).toString().replace('.', ',') + " €");
        edit.recalcPrice(factor, factoralter, factorspeed, vertrage);
        return response;
    }

    @PostMapping("/editPreis")
    public String editPreis(@ModelAttribute Preis preismodell, Model model) {
        BigDecimal preis = edit.recalcPrice(preismodell.getFaktor(), preismodell.getAge(), preismodell.getSpeed(), vertrage);
        model.addAttribute("confirm", "Preise erfolgreich aktualisiert! neue Preissumme: " + preis.setScale(2, RoundingMode.HALF_DOWN).toString().replace('.', ',') + "€ pro Jahr");
        return "home";
    }
}
