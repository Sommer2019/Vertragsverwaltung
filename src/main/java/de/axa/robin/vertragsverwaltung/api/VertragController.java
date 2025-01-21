package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.validators.InputValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vertragsverwaltung/vertrage")
public class VertragController {
    private final InputValidator inputValidator = new InputValidator();
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
        Vertrag createdVertrag;
        boolean invalid = inputValidator.validateVertrag(vertrag, null);
        if (vertrag.getVersicherungsbeginn().isBefore(LocalDate.now())) {
            invalid = true;
        }
        if (Vertragsverwaltung.kennzeichenExistiert(vertrag.getFahrzeug().getAmtlichesKennzeichen())) {
            invalid = true;
        }
        if(Vertragsverwaltung.vertragExistiert(vertrag.getVsnr())){
            invalid=true;
        }
        // Check for errors
        if (!invalid) {
            createdVertrag = Vertragsverwaltung.vertragAnlegen(vertrag);
            return ResponseEntity.ok(createdVertrag);
        }
        else{
            return ResponseEntity.status(400).build();
        }
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
        Vertrag createdVertrag;
        boolean invalid = inputValidator.validateVertrag(vertrag, null);
        boolean deleted;
        if (vertrag.getVersicherungsbeginn().isBefore(LocalDate.now())) {
            invalid = true;
        }
        if (Vertragsverwaltung.kennzeichenExistiert(vertrag.getFahrzeug().getAmtlichesKennzeichen())) {
            invalid = true;
        }
        if(Vertragsverwaltung.vertragExistiert(vertrag.getVsnr())){
            invalid=true;
        }
        if(!invalid){
            deleted = Vertragsverwaltung.vertragLoeschen(id);
            if (deleted) {
                ResponseEntity.noContent().build();
                createdVertrag = Vertragsverwaltung.vertragAnlegen(vertrag);
                return ResponseEntity.ok(createdVertrag);
            } else {
                return ResponseEntity.status(404).build();
            }
        }
        else{
            return ResponseEntity.status(400).build();
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