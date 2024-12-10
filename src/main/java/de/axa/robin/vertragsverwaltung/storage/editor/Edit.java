package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Edit {
    /// /Klassen einlesen////
    private final Setup setup = new Setup();
    private final Vertragsverwaltung vertragsverwaltung;
    private final Create create;
    public Edit(Vertragsverwaltung vertragsverwaltung) {
        this.vertragsverwaltung = vertragsverwaltung;
        this.create = new Create(vertragsverwaltung);
    }
    public BigDecimal recalcpricerun(double factor, double factoralter, double factorspeed, List<Vertrag> vertrage) {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", factor)
                .add("factorage", factoralter)
                .add("factorspeed", factorspeed)
                .build();

        try (JsonWriter writer = Json.createWriter(new FileWriter(setup.getPreisPath()))) {
            writer.writeObject(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
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