package de.axa.robin.vertragsverwaltung.controller.Api;

import de.axa.robin.preisverwaltung.DefaultApi;
import de.axa.robin.vertragsverwaltung.mapper.PreisModelMapper;
import de.axa.robin.preisverwaltung.model.PreisDTO;
import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.services.PreisModelService;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link PreisDTO} entities.
 */
@RestController
@RequestMapping("/api/preisverwaltung")
public class PreisApi extends DefaultApi {
    @Autowired
    private PreisModelService preisModelService;
    @Autowired
    private PreisModelMapper preisModelMapper;
    @Autowired
    private VertragsService vertragsService;
    /**
     * Retrieves the pricing model.
     *
     * @return a {@link String} containing the pricing model formula
     */
    @GetMapping("/")
    public String preismodellGet() {
        Preis preis = preisModelService.getPreismodell();
        return "Preisberechnung: Formel: preis = (alter * " + preis.getAge() + " + hoechstGeschwindigkeit * " + preis.getSpeed() + ") * " + preis.getFaktor();
    }

    /**
     * Sets the pricing model.
     *
     * @param preisDTO the pricing model data transfer object
     * @return a {@link String} indicating the result of the operation
     */
    @PostMapping("/")
    public String preismodellPost(@RequestBody PreisDTO preisDTO) {
        Preis preis = preisModelMapper.toPreis(preisDTO);
        return "Einnahmensumme:" + preisModelService.updatePreisAndModell(preis, false, vertragsService.getVertrage())+"€";
    }
}
