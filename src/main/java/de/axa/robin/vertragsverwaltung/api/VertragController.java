package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vertragsverwaltung/vertrage")
public class VertragController {

    private final Vertragsverwaltung Vertragsverwaltung;

    public VertragController(Vertragsverwaltung Vertragsverwaltung) {
        this.Vertragsverwaltung = Vertragsverwaltung;
    }

    @GetMapping
    public ResponseEntity<List<Vertrag>> getAllVertrage() {
        List<Vertrag> vertrage = Vertragsverwaltung.getVertrage();
        return ResponseEntity.ok(vertrage);
    }

    @PostMapping
    public ResponseEntity<Vertrag> createVertrag(@RequestBody Vertrag vertrag) {
        Vertrag createdVertrag = Vertragsverwaltung.vertragAnlegen(vertrag);
        return ResponseEntity.ok(createdVertrag);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vertrag> getVertragById(@PathVariable Integer id) {
        Vertrag vertrag = Vertragsverwaltung.getVertrag(id);
        if (vertrag != null) {
            return ResponseEntity.ok(vertrag);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vertrag> updateVertrag(@PathVariable Integer id, @RequestBody Vertrag vertrag) {
        boolean deleted = Vertragsverwaltung.vertragLoeschen(id);
        if (deleted) {
            ResponseEntity.noContent().build();
            Vertrag createdVertrag = Vertragsverwaltung.vertragAnlegen(vertrag);
            return ResponseEntity.ok(createdVertrag);
        } else {
            return ResponseEntity.status(404).build();
        }
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