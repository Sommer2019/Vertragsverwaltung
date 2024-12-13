package de.axa.robin.vertragsverwaltung.backend.storage.editor;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Edit {
    /// /Klassen einlesen////
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung;
    private final Repository repository = new Repository(setup);
    private final Create create;
    public Edit(Vertragsverwaltung vertragsverwaltung) {
        this.vertragsverwaltung = vertragsverwaltung;
        this.create = new Create(vertragsverwaltung);
    }
    public BigDecimal recalcpricerun(double factor, double factoralter, double factorspeed, List<Vertrag> vertrage) {
        repository.speichereFaktoren(factor, factoralter, factorspeed);
        BigDecimal summe = BigDecimal.ZERO;
        for (Vertrag v : vertrage) {
            v.setPreis(create.createPreis(v.getMonatlich(), v.getPartner(), v.getFahrzeug()));
            if (!v.getVersicherungsablauf().isBefore(LocalDate.now())){
                if (!v.getMonatlich()) {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis()));
                } else {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis() * 12));
                }
            }
            vertragsverwaltung.vertragLoeschen(v.getVsnr());
            vertragsverwaltung.vertragAnlegen(v);
        }
        System.out.println("Neue"+ summe);
        return summe;
    }
}