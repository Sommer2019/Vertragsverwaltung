package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Create;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Edit;
import de.axa.robin.vertragsverwaltung.backend.storage.validators.InputValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Vertrag} entities.
 */
@RestController
@RequestMapping("/api/vertragsverwaltung/vertrage")
public class VertragController {
    private final InputValidator inputValidator;
    private final Vertragsverwaltung vertragsverwaltung;
    private final Edit edit;
    private final Create create;

    /**
     * Constructs a new {@code VertragController}.
     *
     * @param inputValidator the input validator
     * @param vertragsverwaltung the contract management service
     */
    public VertragController(InputValidator inputValidator, Vertragsverwaltung vertragsverwaltung) {
        this.inputValidator = inputValidator;
        this.vertragsverwaltung = vertragsverwaltung;
        this.edit = new Edit(vertragsverwaltung);
        this.create = new Create(vertragsverwaltung);
    }

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
     * @param vertrag the contract to create
     * @return a {@link ResponseEntity} containing the created contract
     */
    @PutMapping
    public ResponseEntity<Vertrag> createVertrag(@RequestBody Vertrag vertrag) {
        vertrag.setPreis(create.createPreis(vertrag.getMonatlich(), vertrag.getPartner().getGeburtsdatum(), vertrag.getFahrzeug().getHoechstgeschwindigkeit()));
        if (inputValidator.isInvalidVertrag(vertrag)) {
            return ResponseEntity.status(400).build();
        }
        Vertrag createdVertrag = vertragsverwaltung.vertragAnlegen(vertrag);
        return ResponseEntity.ok(createdVertrag);
    }

    /**
     * Updates an existing contract.
     *
     * @param id the ID of the contract to update
     * @param vertrag the updated contract data
     * @return a {@link ResponseEntity} containing the updated contract
     */
    @PostMapping("/{id}")
    public ResponseEntity<Vertrag> updateVertrag(@PathVariable Integer id, @RequestBody Vertrag vertrag) {
        vertrag = edit.updateVertragFields(vertrag, vertragsverwaltung.getVertrag(id));
        vertrag.setPreis(create.createPreis(vertrag.getMonatlich(), vertrag.getPartner().getGeburtsdatum(), vertrag.getFahrzeug().getHoechstgeschwindigkeit()));
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