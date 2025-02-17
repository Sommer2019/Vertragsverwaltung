package de.axa.robin.vertragsverwaltung.validators;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class InputValidatorTest {

    @InjectMocks
    private InputValidator inputValidator;

    @Mock
    private Setup setup;

    @Mock
    private AdressValidator adressValidator;

    @Mock
    private Repository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateHersteller() throws Exception {
        // Mock the repository to return a JsonObject containing the manufacturer
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("brands", Json.createArrayBuilder()
                        .add("'BMW'")
                        .add("'Audi'"))
                .build();
        when(repository.ladeHersteller()).thenReturn(jsonObject);

        // Test for a manufacturer that exists
        boolean result = inputValidator.validateHersteller("BMW");
        assertTrue(result, "The manufacturer 'BMW' should be found.");

        // Test for a manufacturer that does not exist
        result = inputValidator.validateHersteller("Mercedes");
        assertFalse(result, "The manufacturer 'Mercedes' should not be found.");
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
    void testValidateVertragWithoutErrors() throws Exception {
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
        when(repository.ladeHersteller()).thenReturn(Json.createObjectBuilder()
                .add("brands", Json.createArrayBuilder()
                        .add("'BMW'")
                        .add("'Audi'"))
                .build());

        BindingResult result = new BeanPropertyBindingResult(vertrag, "vertrag");
        boolean hasErrors = inputValidator.validateVertrag(vertrage, vertrag, result);

        assertFalse(hasErrors, "Die Validierung sollte keine Fehler melden.");
        assertFalse(result.hasErrors(), "Das BindingResult sollte keine Fehler enthalten.");
    }
}
