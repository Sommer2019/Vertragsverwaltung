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

@RestController
@RequestMapping("/api/vertragsverwaltung/vertrage")
public class VertragController {
    private final InputValidator inputValidator;
    private final Vertragsverwaltung vertragsverwaltung;
    private final Edit edit;
    private final Create create;

    public VertragController(InputValidator inputValidator, Vertragsverwaltung Vertragsverwaltung) {
        this.inputValidator = inputValidator;
        this.vertragsverwaltung = Vertragsverwaltung;
        this.edit = new Edit(Vertragsverwaltung);
        this.create = new Create(Vertragsverwaltung);
    }

    @GetMapping
    public ResponseEntity<List<Vertrag>> getAllVertrage() {
        List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
        return ResponseEntity.ok(vertrage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vertrag> getVertragById(@PathVariable Integer id) {
        Optional<Vertrag> vertrag = Optional.ofNullable(vertragsverwaltung.getVertrag(id));
        return vertrag.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @PutMapping
    public ResponseEntity<Vertrag> createVertrag(@RequestBody Vertrag vertrag) {
        vertrag.setPreis(create.createPreis(vertrag.getMonatlich(), vertrag.getPartner().getGeburtsdatum(), vertrag.getFahrzeug().getHoechstgeschwindigkeit()));
        if (inputValidator.isInvalidVertrag(vertrag)) {
            return ResponseEntity.status(400).build();
        }
        Vertrag createdVertrag = vertragsverwaltung.vertragAnlegen(vertrag);
        return ResponseEntity.ok(createdVertrag);
    }

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