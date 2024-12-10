package de.axa.robin.vertragsverwaltung.user_interaction;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;


@Controller
public class PrintVertrage {
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung(setup);
    private List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
    @GetMapping("/printVertrage")
    public String showAll(Model model) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.GERMANY));
        vertrage = vertragsverwaltung.getVertrage();
        BigDecimal summe = BigDecimal.ZERO;
        for (Vertrag v : vertrage) {
            summe = summe.add(BigDecimal.valueOf(v.getPreis() * (v.getMonatlich() ? 12 : 1)));
            v.setFormattedPreis(decimalFormat.format(v.getPreis()));
        }
        model.addAttribute("vertrage", vertrage);
        model.addAttribute("preis", summe);
        return "printVertrage";
    }
}
