package de.axa.robin.vertragsverwaltung.frontend.spring.storage;

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
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung(setup);
    private final Repository repository = new Repository(setup);
    private final Edit edit = new Edit(vertragsverwaltung);
    private final List<Vertrag> vertrage = vertragsverwaltung.getVertrage();

    @GetMapping("/editPreis")
    public String editPreis(Model model) {
        model.addAttribute("preismodell", new Preis());
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
        BigDecimal preis = edit.recalcpricerun(preismodell.getFaktor(), preismodell.getAge(), preismodell.getSpeed(), vertrage);
        Map<String, Object> response = new HashMap<>();
        response.put("preis", preis.setScale(2, RoundingMode.HALF_DOWN).toString().replace('.', ',') + " €");
        edit.recalcpricerun(factor, factoralter, factorspeed, vertrage);
        return response;
    }

    @PostMapping("/editPreis")
    public String editPreis(@ModelAttribute Preis preismodell, Model model) {
        BigDecimal preis = edit.recalcpricerun(preismodell.getFaktor(), preismodell.getAge(), preismodell.getSpeed(), vertrage);
        model.addAttribute("confirm", "Preise erfolgreich aktualisiert! neue Preissumme: " + preis.setScale(2, RoundingMode.HALF_DOWN).toString().replace('.', ',') + "€ pro Jahr");
        return "home";
    }
}
