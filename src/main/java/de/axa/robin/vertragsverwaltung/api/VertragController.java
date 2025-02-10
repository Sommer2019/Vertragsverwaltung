package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.editor.CreateData;
import de.axa.robin.vertragsverwaltung.storage.editor.EditVertrag;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//ToDo: Login implementieren

/**
 * REST controller for managing {@link VertragDTO} entities.
 */
@RestController
@RequestMapping("/api/vertragsverwaltung/vertrage")
public class VertragController {
    @Autowired
    private InputValidator inputValidator;
    @Autowired
    private Vertragsverwaltung vertragsverwaltung;
    @Autowired
    private EditVertrag editVertrag;
    @Autowired
    private CreateData createData;

    /**
     * Retrieves all contracts.
     *
     * @return a {@link ResponseEntity} containing the list of all contracts
     */
    @GetMapping
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
    @GetMapping("/{id}")
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
    @PutMapping
    public ResponseEntity<Vertrag> createVertrag(@RequestBody @Valid VertragDTO vertragDTO, BindingResult result) {
        Vertrag vertrag = VertragMapper.INSTANCE.toVertrag(vertragDTO);
        vertrag.setVsnr(createData.createvsnr());
        vertrag.setPreis(createData.createPreis(vertragDTO.getMonatlich(), vertragDTO.getPartner().getGeburtsdatum(), vertragDTO.getFahrzeug().getHoechstgeschwindigkeit()));
        if (inputValidator.validateVertrag(vertrag, result)||vertrag.getVersicherungsbeginn().isBefore(LocalDate.now())||vertragsverwaltung.kennzeichenExistiert(vertrag.getFahrzeug().getAmtlichesKennzeichen())||vertragsverwaltung.vertragExistiert(vertrag.getVsnr())) {
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
    @PostMapping("/{id}")
    public ResponseEntity<Vertrag> updateVertrag(@PathVariable Integer id, @RequestBody VertragDTO vertragDTO) {
        Vertrag vertrag = VertragMapper.INSTANCE.toVertrag(vertragDTO);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVertrag(@PathVariable Integer id) {
        boolean deleted = vertragsverwaltung.vertragLoeschen(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}