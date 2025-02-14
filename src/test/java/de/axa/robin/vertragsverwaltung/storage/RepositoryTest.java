package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(CustomTestConfig.class)
public class RepositoryTest {

    @InjectMocks
    private Repository repository;

    @Mock
    private Setup setup;

    @TempDir
    Path tempDir;

    private Path repoFile;
    private Path preisFile;


    @Test
    public void testSpeichereUndLadeVertrage() {
        repoFile = tempDir.resolve("vertrage.json");
        when(setup.getJson_repositoryPath()).thenReturn(repoFile.toString());
        // Erstelle ein Beispielobjekt für Partner
        Partner partner = new Partner(
                "John", "Doe", 'M', LocalDate.of(1990, 1, 1),
                "USA", "Main Street", "123", "12345", "New York", "NY"
        );
        // Erstelle ein Beispielobjekt für Fahrzeug
        Fahrzeug fahrzeug = new Fahrzeug(
                "ABC123", "Toyota", "Corolla", 180, 1
        );
        // Erstelle ein Beispielobjekt für Vertrag
        Vertrag vertrag = new Vertrag(
                1, false, 100.0,
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2021, 12, 1),
                fahrzeug,
                partner
        );
        List<Vertrag> vertrage = List.of(vertrag);

        // Speichern der Verträge in die Datei
        repository.speichereVertrage(vertrage);

        // Laden der Verträge aus der Datei
        List<Vertrag> loadedVertrage = repository.ladeVertrage();
        assertEquals(1, loadedVertrage.size(), "Es sollte genau ein Vertrag geladen werden.");

        Vertrag loaded = loadedVertrage.getFirst();
        // Überprüfe die Grundfelder
        assertEquals(vertrag.getVsnr(), loaded.getVsnr());
        assertEquals(vertrag.isMonatlich(), loaded.isMonatlich());
        assertEquals(vertrag.getPreis(), loaded.getPreis());
        assertEquals(vertrag.getVersicherungsbeginn(), loaded.getVersicherungsbeginn());
        assertEquals(vertrag.getVersicherungsablauf(), loaded.getVersicherungsablauf());
        assertEquals(vertrag.getAntragsDatum(), loaded.getAntragsDatum());

        // Überprüfe die Fahrzeug-Felder
        Fahrzeug loadedFahrzeug = loaded.getFahrzeug();
        assertEquals(fahrzeug.getAmtlichesKennzeichen(), loadedFahrzeug.getAmtlichesKennzeichen());
        assertEquals(fahrzeug.getHersteller(), loadedFahrzeug.getHersteller());
        assertEquals(fahrzeug.getTyp(), loadedFahrzeug.getTyp());
        assertEquals(fahrzeug.getHoechstgeschwindigkeit(), loadedFahrzeug.getHoechstgeschwindigkeit());
        assertEquals(fahrzeug.getWagnisskennziffer(), loadedFahrzeug.getWagnisskennziffer());

        // Überprüfe die Partner-Felder
        Partner loadedPartner = loaded.getPartner();
        assertEquals(partner.getVorname(), loadedPartner.getVorname());
        assertEquals(partner.getNachname(), loadedPartner.getNachname());
        // Das Geschlecht wird in Repository als erster Buchstabe gespeichert.
        assertEquals(partner.getGeschlecht(), loadedPartner.getGeschlecht());
        assertEquals(partner.getGeburtsdatum(), loadedPartner.getGeburtsdatum());
        assertEquals(partner.getLand(), loadedPartner.getLand());
        assertEquals(partner.getStrasse(), loadedPartner.getStrasse());
        assertEquals(partner.getHausnummer(), loadedPartner.getHausnummer());
        assertEquals(partner.getPlz(), loadedPartner.getPlz());
        assertEquals(partner.getStadt(), loadedPartner.getStadt());
        assertEquals(partner.getBundesland(), loadedPartner.getBundesland());
    }

    @Test
    public void testLadeVertrageFileNotFound() {
        repoFile = tempDir.resolve("vertrage.json");
        when(setup.getJson_repositoryPath()).thenReturn(repoFile.toString());
        // Lösche die Datei (falls sie nicht existiert, passiert nichts) und überprüfe,
        // ob eine leere Liste zurückgegeben wird.
        repoFile.toFile().delete();
        List<Vertrag> vertrage = repository.ladeVertrage();
        assertTrue(vertrage.isEmpty(), "Wenn die Datei nicht existiert, soll eine leere Liste zurückgegeben werden.");
    }

    @Test
    public void testSpeichereFaktoren() throws IOException {
        preisFile = tempDir.resolve("preis.json");
        when(setup.getJson_preisPath()).thenReturn(preisFile.toString());
        double factor = 1.5;
        double factorage = 2.5;
        double factorspeed = 3.5;

        // Schreibe die Faktoren in die Datei
        repository.speichereFaktoren(factor, factorage, factorspeed);

        // Lies die Datei und überprüfe den Inhalt
        try (FileReader fr = new FileReader(preisFile.toFile());
             JsonReader reader = Json.createReader(fr)) {
            JsonObject obj = reader.readObject();
            assertEquals(factor, obj.getJsonNumber("factor").doubleValue(), 0.0001);
            assertEquals(factorage, obj.getJsonNumber("factorage").doubleValue(), 0.0001);
            assertEquals(factorspeed, obj.getJsonNumber("factorspeed").doubleValue(), 0.0001);
        }
    }

    @Test
    public void testLadeFaktoren() throws IOException {
        preisFile = tempDir.resolve("preis.json");
        when(setup.getJson_preisPath()).thenReturn(preisFile.toString());
        // Erstelle manuell ein JSON-Objekt in der Datei
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 4.5)
                .add("factorage", 5.5)
                .add("factorspeed", 6.5)
                .build();
        try (FileWriter fw = new FileWriter(preisFile.toFile());
             JsonWriter writer = Json.createWriter(fw)) {
            writer.writeObject(jsonObject);
        }
        // Lade die Faktoren und überprüfe den Inhalt
        JsonObject loaded = repository.ladeFaktoren();
        assertNotNull(loaded, "Das geladene JSON-Objekt darf nicht null sein.");
        assertEquals(4.5, loaded.getJsonNumber("factor").doubleValue(), 0.0001);
        assertEquals(5.5, loaded.getJsonNumber("factorage").doubleValue(), 0.0001);
        assertEquals(6.5, loaded.getJsonNumber("factorspeed").doubleValue(), 0.0001);
    }
}
