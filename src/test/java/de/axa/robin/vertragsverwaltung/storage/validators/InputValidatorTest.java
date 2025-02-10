package de.axa.robin.vertragsverwaltung.storage.validators;

import de.axa.robin.vertragsverwaltung.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(CustomTestConfig.class)
@ExtendWith(MockitoExtension.class)
class InputValidatorTest {
    @InjectMocks
    private InputValidator inputValidator;
    @Mock
    private Setup setup;
    @Mock
    private AdressValidator adressValidator;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    void testIsStringInJsonFile() {
        String jsonContent = "{\"brands\": [\"Ford\", \"BMW\", \"Mercedes\"]}";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        JsonReader jsonReader = Json.createReader(inputStream);
        JsonObject jsonObject = jsonReader.readObject();

        assertTrue(jsonObject.toString().contains("Ford"));
        assertFalse(jsonObject.toString().contains("BrandX"));
    }

    @Test
    void validateVertrag_withInvalidInsuranceDates_returnsTrue() {
        Vertrag vertrag = createValidVertrag();
        vertrag.setVersicherungsbeginn(LocalDate.now().plusDays(1));
        vertrag.setAntragsDatum(LocalDate.now().plusDays(2));
        BindingResult result = new BeanPropertyBindingResult(vertrag, "vertrag");

        inputValidator.validateVertrag(vertrag, result);
        assertTrue(result.hasErrors());
    }

    @Test
    void validateVertrag_withInvalidVehicle_returnsTrue() {
        Vertrag vertrag = createValidVertrag();
        vertrag.getFahrzeug().setHersteller("InvalidBrand");
        BindingResult result = new BeanPropertyBindingResult(vertrag, "vertrag");

        inputValidator.validateVertrag(vertrag, result);
        assertTrue(result.hasErrors());
    }

    @Test
    void validateVertrag_withInvalidPartner_returnsTrue() {
        Vertrag vertrag = createValidVertrag();
        vertrag.getPartner().setGeschlecht("X");
        BindingResult result = new BeanPropertyBindingResult(vertrag, "vertrag");

        inputValidator.validateVertrag(vertrag, result);
        assertTrue(result.hasErrors());
    }

    @Test
    void validateVertrag_withInvalidAddress_returnsTrue() {
        Vertrag vertrag = createValidVertrag();
        vertrag.getPartner().setLand("InvalidLand");
        BindingResult result = new BeanPropertyBindingResult(vertrag, "vertrag");

        inputValidator.validateVertrag(vertrag, result);
        assertTrue(result.hasErrors());
    }

    private Vertrag createValidVertrag() {
        Vertrag vertrag = new Vertrag();
        vertrag.setVersicherungsbeginn(LocalDate.now().plusDays(1));
        vertrag.setAntragsDatum(LocalDate.now());
        vertrag.setVersicherungsablauf(LocalDate.now().plusYears(1));
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setHersteller("Ford");
        fahrzeug.setHoechstgeschwindigkeit(100);
        fahrzeug.setWagnisskennziffer(112);
        vertrag.setFahrzeug(fahrzeug);
        Partner partner = new Partner();
        partner.setGeschlecht("M");
        partner.setGeburtsdatum(LocalDate.now().minusYears(30));
        partner.setStrasse("Hauptstraße");
        partner.setHausnummer("11");
        partner.setPlz("51429");
        partner.setStadt("Bergisch Gladbach");
        partner.setBundesland("Nordrhein-Westfalen");
        partner.setLand("Deutschland");
        vertrag.setPartner(partner);
        return vertrag;
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}