package de.axa.robin.vertragsverwaltung.storage.editor;

import de.axa.robin.vertragsverwaltung.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.user_interaction.MenuSpring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(CustomTestConfig.class)
class HandleVertragTest {

    @InjectMocks
    private HandleVertrag handleVertrag;

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private MenuSpring menuSpring;

    @Mock
    private CreateData createData;

    @Mock
    private InputValidator inputValidator;

    @Mock
    private BindingResult bindingResult;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ExtendedModelMap();
    }

    /**
     * Testet, dass processPrintVertrag bei leerer VSNR den View "home" zurückgibt.
     */
    @Test
    void testProcessPrintVertrag_emptyVsnr() {
        String view = handleVertrag.processPrintVertrag("", model);
        assertEquals("home", view);
    }

    /**
     * Testet, dass processPrintVertrag bei einer ungültigen (nicht-numerischen) VSNR "Ungültige VSNR!" im Modell setzt.
     */
    @Test
    void testProcessPrintVertrag_invalidVsnr() {
        String view = handleVertrag.processPrintVertrag("abc", model);
        assertEquals("home", view);
        assertEquals("Ungültige VSNR!", model.asMap().get("result"));
    }

    /**
     * Testet den Fall, wenn der Vertrag nicht gefunden wird.
     */
    @Test
    void testProcessPrintVertrag_notFound() {
        String vsnr = "123";
        int vsnrInt = 123;
        when(vertragsverwaltung.getVertrag(vsnrInt)).thenReturn(null);

        String view = handleVertrag.processPrintVertrag(vsnr, model);
        assertEquals("home", view);
        assertEquals("Vertrag nicht gefunden!", model.asMap().get("result"));
        verify(menuSpring).setVsnr(vsnrInt);
    }

    /**
     * Testet den erfolgreichen Abruf eines vorhandenen Vertrags.
     */
    @Test
    void testProcessPrintVertrag_found() {
        String vsnr = "456";
        int vsnrInt = 456;
        Vertrag vertrag = createDummyVertrag(vsnrInt);
        when(vertragsverwaltung.getVertrag(vsnrInt)).thenReturn(vertrag);

        String view = handleVertrag.processPrintVertrag(vsnr, model);
        assertEquals("handleVertrag", view);
        // Überprüft, ob das Modell u.a. den Vertrag und die VSNR enthält
        assertTrue(model.asMap().containsKey("vertrag"));
        assertTrue(model.asMap().containsKey("vsnr"));
        verify(menuSpring).setVsnr(vsnrInt);
    }

    /**
     * Testet, dass die Methode showDelete den View "handleVertrag" zurückgibt und das Attribut "showFields" auf true gesetzt wird.
     */
    @Test
    void testShowDelete() {
        String view = handleVertrag.showDelete(model);
        assertEquals("handleVertrag", view);
        assertEquals(true, model.asMap().get("showFields"));
    }

    /**
     * Testet die deleteVertrag-Methode: Der Vertrag wird gelöscht und es wird eine Bestätigung im Modell gesetzt.
     */
    @Test
    void testDeleteVertrag() {
        int vsnr = 789;
        when(menuSpring.getVsnr()).thenReturn(vsnr);

        String view = handleVertrag.deleteVertrag(model);
        assertEquals("home", view);
        assertEquals("Vertrag erfolgreich gelöscht!", model.asMap().get("confirm"));
        verify(vertragsverwaltung).vertragLoeschen(vsnr);
    }

    /**
     * Testet editVertrag, wenn Validierungsfehler vorliegen. In diesem Fall
     * soll der View "handleVertrag" zurückgegeben und das Modell mit dem existierenden Vertrag befüllt werden.
     */
    @Test
    void testEditVertrag_withErrors() {
        Vertrag inputVertrag = createDummyVertrag(111);
        when(bindingResult.hasErrors()).thenReturn(true);

        // Um den richtigen Vertrag für das Setup bereitzustellen, wird der interne Zähler "handledVertrag" gesetzt.
        setHandledVertrag(111);
        Vertrag existingVertrag = createDummyVertrag(111);
        when(vertragsverwaltung.getVertrag(111)).thenReturn(existingVertrag);

        String view = handleVertrag.editVertrag(inputVertrag, bindingResult, true, model);
        assertEquals("handleVertrag", view);
        assertTrue(model.asMap().containsKey("editVisible"));
        assertEquals(true, model.asMap().get("editVisible"));
        // Das Modell sollte auch den (erneuerten) Vertrag enthalten
        assertTrue(model.asMap().containsKey("vertrag"));
    }

    /**
     * Testet editVertrag ohne Validierungsfehler: Der Vertrag wird gelöscht,
     * ein neuer Preis wird berechnet und eine Bestätigung im Modell gesetzt.
     */
    @Test
    void testEditVertrag_noErrors() {
        Vertrag inputVertrag = createDummyVertrag(222);
        when(bindingResult.hasErrors()).thenReturn(false);
        int vsnr = 222;
        when(menuSpring.getVsnr()).thenReturn(vsnr);
        inputVertrag.setMonatlich(true);

        double newPrice = 123.45;
        when(createData.createVertragAndSave(inputVertrag, true, vsnr)).thenReturn(newPrice);

        String view = handleVertrag.editVertrag(inputVertrag, bindingResult, false, model);
        assertEquals("home", view);
        String expectedMsg = "Vertrag mit VSNR " + vsnr + " erfolgreich bearbeitet! Neuer Preis: " +
                String.valueOf(newPrice).replace('.', ',') + "€";
        assertEquals(expectedMsg, model.asMap().get("confirm"));
        verify(vertragsverwaltung).vertragLoeschen(vsnr);
    }

    /**
     * Hilfsmethode zur Erstellung eines Dummy-Vertrags für die Tests.
     */
    private Vertrag createDummyVertrag(int vsnr) {
        Vertrag vertrag = new Vertrag();
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setAmtlichesKennzeichen("ABC123");
        fahrzeug.setHersteller("Hersteller");
        fahrzeug.setTyp("Typ");
        fahrzeug.setHoechstgeschwindigkeit(200);
        fahrzeug.setWagnisskennziffer(112);

        Partner partner = new Partner();
        partner.setVorname("Max");
        partner.setNachname("Mustermann");
        partner.setGeschlecht("M");
        partner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        partner.setStrasse("Musterstrasse");
        partner.setHausnummer("1");
        partner.setPlz("12345");
        partner.setStadt("Musterstadt");
        partner.setBundesland("Bayern");
        partner.setLand("Deutschland");

        vertrag.setFahrzeug(fahrzeug);
        vertrag.setPartner(partner);
        vertrag.setVsnr(vsnr);
        vertrag.setPreis(100.0);
        vertrag.setMonatlich(false);
        vertrag.setVersicherungsbeginn(LocalDate.now().minusDays(1));
        vertrag.setVersicherungsablauf(LocalDate.now().plusDays(365));
        vertrag.setAntragsDatum(LocalDate.now().minusDays(10));
        return vertrag;
    }

    /**
     * Hilfsmethode, um mittels Reflection den privaten Zähler "handledVertrag" in der Controller-Klasse zu setzen.
     */
    private void setHandledVertrag(int value) {
        try {
            java.lang.reflect.Field field = HandleVertrag.class.getDeclaredField("handledVertrag");
            field.setAccessible(true);
            field.set(handleVertrag, value);
        } catch (Exception e) {
            fail("Failed to set handledVertrag: " + e.getMessage());
        }
    }
}
