package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileReader;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
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
        create = new Create(mockVertragsverwaltung);
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
        try (JsonReader reader = mock(Json.createReader(new FileReader(setup.getPreisPath())))) {
            JsonObject jsonObject = reader.readObject();
            given(jsonObject.getJsonNumber("factor").doubleValue()).willReturn(1.5);
            given(jsonObject.getJsonNumber("factorage").doubleValue()).willReturn(0.5);
            given(jsonObject.getJsonNumber("factorspeed").doubleValue()).willReturn(0.2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Act
        double preis = create.createPreis(monatlich, partner, fahrzeug);

        // Assert
        assertTrue(preis >= 0);
        double expectedPreis = ((LocalDate.now().getYear() - partner.getGeburtsdatum().getYear()) * 0.5 + fahrzeug.getHoechstgeschwindigkeit() * 0.2) * 1.5;
        if (!monatlich) {
            expectedPreis *= 11;
        }
        assertEquals(preis, expectedPreis, 0.0);
    }
}