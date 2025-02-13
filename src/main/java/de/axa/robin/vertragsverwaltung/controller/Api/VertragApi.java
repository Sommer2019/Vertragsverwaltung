package de.axa.robin.vertragsverwaltung.controller.Api;

import de.axa.robin.vertragsverwaltung.DefaultApi;
import de.axa.robin.vertragsverwaltung.mapper.VertragMapper;
import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.services.PreisModelService;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing {@link VertragDTO} entities.
 */
@RestController
@RequestMapping("/api/vertragsverwaltung")
public class VertragApi extends DefaultApi {
    private static final Logger logger = LoggerFactory.getLogger(VertragApi.class);

    @Autowired
    private VertragsService vertragsService;
    @Autowired
    private VertragMapper vertragMapper;
    @Autowired
    private PreisModelService preisModelService;

    /**
     * Retrieves all contracts.
     *
     * @return a {@link ResponseEntity} containing the list of all contracts
     */
    @GetMapping("/")
    @Override
    public List<de.axa.robin.vertragsverwaltung.model.VertragApi> rootGet() throws RestClientException {
        logger.info("Retrieving all contracts");
        List<Vertrag> vertrageloaded = vertragsService.getVertrage();
        List<de.axa.robin.vertragsverwaltung.model.VertragApi> vertragereturn = new ArrayList<>();
        for(Vertrag vertrag : vertrageloaded) {
            vertragereturn.add(vertragMapper.toVertragApi(vertrag));
        }
        return vertragereturn;
    }

    /**
     * Retrieves a contract by its ID.
     *
     * @param id the ID of the contract
     * @return a {@link ResponseEntity} containing the contract, or 404 if not found
     */
    @GetMapping("/vertrage/{id}")
    @Override
    public de.axa.robin.vertragsverwaltung.model.VertragApi vertrageIdGet(@PathVariable Integer id) throws RestClientException {
        Vertrag vertrag = vertragsService.getVertrag(id);
        return vertragMapper.toVertragApi(vertrag);
    }

    /**
     * Creates a new contract.
     *
     * @param vertragDTO the contract to create
     * @return a {@link ResponseEntity} containing the created contract
     */
    @PutMapping("/")
    @Override
    public de.axa.robin.vertragsverwaltung.model.VertragApi rootPut(@RequestBody VertragDTO vertragDTO) throws RestClientException {
        logger.info("Creating new contract: {}", vertragDTO);
        Vertrag vertrag = vertragMapper.toVertrag(vertragDTO);
        vertragsService.vertragAnlegen(vertrag, preisModelService.getPreismodell(), null);
        logger.info("Contract successfully created: {}", vertrag);
        return vertragMapper.toVertragApi(vertrag);
    }

    /**
     * Updates an existing contract.
     *
     * @param id the ID of the contract to update
     * @param vertragDTO the updated contract data
     * @return a {@link ResponseEntity} containing the updated contract
     */
    @PostMapping("/vertrage/{id}")
    @Override
    public de.axa.robin.vertragsverwaltung.model.VertragApi vertrageIdPost(@PathVariable Integer id, @RequestBody VertragDTO vertragDTO) throws RestClientException {
        logger.info("Updating contract with ID: {}", id);
        Vertrag vertrag = vertragMapper.toVertrag(vertragDTO);
        Vertrag vertragneu = vertragsService.vertragBearbeiten(vertrag, id, preisModelService.getPreismodell(), null);
        logger.info("Contract successfully updated: {}", vertragneu);
        return vertragMapper.toVertragApi(vertragneu);
    }

    /**
     * Deletes a contract by its ID.
     *
     * @param id the ID of the contract to delete
     * @return a {@link ResponseEntity} indicating the result of the operation
     */
    @DeleteMapping("/vertrage/{id}")
    @Override
    public de.axa.robin.vertragsverwaltung.model.VertragApi vertrageIdDelete(@PathVariable Integer id) throws RestClientException {
        Vertrag vertrag = vertragsService.getVertrag(id);
        logger.info("Deleting contract with ID: {}", id);
        vertragsService.vertragLoeschen(id, vertragsService.getVertrage());
        return vertragMapper.toVertragApi(vertrag);
    }
}