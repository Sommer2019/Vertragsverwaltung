package de.axa.robin.vertragsverwaltung.validators;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InputValidatorTest {

    @InjectMocks
    private InputValidator inputValidator;

    @Mock
    private Setup setup;

    @Mock
    private AdressValidator adressValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Tests für validateHersteller ---

    @Test
    void testValidateHerstellerFound() throws Exception {
        // Erzeuge eine temporäre Datei mit JSON-Inhalt, der den Hersteller "BMW" (in einfachen Anführungszeichen) enthält.
        File tempFile = File.createTempFile("brands", ".json");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("{\"brands\": [\"'BMW'\", \"'Audi'\"]}");
        }
        when(setup.getJson_brandsPath()).thenReturn(tempFile.getAbsolutePath());

        boolean result = inputValidator.validateHersteller("BMW");
        assertTrue(result, "Es sollte true zurückgegeben werden, wenn 'BMW' gefunden wird.");
    }

    @Test
    void testValidateHerstellerNotFound() throws Exception {
        // Erzeuge eine temporäre Datei ohne den gesuchten Hersteller "BMW"
        File tempFile = File.createTempFile("brands", ".json");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("{\"brands\": [\"'Audi'\", \"'Mercedes'\"]}");
        }
        when(setup.getJson_brandsPath()).thenReturn(tempFile.getAbsolutePath());

        boolean result = inputValidator.validateHersteller("BMW");
        assertFalse(result, "Es sollte false zurückgegeben werden, wenn 'BMW' nicht gefunden wird.");
    }

    // --- Tests für vertragExistiert und kennzeichenExistiert ---

    @Test
    void testVertragExistiert() {
        List<Vertrag> vertrage = new ArrayList<>();
        Vertrag vertrag = new Vertrag();
        vertrag.setVsnr(12345);
        vertrage.add(vertrag);

        assertTrue(inputValidator.vertragExistiert(vertrage, 12345), "Der Vertrag mit VSNR 12345 sollte existieren.");
        assertFalse(inputValidator.vertragExistiert(vertrage, 99999), "Der Vertrag mit VSNR 99999 sollte nicht existieren.");
    }

    @Test
    void testKennzeichenExistiert() {
        List<Vertrag> vertrage = new ArrayList<>();
        Vertrag vertrag = new Vertrag();
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setAmtlichesKennzeichen("ABC123");
        vertrag.setFahrzeug(fahrzeug);
        vertrage.add(vertrag);

        assertTrue(inputValidator.kennzeichenExistiert(vertrage, "ABC123"), "Das Kennzeichen 'ABC123' sollte existieren.");
        assertFalse(inputValidator.kennzeichenExistiert(vertrage, "XYZ789"), "Das Kennzeichen 'XYZ789' sollte nicht existieren.");
    }

    // --- Tests für validateVertrag ---

    @Test
    void testValidateVertragWithErrors() {
        List<Vertrag> vertrage = new ArrayList<>();
        Vertrag vertrag = new Vertrag();

        // Setze fehlerhafte Versicherungsdaten: Versicherungsbeginn vor Antragsdatum, Ablauf vor Beginn.
        vertrag.setVersicherungsbeginn(LocalDate.of(2022, 1, 1));
        vertrag.setAntragsDatum(LocalDate.of(2022, 2, 1));  // ungültig: Antragsdatum > Beginn
        vertrag.setVersicherungsablauf(LocalDate.of(2021, 12, 31));  // ungültig: Ablauf < Beginn

        // Setze fehlerhafte Partnerdaten: zu jung und ungültiges Geschlecht.
        Partner partner = new Partner();
        partner.setGeburtsdatum(LocalDate.now().minusYears(10)); // unter 18
        partner.setGeschlecht("X");  // ungültig (erwartet: M, D oder W)
        vertrag.setPartner(partner);

        // Setze fehlerhafte Fahrzeugdaten: ungültiger Hersteller (wird als false angenommen), Geschwindigkeit zu niedrig, falsche Risikokennziffer.
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setHersteller("UnknownBrand");
        fahrzeug.setHoechstgeschwindigkeit(30); // < 50
        fahrzeug.setWagnisskennziffer(999); // ungültig, erwartet 112
        fahrzeug.setAmtlichesKennzeichen("PLATE123");
        vertrag.setFahrzeug(fahrzeug);

        // Füge den Vertrag in die Liste ein, damit auch vertragExistiert bzw. kennzeichenExistiert feuern.
        vertrage.add(vertrag);

        // Mock: Adresse ist ungültig
        when(adressValidator.validateAddress(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(false);

        BindingResult result = new BeanPropertyBindingResult(vertrag, "vertrag");
        boolean hasErrors = inputValidator.validateVertrag(vertrage, vertrag, result);

        assertTrue(hasErrors, "Die Validierung sollte Fehler melden.");
        assertTrue(result.hasErrors(), "Das BindingResult sollte Fehler enthalten.");
    }

    @Test
    void testValidateVertragWithoutErrors() {
        List<Vertrag> vertrage = new ArrayList<>();
        Vertrag vertrag = new Vertrag();

        // Setze gültige Versicherungsdaten: Beginn nach Antragsdatum und Ablauf nach Beginn.
        vertrag.setAntragsDatum(LocalDate.now());
        vertrag.setVersicherungsbeginn(LocalDate.now().plusDays(1));
        vertrag.setVersicherungsablauf(LocalDate.now().plusDays(2));
        vertrag.setMonatlich(true);
        vertrag.setVsnr(10000000);
        vertrag.setPreis(100);

        // Setze gültige Partnerdaten: Alter zwischen 18 und 110 und gültiges Geschlecht.
        Partner partner = new Partner();
        partner.setVorname("Max");
        partner.setNachname("Mustermann");
        partner.setGeburtsdatum(LocalDate.now().minusYears(30));
        partner.setGeschlecht("M");
        partner.setLand("Deutschland");
        partner.setStrasse("Musterstraße");
        partner.setHausnummer("123");
        partner.setPlz("12345");
        partner.setStadt("Musterstadt");
        partner.setBundesland("Musterland");
        vertrag.setPartner(partner);

        // Setze gültige Fahrzeugdaten: Hersteller "BMW" (als gültig angenommen), Geschwindigkeit im gültigen Bereich, korrekte Risikokennziffer, einzigartiges Kennzeichen.
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setHersteller("BMW");
        fahrzeug.setTyp("M3");
        fahrzeug.setHoechstgeschwindigkeit(150);
        fahrzeug.setWagnisskennziffer(112);
        fahrzeug.setAmtlichesKennzeichen("UNIQUE123");
        vertrag.setFahrzeug(fahrzeug);

        // Simuliere, dass die Adresse gültig ist.
        when(adressValidator.validateAddress(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        //Simuliere, dass der Hersteller gültig ist.
        when(setup.getJson_brandsPath()).thenReturn("src/main/resources/static/json/brands.json");

        BindingResult result = new BeanPropertyBindingResult(vertrag, "vertrag");
        boolean hasErrors = inputValidator.validateVertrag(vertrage, vertrag, result);

        assertFalse(hasErrors, "Die Validierung sollte keine Fehler melden.");
        assertFalse(result.hasErrors(), "Das BindingResult sollte keine Fehler enthalten.");
    }
}
