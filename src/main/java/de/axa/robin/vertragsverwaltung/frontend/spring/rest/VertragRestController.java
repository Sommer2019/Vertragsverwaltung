package de.axa.robin.vertragsverwaltung.frontend.spring.rest;

import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vertrage")
public class VertragRestController {

    private final Vertragsverwaltung vertragsverwaltung;

    @Autowired
    public VertragRestController(Vertragsverwaltung vertragsverwaltung) {
        this.vertragsverwaltung = vertragsverwaltung;
    }

    @GetMapping
    public List<Vertrag> getAllVertrage() {
        return vertragsverwaltung.getVertrage();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vertrag> getVertragById(@PathVariable int id) {
        Vertrag vertrag = vertragsverwaltung.getVertrag(id);
        if (vertrag == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vertrag);
    }

    @PostMapping
    public ResponseEntity<Vertrag> createVertrag(@RequestBody Vertrag vertrag) {
        vertragsverwaltung.vertragLoeschen(vertrag.getVsnr());
        vertragsverwaltung.vertragAnlegen(vertrag);
        return ResponseEntity.ok(vertrag);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vertrag> updateVertrag(@PathVariable int id, @RequestBody Vertrag vertragDetails) {
        Vertrag vertrag = vertragsverwaltung.getVertrag(id);
        if (vertrag == null) {
            return ResponseEntity.notFound().build();
        }
        vertrag.setPreis(vertragDetails.getPreis());
        vertrag.setMonatlich(vertragDetails.getMonatlich());
        vertragsverwaltung.vertragLoeschen(vertrag.getVsnr());
        vertragsverwaltung.vertragAnlegen(vertrag);
        return ResponseEntity.ok(vertrag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVertrag(@PathVariable int id) {
        Vertrag vertrag = vertragsverwaltung.getVertrag(id);
        if (vertrag == null) {
            return ResponseEntity.notFound().build();
        }
        vertragsverwaltung.vertragLoeschen(id);
        return ResponseEntity.noContent().build();
    }
}
