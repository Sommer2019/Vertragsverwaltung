package de.axa.robin.vertragsverwaltung.backend.storage.editor;

import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

@Component
public class Edit {
    @Autowired
    private Vertragsverwaltung vertragsverwaltung;
    @Autowired
    private Repository repository;
    @Autowired
    private Create create;

    public BigDecimal recalcPrice(double factor, double factoralter, double factorspeed, List<Vertrag> vertrage) {
        repository.speichereFaktoren(factor,factoralter,factorspeed);
        BigDecimal summe = BigDecimal.ZERO;
        for (Vertrag v : vertrage) {
            v.setPreis(create.createPreis(v.isMonatlich(), v.getPartner().getGeburtsdatum(), v.getFahrzeug().getHoechstgeschwindigkeit()));
            if (!v.getVersicherungsablauf().isBefore(LocalDate.now())){
                if (!v.isMonatlich()) {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis()));
                } else {
                    summe = summe.add(BigDecimal.valueOf(v.getPreis() * 12));
                }
            }
            vertragsverwaltung.vertragLoeschen(v.getVsnr());
            vertragsverwaltung.vertragAnlegen(v);
        }
        System.out.println("Neue Summe aller Beiträge im Jahr: " + summe.setScale(2, RoundingMode.HALF_DOWN) + "€");
        return summe;
    }

    public Vertrag updateVertragFields(Vertrag source, Vertrag target) {
        if (source.getVsnr() != 0) {
            target.setVsnr(source.getVsnr());
        }
        updateField(source.getVersicherungsbeginn(), target::setVersicherungsbeginn);
        updateField(source.getVersicherungsablauf(), target::setVersicherungsablauf);
        updateField(source.getAntragsDatum(), target::setAntragsDatum);

        Partner targetPartner = ensurePartnerExists(target);
        updatePartnerFields(source.getPartner(), targetPartner);

        Fahrzeug targetFahrzeug = ensureFahrzeugExists(target);
        updateFahrzeugFields(source.getFahrzeug(), targetFahrzeug);

        return target;
    }

    private <T> void updateField(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }

    private Partner ensurePartnerExists(Vertrag target) {
        return target.getPartner();
    }

    private void updatePartnerFields(Partner sourcePartner, Partner targetPartner) {
        updateField(sourcePartner.getVorname(), targetPartner::setVorname);
        updateField(sourcePartner.getNachname(), targetPartner::setNachname);
        if (sourcePartner.getGeschlecht().charAt(0) != ' ') {
            sourcePartner.setGeschlecht(sourcePartner.getGeschlecht());
        }
        updateField(sourcePartner.getGeburtsdatum(), targetPartner::setGeburtsdatum);
        updateField(sourcePartner.getLand(), targetPartner::setLand);
        updateField(sourcePartner.getStrasse(), targetPartner::setStrasse);
        updateField(sourcePartner.getHausnummer(), targetPartner::setHausnummer);
        updateField(sourcePartner.getPlz(), targetPartner::setPlz);
        updateField(sourcePartner.getStadt(), targetPartner::setStadt);
        updateField(sourcePartner.getBundesland(), targetPartner::setBundesland);
    }

    private Fahrzeug ensureFahrzeugExists(Vertrag target) {
        return target.getFahrzeug();
    }

    private void updateFahrzeugFields(Fahrzeug sourceFahrzeug, Fahrzeug targetFahrzeug) {
        updateField(sourceFahrzeug.getAmtlichesKennzeichen(), targetFahrzeug::setAmtlichesKennzeichen);
        updateField(sourceFahrzeug.getHersteller(), targetFahrzeug::setHersteller);
        updateField(sourceFahrzeug.getTyp(), targetFahrzeug::setTyp);
        if (sourceFahrzeug.getHoechstgeschwindigkeit() != 0) {
            targetFahrzeug.setHoechstgeschwindigkeit(sourceFahrzeug.getHoechstgeschwindigkeit());
        }
        if (sourceFahrzeug.getWagnisskennziffer() != 0) {
            targetFahrzeug.setWagnisskennziffer(sourceFahrzeug.getWagnisskennziffer());
        }
    }

}