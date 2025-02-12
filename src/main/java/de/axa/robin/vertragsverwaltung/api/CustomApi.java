package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.DefaultApi;
import de.axa.robin.vertragsverwaltung.model.PreisDTO;
import de.axa.robin.vertragsverwaltung.model.VertragApi;
import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.services.PreisService;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * REST controller for managing {@link VertragDTO} entities.
 */
@RestController
@RequestMapping("/api/vertragsverwaltung")
public class CustomApi extends DefaultApi {
    private static final Logger logger = LoggerFactory.getLogger(CustomApi.class);

    @Autowired
    private VertragsService vertragsService;
    @Autowired
    private Mapper mapper;
    @Autowired
    private PreisService preisService;

    /**
     * Retrieves all contracts.
     *
     * @return a {@link ResponseEntity} containing the list of all contracts
     */
    @Override
    public List<VertragApi> vertrageGet() throws RestClientException {
        logger.info("Retrieving all contracts");
        List<Vertrag> vertrageloaded = vertragsService.getVertrage();
        List<VertragApi> vertragereturn = null;
        for(Vertrag vertrag : vertrageloaded) {
            vertragereturn.add(mapper.toVertragApi(vertrag));
        }
        return vertrageGetWithHttpInfo().getBody();
    }

    /**
     * Retrieves a contract by its ID.
     *
     * @param id the ID of the contract
     * @return a {@link ResponseEntity} containing the contract, or 404 if not found
     */
    @Override
    public VertragApi vertrageIdGet(Integer id) throws RestClientException {
        Vertrag vertrag = vertragsService.getVertrag(id);
        mapper.toVertragApi(vertrag);
        return vertrageIdGetWithHttpInfo(id).getBody();
    }

    /**
     * Creates a new contract.
     *
     * @param vertragDTO the contract to create
     * @return a {@link ResponseEntity} containing the created contract
     */
    @Override
    public VertragApi vertragePut(VertragDTO vertragDTO) throws RestClientException {
        logger.info("Creating new contract: {}", vertragDTO);
        Vertrag vertrag = mapper.toVertrag(vertragDTO);
        vertragsService.vertragAnlegen(vertrag, preisService.getPreismodell(), null);
        logger.info("Contract successfully created: {}", vertrag);
        return mapper.toVertragApi(vertrag);
    }

    /**
     * Updates an existing contract.
     *
     * @param id the ID of the contract to update
     * @param vertragDTO the updated contract data
     * @return a {@link ResponseEntity} containing the updated contract
     */
    @Override
    public VertragApi vertrageIdPost(Integer id, VertragDTO vertragDTO) throws RestClientException {
        logger.info("Updating contract with ID: {}", id);
        Vertrag vertrag = mapper.toVertrag(vertragDTO);
        Vertrag vertragneu = vertragsService.vertragBearbeiten(vertrag, id, preisService.getPreismodell(), null);
        logger.info("Contract successfully updated: {}", vertragneu);
        return mapper.toVertragApi(vertragneu);
    }

    /**
     * Deletes a contract by its ID.
     *
     * @param id the ID of the contract to delete
     * @return a {@link ResponseEntity} indicating the result of the operation
     */
    @Override
    public VertragApi vertrageIdDelete(Integer id) throws RestClientException {
        logger.info("Deleting contract with ID: {}", id);
        vertragsService.vertragLoeschen(id, vertragsService.getVertrage());
        return vertrageIdDeleteWithHttpInfo(id).getBody();
    }

    /**
     * Retrieves the pricing model.
     *
     * @return a {@link ResponseEntity} containing the pricing model formula
     */
    @GetMapping("/preismodell")
    public ResponseEntity<String> getPreismodell() {
        Preis preis = preisService.getPreismodell();
        String formula = "Preisberechnung: Formel: preis = (alter * " + preis.getAge() + " + hoechstGeschwindigkeit * " + preis.getSpeed() + ") * " + preis.getFaktor();
        return ResponseEntity.ok(formula);
    }

    /**
     * Sets the pricing model.
     *
     * @param preisDTO the pricing model data transfer object
     * @return a {@link ResponseEntity} indicating the result of the operation
     */
    @PostMapping("/preismodell")
    public ResponseEntity<String> setPreismodell(@RequestBody PreisDTO preisDTO) {
        Preis preis = mapper.toPreis(preisDTO);
        return ResponseEntity.ok("Einnahmensumme:" + preisService.updatePreismodell(preis, false)+"€");
    }
}