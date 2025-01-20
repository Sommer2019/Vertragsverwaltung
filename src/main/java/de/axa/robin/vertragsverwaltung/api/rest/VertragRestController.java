package de.axa.robin.vertragsverwaltung.api.rest;

import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Create;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rest/vertrage")
public class VertragRestController {
    private final Vertragsverwaltung vertragsverwaltung;
    private final Create create;

    @Autowired
    public VertragRestController(Vertragsverwaltung vertragsverwaltung) {
        this.vertragsverwaltung = vertragsverwaltung;
        create = new Create(vertragsverwaltung);
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
        vertrag.setPreis(create.createPreis(vertrag.isMonatlich(), vertrag.getPartner(), vertrag.getFahrzeug()));
        vertragsverwaltung.vertragAnlegen(vertrag);
        return ResponseEntity.ok(vertrag);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vertrag> updateVertrag(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        Vertrag vertrag = vertragsverwaltung.getVertrag(id);
        if (vertrag == null) {
            return ResponseEntity.notFound().build();
        }

        // Update partner attributes if present in the request
        if (updates.containsKey("partner")) {
            Map<String, Object> partnerUpdates = (Map<String, Object>) updates.get("partner");
            if (partnerUpdates.containsKey("vorname")) {
                vertrag.getPartner().setVorname((String) partnerUpdates.get("vorname"));
            }
            if (partnerUpdates.containsKey("nachname")) {
                vertrag.getPartner().setNachname((String) partnerUpdates.get("nachname"));
            }
            if (partnerUpdates.containsKey("geschlecht")) {
                String geschlechtStr = (String) partnerUpdates.get("geschlecht");
                if (geschlechtStr != null && !geschlechtStr.isEmpty()) {
                    vertrag.getPartner().setGeschlecht(geschlechtStr.charAt(0));
                }
            }
            if (partnerUpdates.containsKey("geburtsdatum")) {
                vertrag.getPartner().setGeburtsdatum(LocalDate.parse((String) partnerUpdates.get("geburtsdatum")));
            }
            if (partnerUpdates.containsKey("land")) {
                vertrag.getPartner().setLand((String) partnerUpdates.get("land"));
            }
            if (partnerUpdates.containsKey("strasse")) {
                vertrag.getPartner().setStrasse((String) partnerUpdates.get("strasse"));
            }
            if (partnerUpdates.containsKey("hausnummer")) {
                vertrag.getPartner().setHausnummer((String) partnerUpdates.get("hausnummer"));
            }
            if (partnerUpdates.containsKey("plz")) {
                vertrag.getPartner().setPlz((String) partnerUpdates.get("plz"));
            }
            if (partnerUpdates.containsKey("stadt")) {
                vertrag.getPartner().setStadt((String) partnerUpdates.get("stadt"));
            }
            if (partnerUpdates.containsKey("bundesland")) {
                vertrag.getPartner().setBundesland((String) partnerUpdates.get("bundesland"));
            }
        }

        // Update fahrzeug attributes if present in the request
        if (updates.containsKey("fahrzeug")) {
            Map<String, Object> fahrzeugUpdates = (Map<String, Object>) updates.get("fahrzeug");
            if (fahrzeugUpdates.containsKey("amtlichesKennzeichen")) {
                vertrag.getFahrzeug().setAmtlichesKennzeichen((String) fahrzeugUpdates.get("amtlichesKennzeichen"));
            }
            if (fahrzeugUpdates.containsKey("hersteller")) {
                vertrag.getFahrzeug().setHersteller((String) fahrzeugUpdates.get("hersteller"));
            }
            if (fahrzeugUpdates.containsKey("typ")) {
                vertrag.getFahrzeug().setTyp((String) fahrzeugUpdates.get("typ"));
            }
            if (fahrzeugUpdates.containsKey("hoechstgeschwindigkeit")) {
                vertrag.getFahrzeug().setHoechstgeschwindigkeit((Integer) fahrzeugUpdates.get("hoechstgeschwindigkeit"));
            }
            if (fahrzeugUpdates.containsKey("wagnisskennziffer")) {
                vertrag.getFahrzeug().setWagnisskennziffer((Integer) fahrzeugUpdates.get("wagnisskennziffer"));
            }
        }

        // Update other fields as needed
        vertrag.setMonatlich((Boolean) updates.getOrDefault("monatlich", vertrag.isMonatlich()));
        vertrag.setPreis(create.createPreis(vertrag.isMonatlich(), vertrag.getPartner(), vertrag.getFahrzeug()));
        vertrag.setVersicherungsbeginn(LocalDate.parse((String) updates.getOrDefault("versicherungsbeginn", vertrag.getVersicherungsbeginn().toString())));
        vertrag.setVersicherungsablauf(LocalDate.parse((String) updates.getOrDefault("versicherungsablauf", vertrag.getVersicherungsablauf().toString())));
        vertrag.setAntragsDatum(LocalDate.parse((String) updates.getOrDefault("antragsDatum", vertrag.getAntragsDatum().toString())));

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