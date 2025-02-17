package de.axa.robin.vertragsverwaltung.validators;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.validators.AdressValidator;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdressValidatorTest {

    private AdressValidator adressValidator;
    private Setup setup;

    @BeforeEach
    public void setUp() throws Exception {
        // Erstellen eines Mock-Setup-Objekts
        setup = mock(Setup.class);
        when(setup.getProxy_host()).thenReturn("127.0.0.1");
        when(setup.getProxy_port()).thenReturn(8080);

        adressValidator = new AdressValidator();

        // Injection des gemockten Setup-Objekts in die private Variable 'setup'
        Field setupField = AdressValidator.class.getDeclaredField("setup");
        setupField.setAccessible(true);
        setupField.set(adressValidator, setup);
    }

    /**
     * Testet den Fall einer gültigen Adresse, bei der der externe Service eine JSON-Antwort mit
     * passendem "display_name" zurückliefert.
     */
    @Test
    void testValidateAddress_valid() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            // Dispatcher simuliert zwei Endpunkte:
            // - /test: Für den Internet-Check (HTTP 200)
            // - /check: Liefert eine JSON-Antwort mit gültigen Adressdaten
            Dispatcher dispatcher = new Dispatcher() {
                @NotNull
                @Override
                public MockResponse dispatch(RecordedRequest request) {
                    assert request.getPath() != null;
                    if (request.getPath().startsWith("/test")) {
                        return new MockResponse().setResponseCode(200);
                    } else if (request.getPath().startsWith("/check")) {
                        String responseBody = "[{\"display_name\": \"musterstrasse, 1, 12345, musterstadt, bayern, deutschland\"}]";
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(responseBody)
                                .addHeader("Content-Type", "application/json");
                    }
                    return new MockResponse().setResponseCode(404);
                }
            };
            server.setDispatcher(dispatcher);
            server.start();

            // Die URLs für den Internet-Check und den Address-Check werden auf den MockWebServer umgeleitet
            when(setup.getTestURL()).thenReturn(server.url("/test").toString());
            when(setup.getCheckURL()).thenReturn(server.url("/check?query=").toString());

            // Vorgabe der Adresskomponenten
            String street = "Musterstrasse";
            String houseNumber = "1";
            String plz = "12345";
            String place = "Musterstadt";
            String bundesland = "Bayern";
            String land = "Deutschland";

            boolean result = adressValidator.validateAddress(street, houseNumber, plz, place, bundesland, land);
            assertTrue(result, "Die Adresse sollte als gültig erkannt werden.");
        }
    }

    /**
     * Testet den Fall einer ungültigen Adresse (leere JSON-Antwort vom externen Service).
     */
    @Test
    void testValidateAddress_invalidEmptyResponse() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            // Dispatcher liefert für /test einen HTTP 200 und für /check einen leeren JSON-Array
            Dispatcher dispatcher = new Dispatcher() {
                @NotNull
                @Override
                public MockResponse dispatch(RecordedRequest request) {
                    assert request.getPath() != null;
                    if (request.getPath().startsWith("/test")) {
                        return new MockResponse().setResponseCode(200);
                    } else if (request.getPath().startsWith("/check")) {
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody("[]")
                                .addHeader("Content-Type", "application/json");
                    }
                    return new MockResponse().setResponseCode(404);
                }
            };
            server.setDispatcher(dispatcher);
            server.start();

            when(setup.getTestURL()).thenReturn(server.url("/test").toString());
            when(setup.getCheckURL()).thenReturn(server.url("/check?query=").toString());

            boolean result = adressValidator.validateAddress("Musterstrasse", "1", "12345", "Musterstadt", "Bayern", "Deutschland");
            assertFalse(result, "Bei leerer Antwort sollte die Adresse als ungültig erkannt werden.");
        }
    }

    /**
     * Testet, dass isInternetAvailable() true zurückgibt, wenn der Test-URL-Endpunkt HTTP 200 liefert.
     */
    @Test
    void testIsInternetAvailable_success() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(200));
            server.start();

            when(setup.getTestURL()).thenReturn(server.url("/test").toString());
            boolean available = adressValidator.isInternetAvailable();
            assertTrue(available, "Die Internetverbindung sollte als verfügbar erkannt werden.");
        }
    }

    /**
     * Testet, dass isInternetAvailable() false zurückgibt, wenn der Test-URL-Endpunkt keinen HTTP 200 Status liefert.
     */
    @Test
    void testIsInternetAvailable_failure() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(500));
            server.start();

            when(setup.getTestURL()).thenReturn(server.url("/test").toString());
            boolean available = adressValidator.isInternetAvailable();
            assertFalse(available, "Bei einem Fehlerstatus sollte die Internetverbindung als nicht verfügbar erkannt werden.");
        }
    }

    /**
     * Testet die Methode isProxyReachable() für einen vermutlich nicht erreichbaren Proxy.
     */
    @Test
    void testIsProxyReachable_unreachable() {
        // Hier wird davon ausgegangen, dass auf Port 9999 kein Dienst läuft.
        boolean reachable = adressValidator.isProxyReachable("127.0.0.1", 9999);
        assertFalse(reachable, "Der Proxy sollte als nicht erreichbar erkannt werden.");
    }
}
