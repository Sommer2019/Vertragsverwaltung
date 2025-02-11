package de.axa.robin.vertragsverwaltung.user_interaction;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Controller class for handling the printing of contracts.
 */
@Controller
public class PrintVertrage {
    @Autowired
    private Vertragsverwaltung vertragsverwaltung;
    private static final Logger logger = Logger.getLogger(PrintVertrage.class.getName());
    private static final String PRICE_FORMAT_PATTERN = "#,##0.00";

    /**
     * Handles the GET request to display all contracts.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/printVertrage")
    public String showAll(Model model) {
        List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
        BigDecimal summe = calculateTotalPrice(vertrage);

        model.addAttribute("vertrage", vertrage);
        model.addAttribute("preis", summe);
        return "printVertrage";
    }

    /**
     * Calculates the total price of all contracts.
     *
     * @param vertrage the list of contracts
     * @return the total price as a BigDecimal
     */
    public BigDecimal calculateTotalPrice(List<Vertrag> vertrage) {
        BigDecimal summe = BigDecimal.ZERO;
        DecimalFormat decimalFormat = new DecimalFormat(PRICE_FORMAT_PATTERN, new DecimalFormatSymbols(Locale.GERMANY));

        for (Vertrag v : vertrage) {
            BigDecimal preis = BigDecimal.valueOf(v.getPreis() * (v.isMonatlich() ? 12 : 1));
            summe = summe.add(preis);
            v.setFormattedPreis(decimalFormat.format(v.getPreis()));
        }

        // Log contracts and total price
        logger.info("Contracts: " + vertrage);
        logger.info("Total price: " + summe);

        return summe;
    }
}
