package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.io.FileReader;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Import(CustomTestConfig.class)
@ExtendWith(MockitoExtension.class)
public class CreateDataDataTest {

    @InjectMocks
    private CreateData createData;

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private Repository repository;
    @Mock
    private Setup setup;


    @Test
    void createvsnr() {
        // Arrange
        when(vertragsverwaltung.getVertrag(anyInt())).thenReturn(null);

        // Act
        int vsnr = createData.createvsnr();

        // Assert
        assertTrue(vsnr >= 10000000 && vsnr <= 99999999);
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
        double preis = createData.createPreis(monatlich, partner.getGeburtsdatum(), fahrzeug.getHoechstgeschwindigkeit());

        // Assert
        assertTrue(preis >= 0);
        double expectedPreis = ((LocalDate.now().getYear() - partner.getGeburtsdatum().getYear()) * 0.6 + fahrzeug.getHoechstgeschwindigkeit() * 0.3) * 1.7;
        if (!monatlich) {
            expectedPreis *= 11;
        }
        assertEquals(expectedPreis, expectedPreis, 0.0);
    }

    @Test
    public void testCreateVertragAndSave() {
        // Arrange: Erstelle Beispiel-Daten
        Partner partner = new Partner("John", "Doe", 'M', LocalDate.of(1990, 1, 1),
                "Germany", "Main Street", "10", "12345", "Berlin", "Berlin");
        Fahrzeug fahrzeug = new Fahrzeug("ABC-123", "VW", "Golf", 200, 42);
        Vertrag inputVertrag = new Vertrag(0, false, 0.0,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 1, 1),
                LocalDate.now(),
                fahrzeug, partner);

        boolean monatlich = true;
        int vsnr = 999;
        double stubPreis = 123.45;

        // Erstelle einen Spy, um die interne Methode createPreis zu stubben
        CreateData spyCreateData = spy(createData);
        doReturn(stubPreis)
                .when(spyCreateData)
                .createPreis(eq(monatlich), eq(partner.getGeburtsdatum()), eq(fahrzeug.getHoechstgeschwindigkeit()));

        // Act: Aufruf der zu testenden Methode
        double resultPreis = spyCreateData.createVertragAndSave(inputVertrag, monatlich, vsnr);

        // Assert: Überprüfe den Rückgabewert (Preis)
        assertEquals(stubPreis, resultPreis, 0.001, "Der zurückgegebene Preis muss dem Stub-Wert entsprechen.");

        // Erfasse das an die Methode vertragAnlegen übergebene Argument
        ArgumentCaptor<Vertrag> vertragCaptor = ArgumentCaptor.forClass(Vertrag.class);
        verify(vertragsverwaltung).vertragAnlegen(vertragCaptor.capture());
        Vertrag savedVertrag = vertragCaptor.getValue();

        // Überprüfe, ob die Werte im gespeicherten Vertrag den Erwartungen entsprechen
        assertEquals(vsnr, savedVertrag.getVsnr(), "Die Vertragsnummer muss dem übergebenen vsnr entsprechen.");
        assertEquals(monatlich, savedVertrag.isMonatlich(), "Der Wert für 'monatlich' muss übernommen worden sein.");
        assertEquals(stubPreis, savedVertrag.getPreis(), 0.001, "Der berechnete Preis muss übernommen worden sein.");
        assertEquals(inputVertrag.getVersicherungsbeginn(), savedVertrag.getVersicherungsbeginn(), "Versicherungsbeginn muss übernommen werden.");
        assertEquals(inputVertrag.getVersicherungsablauf(), savedVertrag.getVersicherungsablauf(), "Versicherungsablauf muss übernommen werden.");
        assertEquals(inputVertrag.getAntragsDatum(), savedVertrag.getAntragsDatum(), "Antragsdatum muss übernommen werden.");

        // Überprüfe auch, ob die neu erstellten Partner- und Fahrzeugdaten stimmen
        assertEquals("John", savedVertrag.getPartner().getVorname(), "Partner-Vorname muss 'John' sein.");
        assertEquals("Doe", savedVertrag.getPartner().getNachname(), "Partner-Nachname muss 'Doe' sein.");
        assertEquals("ABC-123", savedVertrag.getFahrzeug().getAmtlichesKennzeichen(), "Das amtliche Kennzeichen muss 'ABC-123' sein.");
    }
}