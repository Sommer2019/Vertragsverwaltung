package de.axa.robin.vertragsverwaltung.controller;

import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import de.axa.robin.vertragsverwaltung.services.CreateUnsetableData;
import de.axa.robin.vertragsverwaltung.validators.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateVertragTest {

    @InjectMocks
    private CreateVertrag createVertragController;

    @Mock
    private VertragsService vertragsService;

    @Mock
    private CreateUnsetableData createUnsetableData;

    @Mock
    private MenuSpring menuSpring;

    @Mock
    private InputValidator inputValidator;

    @BeforeEach
    void setUp() {
        // Initialisiert alle mit @Mock annotierten Objekte
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testet den GET-Endpunkt (/createVertrag).
     * Erwartet, dass das Model die Attribute "vertrag" und "vsnr" enthält
     * und der View-Name "createVertrag" zurückgegeben wird.
     */
    @Test
    void testGetCreateVertrag() {
        // Arrange
        Model model = new ExtendedModelMap();
        int vsnr = 123;
        when(menuSpring.getVsnr()).thenReturn(vsnr);

        // Act
        String viewName = createVertragController.createVertrag(model);

        // Assert
        assertEquals("createVertrag", viewName);
        assertTrue(model.containsAttribute("vertrag"));
        assertTrue(model.containsAttribute("vsnr"));
        assertEquals(vsnr, model.getAttribute("vsnr"));
    }

    /**
     * Testet den POST-Endpunkt (/createVertrag) im Falle von Validierungsfehlern.
     * Es wird erwartet, dass der View "createVertrag" erneut angezeigt wird und
     * keine Speicherung erfolgt.
     */
    @Test
    void testPostCreateVertragWithErrors() {
        // Arrange
        // Erstellen eines minimalen Vertrag-Objekts (hier wird der Konstruktor mit Fahrzeug und Partner verwendet)
        Partner partner = new Partner();
        Fahrzeug fahrzeug = new Fahrzeug();
        Vertrag vertrag = new Vertrag(fahrzeug, partner);
        vertrag.setVersicherungsbeginn(LocalDate.now());
        BindingResult bindingResult = mock(BindingResult.class);
        Model model = new ExtendedModelMap();
        when(bindingResult.hasErrors()).thenReturn(true);
        int vsnr = 456;
        when(menuSpring.getVsnr()).thenReturn(vsnr);

        // Act
        String viewName = createVertragController.createVertrag(vertrag, bindingResult, model);

        // Assert
        assertEquals("createVertrag", viewName);
        assertTrue(model.containsAttribute("vsnr"));
        assertEquals(vsnr, model.getAttribute("vsnr"));
    }

    /**
     * Testet den POST-Endpunkt (/createVertrag) ohne Validierungsfehler.
     * Erwartet wird, dass eine Vertrag-Entität erstellt, gespeichert und der View "home" zurückgegeben wird.
     */
    @Test
    void testPostCreateVertragWithoutErrors() {
        // Arrange
        Partner partner = new Partner();
        Fahrzeug fahrzeug = new Fahrzeug();
        Vertrag inputVertrag = new Vertrag(fahrzeug, partner);
        inputVertrag.setVersicherungsbeginn(LocalDate.now());
        BindingResult bindingResult = mock(BindingResult.class);
        Model model = new ExtendedModelMap();
        when(bindingResult.hasErrors()).thenReturn(false);
        int vsnr = 789;
        double preis = 250.75;

        // Act
        String viewName = createVertragController.createVertrag(inputVertrag, bindingResult, model);

        // Assert
        assertEquals("home", viewName);
        assertTrue(model.containsAttribute("confirm"));
        String confirmMessage = (String) model.getAttribute("confirm");
        assert confirmMessage != null;
        assertTrue(confirmMessage.contains(String.valueOf(vsnr)));
        // Überprüfung, ob der Preis korrekt formatiert wurde (z. B. "250,75")
        assertTrue(confirmMessage.contains(String.format(Locale.GERMANY, "%.2f", preis)));
    }

    /**
     * Testet den Preisberechnungs-Endpunkt (/createPreis),
     * wenn die PLZ des Partners nicht leer ist.
     */
    @Test
    void testCreatePreisWhenPlzNotEmpty() {
        // Arrange
        Vertrag vertrag = new Vertrag();
        Partner partner = new Partner();
        partner.setPlz("12345");
        partner.setGeburtsdatum(LocalDate.of(1990, 1, 1));
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setHoechstgeschwindigkeit(180);
        vertrag.setPartner(partner);
        vertrag.setFahrzeug(fahrzeug);
        vertrag.setMonatlich(true);

        double preis = 150.50;

        // Act
        Map<String, Object> response = createVertragController.createPreis(vertrag);

        // Assert
        String expectedPreis = String.format(Locale.GERMANY, "%.2f €", preis);
        assertEquals(expectedPreis, response.get("preis"));
    }

    /**
     * Testet den Preisberechnungs-Endpunkt (/createPreis),
     * wenn die PLZ leer und der Versicherungsablauf null ist.
     */
    @Test
    void testCreatePreisWhenPlzEmptyAndVersicherungsablaufNull() {
        // Arrange
        Vertrag vertrag = new Vertrag();
        Partner partner = new Partner();
        partner.setPlz("");
        vertrag.setPartner(partner);
        vertrag.setVersicherungsablauf(null);

        // Act
        Map<String, Object> response = createVertragController.createPreis(vertrag);

        // Assert
        assertEquals("--,-- €", response.get("preis"));
    }

    /**
     * Testet den Preisberechnungs-Endpunkt (/createPreis),
     * wenn die PLZ leer ist und der Versicherungsablauf in der Vergangenheit liegt.
     */
    @Test
    void testCreatePreisWhenPlzEmptyAndVersicherungsablaufBeforeToday() {
        // Arrange
        Vertrag vertrag = new Vertrag();
        Partner partner = new Partner();
        partner.setPlz("");
        vertrag.setPartner(partner);
        vertrag.setVersicherungsablauf(LocalDate.now().minusDays(1));

        // Act
        Map<String, Object> response = createVertragController.createPreis(vertrag);

        // Assert
        assertEquals("0,00 €", response.get("preis"));
    }

    /**
     * Testet den Preisberechnungs-Endpunkt (/createPreis),
     * wenn die PLZ leer ist und der Versicherungsablauf in der Zukunft liegt.
     */
    @Test
    void testCreatePreisWhenPlzEmptyAndVersicherungsablaufNotBeforeToday() {
        // Arrange
        Vertrag vertrag = new Vertrag();
        Partner partner = new Partner();
        partner.setPlz("");
        vertrag.setPartner(partner);
        vertrag.setVersicherungsablauf(LocalDate.now().plusDays(1));

        // Act
        Map<String, Object> response = createVertragController.createPreis(vertrag);

        // Assert
        assertEquals("--,-- €", response.get("preis"));
    }
}
