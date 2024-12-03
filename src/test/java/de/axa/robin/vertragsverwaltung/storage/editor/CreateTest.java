package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;
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
    private Input mockInput;
    private Setup setup;
    private Vertragsverwaltung mockVertragsverwaltung;

    @BeforeEach
    public void setUp() {
        setup = new Setup();
        mockInput = mock(Input.class);
        mockVertragsverwaltung = mock(Vertragsverwaltung.class);
        Output mockOutput = mock(Output.class);
        create = new Create(mockInput, mockVertragsverwaltung, mockOutput);
    }

    @Test
    void createFahrzeug() {
        // Arrange
        when(mockInput.getString(anyString(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn("GL-GL123")
                .thenReturn("Toyota")
                .thenReturn("Corolla");
        when(mockInput.getNumber(Integer.class, "die Höchstgeschwindigkeit", 50, 250, -1, false))
                .thenReturn(150);
        when(mockInput.getNumber(Integer.class, "die Wagnisskennziffer", -1, -1, 112, false))
                .thenReturn(112);

        // Act
        Fahrzeug fahrzeug = create.createFahrzeug();

        // Assert
        assertNotNull(fahrzeug);
        assertEquals("GL-GL123", fahrzeug.getAmtlichesKennzeichen());
        assertEquals("Toyota", fahrzeug.getHersteller());
        assertEquals("Corolla", fahrzeug.getTyp());
        assertEquals(150, fahrzeug.getHoechstgeschwindigkeit());
        assertEquals(112, fahrzeug.getWagnisskennziffer());
    }

    @Test
    void createPartner() {
        // Arrange
        when(mockInput.getString(anyString(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn("John")
                .thenReturn("Doe")
                .thenReturn("Deutschland")
                .thenReturn("Hauptstraße")
                .thenReturn("11")
                .thenReturn("Bergisch Gladbach")
                .thenReturn("Nordrhein-Westfalen");
        when(mockInput.getChar(any(), anyString())).thenReturn('M');
        when(mockInput.getDate(anyString(), any(), any())).thenReturn(LocalDate.of(1980, 1, 1));
        when(mockInput.getNumber(Integer.class, "die PLZ", -1, -1, -1, false)).thenReturn(51465);

        // Act
        Partner partner = create.createPartner();

        // Assert
        assertNotNull(partner);
        assertEquals("John", partner.getVorname());
        assertEquals("Doe", partner.getNachname());
        assertEquals('M', partner.getGeschlecht());
        assertEquals(LocalDate.of(1980, 1, 1), partner.getGeburtsdatum());
        assertEquals("Deutschland", partner.getLand());
        assertEquals("Hauptstraße", partner.getStrasse());
        assertEquals("11", partner.getHausnummer());
        assertEquals(51465, partner.getPlz());
        assertEquals("Bergisch Gladbach", partner.getStadt());
        assertEquals("Nordrhein-Westfalen", partner.getBundesland());
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

    @Test
    void createVertrag() {
        // Arrange
        when(mockInput.getString(anyString(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn("GL-GL123")
                .thenReturn("Toyota")
                .thenReturn("Corolla")
                .thenReturn("John")
                .thenReturn("Doe")
                .thenReturn("Deutschland")
                .thenReturn("Hauptstraße")
                .thenReturn("11")
                .thenReturn("Bergisch Gladbach")
                .thenReturn("Nordrhein-Westfalen");
        when(mockInput.getNumber(Integer.class, "die Höchstgeschwindigkeit", 50, 250, -1, false))
                .thenReturn(150);
        when(mockInput.getNumber(Integer.class, "die Wagnisskennziffer", -1, -1, 112, false))
                .thenReturn(112);
        when(mockInput.getChar(any(), anyString())).thenReturn('M');
        when(mockInput.getDate(anyString(), any(), any())).thenReturn(LocalDate.of(1980, 1, 1));
        when(mockInput.getNumber(Integer.class, "die PLZ", -1, -1, -1, false)).thenReturn(51465);
        when(mockInput.getChar(null, "Abbuchung monatlich oder jährlich? (y/m): ")).thenReturn('m');
        when(mockInput.getDate("den Versicherungsbeginn", LocalDate.now(), null)).thenReturn(LocalDate.now());
        when(mockVertragsverwaltung.getVertrag(anyInt())).thenReturn(null);
        when(mockInput.getChar(any(), eq("erstellt"))).thenReturn('y'); // Ensure confirmation is 'y'

        // Act
        create.createVertrag();

        // Assert
        ArgumentCaptor<Vertrag> vertragCaptor = ArgumentCaptor.forClass(Vertrag.class);
        verify(mockVertragsverwaltung).vertragAnlegen(vertragCaptor.capture());
        Vertrag vertrag = vertragCaptor.getValue();

        assertNotNull(vertrag);
        assertEquals("GL-GL123", vertrag.getFahrzeug().getAmtlichesKennzeichen());
        assertEquals("John", vertrag.getPartner().getVorname());
        assertTrue(vertrag.getMonatlich());
        assertEquals(LocalDate.now(), vertrag.getVersicherungsbeginn());
        assertEquals(LocalDate.now().plusYears(1), vertrag.getVersicherungsablauf());
        assertTrue(vertrag.getPreis() >= 0); // Assuming a valid calculation should return a positive price
    }
}