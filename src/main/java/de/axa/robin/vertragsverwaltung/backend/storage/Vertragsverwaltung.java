package de.axa.robin.vertragsverwaltung.backend.storage;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;

import java.util.List;


public class Vertragsverwaltung {
    private final Repository repository;
    public Vertragsverwaltung(Setup setup) {
        ////Klassen einlesen////
        repository = new Repository(setup);
    }

    public List<Vertrag> getVertrage() {
        return repository.ladeVertrage();
    }

    public Vertrag getVertrag(int vsnr) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        return vertrage.stream().filter(v -> v.getVsnr() == vsnr).findFirst().orElse(null);
    }

    public void vertragAnlegen(Vertrag vertrag) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        vertrage.add(vertrag);
        repository.speichereVertrage(vertrage);
    }

    public void vertragLoeschen(int vsnr) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        vertrage.removeIf(v -> v.getVsnr() == vsnr);
        repository.speichereVertrage(vertrage);
    }

    public boolean vertragExistiert(int vsnr) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        return vertrage.stream().anyMatch(v -> v.getVsnr() == vsnr);
    }

    public boolean kennzeichenExistiert(String kennzeichen) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        return vertrage.stream().anyMatch(v -> v.getFahrzeug().getAmtlichesKennzeichen().equals(kennzeichen));
    }
}
