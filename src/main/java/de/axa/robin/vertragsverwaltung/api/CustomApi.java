package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.DefaultApi;
import de.axa.robin.vertragsverwaltung.model.PreisDTO;
import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.modell.Preis;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.storage.VertragsService;
import de.axa.robin.vertragsverwaltung.storage.editor.CreateData;
import de.axa.robin.vertragsverwaltung.storage.editor.EditPreis;
import de.axa.robin.vertragsverwaltung.storage.editor.EditVertrag;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link VertragDTO} entities.
 */
@RestController
@RequestMapping("/api/vertragsverwaltung")
public class CustomApi extends DefaultApi {
    private static final Logger logger = LoggerFactory.getLogger(CustomApi.class);

    @Autowired
    private InputValidator inputValidator;
    @Autowired
    private VertragsService vertragsService;
    @Autowired
    private EditVertrag editVertrag;
    @Autowired
    private CreateData createData;
    @Autowired
    private Mapper mapper;
    @Autowired
    private Repository repository;
    @Autowired
    private EditPreis editPreis;

    /**
     * Retrieves all contracts.
     *
     * @return a {@link ResponseEntity} containing the list of all contracts
     */
    @GetMapping("/vertrage")
    public ResponseEntity<List<Vertrag>> getAllVertrage() {
        logger.info("Retrieving all contracts");
        List<Vertrag> vertrage = vertragsService.getVertrage();
        logger.debug("Retrieved contracts: {}", vertrage);
        return ResponseEntity.ok(vertrage);
    }

    /**
     * Retrieves a contract by its ID.
     *
     * @param id the ID of the contract
     * @return a {@link ResponseEntity} containing the contract, or 404 if not found
     */
    @GetMapping("/vertrage/{id}")
    public ResponseEntity<Vertrag> getVertragById(@PathVariable Integer id) {
        logger.info("Retrieving contract with ID: {}", id);
        Optional<Vertrag> vertrag = Optional.ofNullable(vertragsService.getVertrag(id));
        return vertrag.map(v -> {
            logger.debug("Retrieved contract: {}", v);
            return ResponseEntity.ok(v);
        }).orElseGet(() -> {
            logger.warn("Contract not found with ID: {}", id);
            return ResponseEntity.status(404).build();
        });
    }

    /**
     * Creates a new contract.
     *
     * @param vertragDTO the contract to create
     * @return a {@link ResponseEntity} containing the created contract
     */
    @PutMapping("/vertrage")
    public ResponseEntity<Vertrag> createVertrag(@RequestBody @Valid VertragDTO vertragDTO, BindingResult result) {
        logger.info("Creating new contract: {}", vertragDTO);
        Vertrag vertrag = mapper.toVertrag(vertragDTO);
        vertrag.setVsnr(createData.createvsnr());
        vertrag.setPreis(createData.createPreis(vertragDTO.getMonatlich(), vertragDTO.getPartner().getGeburtsdatum(), vertragDTO.getFahrzeug().getHoechstgeschwindigkeit()));
        if (inputValidator.validateVertrag(vertrag, result) || inputValidator.flexcheck(vertrag)) {
            logger.warn("Validation errors found: {}", result.getAllErrors());
            return ResponseEntity.status(400).build();
        }
        Vertrag createdVertrag = vertragsService.vertragAnlegen(vertrag);
        logger.info("Contract successfully created: {}", createdVertrag);
        return ResponseEntity.ok(createdVertrag);
    }

    /**
     * Updates an existing contract.
     *
     * @param id the ID of the contract to update
     * @param vertragDTO the updated contract data
     * @return a {@link ResponseEntity} containing the updated contract
     */
    @PostMapping("/vertrage/{id}")
    public ResponseEntity<Vertrag> updateVertrag(@PathVariable Integer id, @RequestBody VertragDTO vertragDTO) {
        logger.info("Updating contract with ID: {}", id);
        Vertrag vertrag = mapper.toVertrag(vertragDTO);
        vertrag = editVertrag.editVertrag(vertrag, id);
        vertrag.setPreis(createData.createPreis(vertrag.isMonatlich(), vertrag.getPartner().getGeburtsdatum(), vertrag.getFahrzeug().getHoechstgeschwindigkeit()));
        boolean deleted = vertragsService.vertragLoeschen(id);
        if (!deleted) {
            logger.warn("Contract not found for deletion with ID: {}", id);
            return ResponseEntity.status(404).build();
        }
        Vertrag updatedVertrag = vertragsService.vertragAnlegen(vertrag);
        logger.info("Contract successfully updated: {}", updatedVertrag);
        return ResponseEntity.ok(updatedVertrag);
    }

    /**
     * Deletes a contract by its ID.
     *
     * @param id the ID of the contract to delete
     * @return a {@link ResponseEntity} indicating the result of the operation
     */
    @DeleteMapping("/vertrage/{id}")
    public ResponseEntity<Void> deleteVertrag(@PathVariable Integer id) {
        logger.info("Deleting contract with ID: {}", id);
        boolean deleted = vertragsService.vertragLoeschen(id);
        if (deleted) {
            logger.info("Contract successfully deleted with ID: {}", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Contract not found for deletion with ID: {}", id);
            return ResponseEntity.status(404).build();
        }
    }

    /**
     * Retrieves the pricing model.
     *
     * @return a {@link ResponseEntity} containing the pricing model formula
     */
    @GetMapping("/preismodell")
    public ResponseEntity<String> getPreismodell() {
        logger.info("Retrieving pricing model");
        JsonObject jsonObject = repository.ladeFaktoren();
        double factor = jsonObject.getJsonNumber("factor").doubleValue();
        double factoralter = jsonObject.getJsonNumber("factorage").doubleValue();
        double factorspeed = jsonObject.getJsonNumber("factorspeed").doubleValue();
        String formula = "Preisberechnung: Formel: preis = (alter * " + factoralter + " + hoechstGeschwindigkeit * " + factorspeed + ") * " + factor;
        logger.debug("Retrieved pricing model formula: {}", formula);
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
        logger.info("Setting new pricing model: {}", preisDTO);
        Preis preis = mapper.toPreis(preisDTO);
        repository.speichereFaktoren(preis.getFaktor(), preis.getAge(), preis.getSpeed());
        BigDecimal summe = editPreis.recalcPrice(preis.getFaktor(), preis.getAge(), preis.getSpeed(), vertragsService.getVertrage());
        String response = "Preisberechnung: Formel: preis = (alter * " + preis.getFaktor() + " + hoechstGeschwindigkeit * " + preis.getAge() + ") * " + preis.getSpeed() + "; Einnahmensumme: " + summe.setScale(2, RoundingMode.HALF_DOWN) + "€";
        logger.info("Pricing model successfully set. New total revenue: {}€", summe.setScale(2, RoundingMode.HALF_DOWN));
        return ResponseEntity.ok(response);
    }
}