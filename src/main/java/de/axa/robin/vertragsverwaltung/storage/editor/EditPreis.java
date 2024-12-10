package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.modell.Preis;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Controller
public class EditPreis {
    private final Setup setup = new Setup();
    private final Scanner scanner = new Scanner(System.in);
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung(setup);
    private final Edit edit = new Edit(vertragsverwaltung);
    private List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
    @GetMapping("/editPreis")
    public String editPreis(Model model) {
        model.addAttribute("preismodell", new Preis());
        return "editPreis";
    }

    @PostMapping("/precalcPreis")
    @ResponseBody
    public Map<String, Object> editPreis(@ModelAttribute Preis preismodell) {
        double factor = 1.5, factoralter = 0.1, factorspeed = 0.4;
        try (JsonReader reader = Json.createReader(new FileReader(setup.getPreisPath()))) {
            JsonObject jsonObject = reader.readObject();
            factor = jsonObject.getJsonNumber("factor").doubleValue();
            factoralter = jsonObject.getJsonNumber("factorage").doubleValue();
            factorspeed = jsonObject.getJsonNumber("factorspeed").doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return "index";
    }
}
