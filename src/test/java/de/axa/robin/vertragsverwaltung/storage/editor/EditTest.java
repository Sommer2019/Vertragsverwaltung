package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Input;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EditTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private Edit edit;
    private Input mockInput;
    private Output mockOutput;
    private Vertragsverwaltung mockVertragsverwaltung;

    @BeforeEach
    public void setUp() {
        mockInput = mock(Input.class);
        mockOutput = mock(Output.class);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        mockVertragsverwaltung = mock(Vertragsverwaltung.class);
        edit = new Edit(mockInput);
    }

    @Test
    void editVertrag() {
        // Arrange
        Vertrag mockVertrag = mock(Vertrag.class);
        when(mockVertrag.getMonatlich()).thenReturn(true);
        when(mockVertrag.getPartner()).thenReturn(null); // You can mock a Partner object if needed
        when(mockVertrag.getFahrzeug()).thenReturn(null); // You can mock a Fahrzeug object if needed
        when(mockInput.getNumber(Integer.class, "", -1, -1, -1, false)).thenReturn(1);

        // Act
        edit.editVertrag(mockVertrag);

        // Assert
        verify(mockOutput, times(1)).druckeVertrag(mockVertrag);
        verify(mockOutput, times(1)).editMenu();
    }

    @Test
    void editAllgemeineDaten() {
        // Arrange
        Vertrag mockVertrag = mock(Vertrag.class);
        when(mockVertrag.getAntragsDatum()).thenReturn(LocalDate.now());
        when(mockInput.getNumber(Integer.class, "", -1, -1, -1, false)).thenReturn(1, 2); // First 1, then 2

        // Act
        edit.editAllgemeineDaten(mockVertrag);

        // Assert
        verify(mockOutput, times(1)).editDates();
        verify(mockVertrag, times(1)).setVersicherungsbeginn(any());
        verify(mockVertrag, times(1)).setVersicherungsablauf(any());
        verify(mockVertrag, times(1)).setAntragsDatum(any());
    }

    @Test
    void editPersonendaten() {
        // Arrange
        Vertrag mockVertrag = mock(Vertrag.class);
        when(mockInput.getNumber(Integer.class, "", -1, -1, -1, false)).thenReturn(1, 2); // First 1, then 2
        when(mockVertrag.getPartner()).thenReturn(new Partner("John", "Doe", 'M', LocalDate.of(1980, 1, 1), "Germany", "Main St", "1", 12345, "Berlin", "Berlin"));

        // Act
        edit.editPersonendaten(mockVertrag);

        // Assert
        verify(mockOutput, times(1)).editPerson();
        verify(mockVertrag.getPartner(), times(1)).setVorname(anyString());
        verify(mockVertrag.getPartner(), times(1)).setNachname(anyString());
        verify(mockVertrag.getPartner(), times(1)).setGeschlecht(anyChar());
        verify(mockVertrag.getPartner(), times(1)).setGeburtsdatum(any());
    }

    @Test
    void editFahrzeugdaten() {
        // Arrange
        Vertrag mockVertrag = mock(Vertrag.class);
        when(mockInput.getNumber(Integer.class, "", -1, -1, -1, false)).thenReturn(1, 2); // First 1, then 2
        when(mockVertrag.getFahrzeug()).thenReturn(new Fahrzeug("ABC-1234", "Toyota", "Corolla", 150, 112));

        // Act
        edit.editFahrzeugdaten(mockVertrag);

        // Assert
        verify(mockOutput, times(1)).editFahrzeug();
        verify(mockVertrag.getFahrzeug(), times(1)).setAmtlichesKennzeichen(anyString());
        verify(mockVertrag.getFahrzeug(), times(1)).setHersteller(anyString());
        verify(mockVertrag.getFahrzeug(), times(1)).setTyp(anyString());
        verify(mockVertrag.getFahrzeug(), times(1)).setHoechstgeschwindigkeit(anyInt());
        verify(mockVertrag.getFahrzeug(), times(1)).setWagnisskennziffer(anyInt());
    }

    @Test
    void editmenu() {
        // Arrange
        List<Vertrag> vertrage = new ArrayList<>();
        vertrage.add(mock(Vertrag.class));
        when(mockInput.getNumber(Integer.class, "", -1, -1, -1, false)).thenReturn(1, 3); // First 1, then 3

        // Act
        edit.editmenu(vertrage);

        // Assert
        verify(mockOutput, times(1)).editwhat();
        verify(mockInput, times(1)).getNumber(Integer.class, "", -1, -1, -1, false);
        verify(mockVertragsverwaltung, times(1)).getVertrag(anyInt());
    }

    @Test
    void recalcprice() {
        // Arrange
        List<Vertrag> vertrage = new ArrayList<>();
        vertrage.add(mock(Vertrag.class));
        when(mockInput.getNumber(Double.class, "", -1, -1, -1, false)).thenReturn(1.5, 0.5, 0.2);
        when(mockVertragsverwaltung.getVertrag(anyInt())).thenReturn(mock(Vertrag.class));

        // Act
        edit.recalcprice(vertrage);

        // Assert
        assertTrue(outContent.toString().contains("-------------------------------"));
    }
}