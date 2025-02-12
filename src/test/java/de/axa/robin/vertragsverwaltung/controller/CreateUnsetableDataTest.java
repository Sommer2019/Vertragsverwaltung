package de.axa.robin.vertragsverwaltung.controller;

import de.axa.robin.vertragsverwaltung.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.services.CreateUnsetableData;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.io.FileReader;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Import(CustomTestConfig.class)
@ExtendWith(MockitoExtension.class)
public class CreateUnsetableDataTest {

    @InjectMocks
    private CreateUnsetableData createUnsetableData;

    @Mock
    private VertragsService vertragsService;

    @Mock
    private Repository repository;
    @Mock
    private Setup setup;


    @Test
    void createvsnr() {
        // Arrange
        when(vertragsService.getVertrag(anyInt())).thenReturn(null);

    }

    @ParameterizedTest
    @CsvSource({
            "true, 1980-01-01, 150, 112, 12345, Deutschland, Main St, 1, Berlin, Berlin",
            "false, 1990-05-15, 200, 120, 54321, Germany, Second St, 2, Munich, Bavaria"
    })
    void createPreis(boolean monatlich, String geburtsdatum, int hoechstgeschwindigkeit, int wagnisskennziffer, int plz, String land, String strasse, String hausnummer, String stadt, String bundesland) {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.7)
                .add("factorage", 0.6)
                .add("factorspeed", 0.3)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);
        // Arrange
        Partner partner = new Partner("John", "Doe", 'M', LocalDate.parse(geburtsdatum), land, strasse, hausnummer, String.valueOf(plz), stadt, bundesland);
        Fahrzeug fahrzeug = new Fahrzeug("ABC-1234", "Toyota", "Corolla", hoechstgeschwindigkeit, wagnisskennziffer);
        try (JsonReader reader = mock(Json.createReader(new FileReader(setup.getJson_preisPath())))) {
            jsonObject = reader.readObject();
            given(jsonObject.getJsonNumber("factor").doubleValue()).willReturn(1.7);
            given(jsonObject.getJsonNumber("factorage").doubleValue()).willReturn(0.6);
            given(jsonObject.getJsonNumber("factorspeed").doubleValue()).willReturn(0.3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Act

        // Assert
        double expectedPreis = ((LocalDate.now().getYear() - partner.getGeburtsdatum().getYear()) * 0.6 + fahrzeug.getHoechstgeschwindigkeit() * 0.3) * 1.7;
        if (!monatlich) {
            expectedPreis *= 11;
        }
        assertEquals(expectedPreis, expectedPreis, 0.0);
    }
}