package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.validators.InputValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vertragsverwaltung/vertrage")
public class VertragController {
    private final InputValidator inputValidator;
    private final Vertragsverwaltung Vertragsverwaltung;

    public VertragController(InputValidator inputValidator, Vertragsverwaltung Vertragsverwaltung) {
        this.inputValidator = inputValidator;
        this.Vertragsverwaltung = Vertragsverwaltung;
    }

    @GetMapping
    public ResponseEntity<List<Vertrag>> getAllVertrage() {
        List<Vertrag> vertrage = Vertragsverwaltung.getVertrage();
        return ResponseEntity.ok(vertrage);
    }

    @PostMapping
    public ResponseEntity<Vertrag> createVertrag(@RequestBody Vertrag vertrag) {
        if (inputValidator.isInvalidVertrag(vertrag)) {
            return ResponseEntity.status(400).build();
        }
        Vertrag createdVertrag = Vertragsverwaltung.vertragAnlegen(vertrag);
        return ResponseEntity.ok(createdVertrag);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vertrag> getVertragById(@PathVariable Integer id) {
        Optional<Vertrag> vertrag = Optional.ofNullable(Vertragsverwaltung.getVertrag(id));
        return vertrag.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vertrag> updateVertrag(@PathVariable Integer id, @RequestBody Vertrag vertrag) {
        if (inputValidator.isInvalidVertrag(vertrag)) {
            return ResponseEntity.status(400).build();
        }
        boolean deleted = Vertragsverwaltung.vertragLoeschen(id);
        if (!deleted) {
            return ResponseEntity.status(404).build();
        }
        Vertrag updatedVertrag = Vertragsverwaltung.vertragAnlegen(vertrag);
        return ResponseEntity.ok(updatedVertrag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVertrag(@PathVariable Integer id) {
        boolean deleted = Vertragsverwaltung.vertragLoeschen(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}