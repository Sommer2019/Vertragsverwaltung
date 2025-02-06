package de.axa.robin.vertragsverwaltung.backend.storage;

import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class Vertragsverwaltung {
    @Autowired
    private Repository repository;

    public List<Vertrag> getVertrage() {
        return repository.ladeVertrage();
    }

    public Vertrag getVertrag(int vsnr) {
        return repository.ladeVertrage().stream()
                .filter(v -> v.getVsnr() == vsnr)
                .findFirst()
                .orElse(null);
    }

    public Vertrag vertragAnlegen(Vertrag vertrag) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        vertrage.add(vertrag);
        repository.speichereVertrage(vertrage);
        return vertrag;
    }

    public boolean vertragLoeschen(int vsnr) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        boolean removed = vertrage.removeIf(v -> v.getVsnr() == vsnr);
        System.out.println(removed);
        System.out.println(vsnr);
        repository.speichereVertrage(vertrage);
        return removed;
    }

    public boolean vertragExistiert(int vsnr) {
        return repository.ladeVertrage().stream().anyMatch(v -> v.getVsnr() == vsnr);
    }

    public boolean kennzeichenExistiert(String kennzeichen) {
        return repository.ladeVertrage().stream()
                .anyMatch(v -> v.getFahrzeug().getAmtlichesKennzeichen().equals(kennzeichen));
    }
}
