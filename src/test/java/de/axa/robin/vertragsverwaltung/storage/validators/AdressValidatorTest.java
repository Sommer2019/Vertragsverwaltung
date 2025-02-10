package de.axa.robin.vertragsverwaltung.storage.validators;

import de.axa.robin.vertragsverwaltung.config.Setup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class AdressValidatorTest {
    //ToDO Tests
    @Mock
    private Setup setup;

    @InjectMocks
    private AdressValidator adressValidator;

    private final String street = "Hauptstraße";
    private final String houseNumber = "11";
    private final String plz = "51429";
    private final String place = "Bergisch Gladbach";
    private final String bundesland = "Nodrhein-Westfalen";
    private final String land = "Deutschland";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled("not ready yet")
    void testValidateAddress_ValidAddress() throws Exception {
        // Setup mock
        String mockUrl = "http://mock-api-url.com";
        when(setup.getCheckURL()).thenReturn(mockUrl);
        when(setup.getTestURL()).thenReturn(mockUrl);

        // Mock the proxy settings to return valid values
        when(setup.getProxy_host()).thenReturn("proxy.example.com");
        when(setup.getProxy_port()).thenReturn(8080);

        // Mock the response from HttpURLConnection
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Mock JSON response from API
        String jsonResponse = "[{\"display_name\": \"Hauptstraße 11, 51429 Bergisch Gladbach, Nordrhein-Westfalen, Deutschland\"}]";
        InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes());
        when(mockConnection.getInputStream()).thenReturn(inputStream);

        // Mock the URI connection
        URI uri = new URI(mockUrl + "testquery");
        when(mockConnection.getURL()).thenReturn(uri.toURL());

        // Call the method with the test data
        boolean isValid = adressValidator.validateAddress(street, houseNumber, plz, place, bundesland, land);

        // Verify the result
        assertTrue(isValid, "Address should be valid.");
    }


    @Test
    void testValidateAddress_InvalidAddress() throws Exception {
        // Setup mock
        String mockUrl = "http://mock-api-url.com";
        when(setup.getCheckURL()).thenReturn(mockUrl);
        when(setup.getTestURL()).thenReturn(mockUrl);

        // Mock the response from HttpURLConnection
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Mock an invalid JSON response
        String jsonResponse = "[{\"display_name\": \"Wrong Street 999, 99999 Nowhere, Unknown, Unknown\"}]";
        InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes());
        when(mockConnection.getInputStream()).thenReturn(inputStream);

        // Mocking URI connection
        URI uri = new URI(mockUrl + "testquery");
        when(mockConnection.getURL()).thenReturn(uri.toURL());

        // Call the method
        boolean isValid = adressValidator.validateAddress(street, houseNumber, plz, place, bundesland, land);

        // Verify the result
        assertFalse(isValid, "Address should be invalid.");
    }

    @Disabled("not ready yet")
    @Test
    void testIsInternetAvailable_InternetAvailable() throws Exception {
        // Setup mock
        String mockUrl = "https://google.com";
        when(setup.getTestURL()).thenReturn(mockUrl);

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Call the method
        boolean isAvailable = adressValidator.isInternetAvailable();

        // Verify the result
        assertTrue(isAvailable, "Internet should be available.");
    }

    @Test
    void testIsInternetAvailable_InternetUnavailable() throws Exception {
        // Setup mock
        String mockUrl = "http://mock-test-url.com";
        when(setup.getTestURL()).thenReturn(mockUrl);

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Call the method
        boolean isAvailable = adressValidator.isInternetAvailable();

        // Verify the result
        assertFalse(isAvailable, "Internet should not be available.");
    }

    @Test
    void testIsProxyReachable_ProxyReachable() {
        // Setup mock
        when(setup.getProxy_host()).thenReturn("localhost");
        when(setup.getProxy_port()).thenReturn(3128);

        // Call the method
        boolean reachable = adressValidator.isProxyReachable("localhost", 3128);

        // Verify the result
        assertTrue(reachable, "Proxy should be reachable.");
    }

    @Test
    void testIsProxyReachable_ProxyNotReachable() {
        // Setup mock
        when(setup.getProxy_host()).thenReturn("invalid.proxy.com");
        when(setup.getProxy_port()).thenReturn(8080);

        // Call the method
        boolean reachable = adressValidator.isProxyReachable("invalid.proxy.com", 8080);

        // Verify the result
        assertFalse(reachable, "Proxy should not be reachable.");
    }
}
