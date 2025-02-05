package de.axa.robin.vertragsverwaltung.backend.storage.editor;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileReader;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class CreateTest {
    private Create create;
    private Setup setup;
    private Vertragsverwaltung mockVertragsverwaltung;

    @BeforeEach
    public void setUp() {
        setup = new Setup();
        mockVertragsverwaltung = mock(Vertragsverwaltung.class);
        create = new Create(mockVertragsverwaltung, setup);
    }

    @Test
    void createvsnr() {
        // Arrange
        when(mockVertragsverwaltung.getVertrag(anyInt())).thenReturn(null);

        // Act
        int vsnr = create.createvsnr();

        // Assert
        assertTrue(vsnr >= 10000000 && vsnr <= 99999999);
    }

    @ParameterizedTest
    @CsvSource({
            "true, 1980-01-01, 150, 112, 12345, Germany, Main St, 1, Berlin, Berlin",
            "false, 1990-05-15, 200, 120, 54321, Germany, Second St, 2, Munich, Bavaria"
    })
    void createPreis(boolean monatlich, String geburtsdatum, int hoechstgeschwindigkeit, int wagnisskennziffer, int plz, String land, String strasse, String hausnummer, String stadt, String bundesland) {
        // Arrange
        Partner partner = new Partner("John", "Doe", 'M', LocalDate.parse(geburtsdatum), land, strasse, hausnummer, String.valueOf(plz), stadt, bundesland);
        Fahrzeug fahrzeug = new Fahrzeug("ABC-1234", "Toyota", "Corolla", hoechstgeschwindigkeit, wagnisskennziffer);
        try (JsonReader reader = mock(Json.createReader(new FileReader(setup.getJson_preisPath())))) {
            JsonObject jsonObject = reader.readObject();
            given(jsonObject.getJsonNumber("factor").doubleValue()).willReturn(1.7);
            given(jsonObject.getJsonNumber("factorage").doubleValue()).willReturn(0.6);
            given(jsonObject.getJsonNumber("factorspeed").doubleValue()).willReturn(0.3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Act
        double preis = create.createPreis(monatlich, partner.getGeburtsdatum(), fahrzeug.getHoechstgeschwindigkeit());

        // Assert
        assertTrue(preis >= 0);
        double expectedPreis = ((LocalDate.now().getYear() - partner.getGeburtsdatum().getYear()) * 0.6 + fahrzeug.getHoechstgeschwindigkeit() * 0.3) * 1.7;
        if (!monatlich) {
            expectedPreis *= 11;
        }
        assertEquals(expectedPreis, expectedPreis, 0.0);
    }
}