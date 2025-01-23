package de.axa.robin.vertragsverwaltung.frontend.spring.user_interaction;

import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
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
    private final Vertragsverwaltung vertragsverwaltung;

    public PrintVertrage(Vertragsverwaltung vertragsverwaltung) {
        this.vertragsverwaltung = vertragsverwaltung;
    }

    @GetMapping("/printVertrage")
    public String showAll(Model model) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.GERMANY));
        List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
        BigDecimal summe = BigDecimal.ZERO;
        for (Vertrag v : vertrage) {
            summe = summe.add(BigDecimal.valueOf(v.getPreis() * (v.isMonatlich() ? 12 : 1)));
            v.setFormattedPreis(decimalFormat.format(v.getPreis()));
        }

        // Debug statements
        System.out.println("Contracts: " + vertrage);
        System.out.println("Total price: " + summe);

        model.addAttribute("vertrage", vertrage);
        model.addAttribute("preis", summe);
        return "printVertrage";
    }
}