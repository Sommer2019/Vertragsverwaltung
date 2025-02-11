package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.model.PreisDTO;
import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.modell.Preis;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.editor.CreateData;
import de.axa.robin.vertragsverwaltung.storage.editor.EditPreis;
import de.axa.robin.vertragsverwaltung.storage.editor.EditVertrag;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

//ToDo: Login implementieren

/**
 * REST controller for managing {@link VertragDTO} entities.
 */
@RestController
@RequestMapping("/api/vertragsverwaltung")
public class VertragController {
    @Autowired
    private InputValidator inputValidator;
    @Autowired
    private Vertragsverwaltung vertragsverwaltung;
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
        List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
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
        Optional<Vertrag> vertrag = Optional.ofNullable(vertragsverwaltung.getVertrag(id));
        return vertrag.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    /**
     * Creates a new contract.
     *
     * @param vertragDTO the contract to create
     * @return a {@link ResponseEntity} containing the created contract
     */
    @PutMapping("/vertrage")
    public ResponseEntity<Vertrag> createVertrag(@RequestBody @Valid VertragDTO vertragDTO, BindingResult result) {
        Vertrag vertrag = mapper.toVertrag(vertragDTO);
        vertrag.setVsnr(createData.createvsnr());
        vertrag.setPreis(createData.createPreis(vertragDTO.getMonatlich(), vertragDTO.getPartner().getGeburtsdatum(), vertragDTO.getFahrzeug().getHoechstgeschwindigkeit()));
        if (inputValidator.validateVertrag(vertrag, result)||inputValidator.flexcheck(vertrag)) {
            return ResponseEntity.status(400).build();
        }
        Vertrag createdVertrag = vertragsverwaltung.vertragAnlegen(vertrag);
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
        Vertrag vertrag = mapper.toVertrag(vertragDTO);
        vertrag = editVertrag.editVertrag(vertrag, id);
        vertrag.setPreis(createData.createPreis(vertrag.isMonatlich(), vertrag.getPartner().getGeburtsdatum(), vertrag.getFahrzeug().getHoechstgeschwindigkeit()));
        boolean deleted = vertragsverwaltung.vertragLoeschen(id);
        if (!deleted) {
            return ResponseEntity.status(404).build();
        }
        Vertrag updatedVertrag = vertragsverwaltung.vertragAnlegen(vertrag);
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
        boolean deleted = vertragsverwaltung.vertragLoeschen(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
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
        JsonObject jsonObject = repository.ladeFaktoren();
        double factor = jsonObject.getJsonNumber("factor").doubleValue();
        double factoralter = jsonObject.getJsonNumber("factorage").doubleValue();
        double factorspeed = jsonObject.getJsonNumber("factorspeed").doubleValue();
        return ResponseEntity.ok("Preisberechnung: Formel: preis = (alter * " + factoralter + " + hoechstGeschwindigkeit * " + factorspeed + ") * " + factor);
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
        repository.speichereFaktoren(preis.getFaktor(), preis.getAge(), preis.getSpeed());
        BigDecimal summe = editPreis.recalcPrice(preis.getFaktor(), preis.getAge(), preis.getSpeed(), vertragsverwaltung.getVertrage());
        return ResponseEntity.ok("Preisberechnung: Formel: preis = (alter * " + preis.getFaktor() + " + hoechstGeschwindigkeit * " + preis.getAge() + ") * " + preis.getSpeed()+"; Einnahmensumme: "+ summe.setScale(2, RoundingMode.HALF_DOWN) + "€");
    }
}