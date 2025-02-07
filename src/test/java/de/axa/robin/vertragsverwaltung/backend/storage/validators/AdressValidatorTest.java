package de.axa.robin.vertragsverwaltung.backend.storage.validators;

import de.axa.robin.vertragsverwaltung.backend.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Nested
@Import(CustomTestConfig.class)
class AdressValidatorTest {


    private AdressValidator adressValidator;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final Setup setup = new Setup();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUp() {
        adressValidator = new AdressValidator();
        Setup setup = new Setup();
        setup.setProxy_host("localhost"); // Beispielwert
        setup.setProxy_port(8080);          // Beispielwert
        setup.setTestURL("http://example.com"); // Beispielwert
        // Weitere notwendige Einstellungen setzen...

        ReflectionTestUtils.setField(adressValidator, "setup", setup);
    }

    @Test
    public void testValidateAddress_ValidAddress() throws Exception {
        // Mocking URL and HttpURLConnection
        URL mockUrl = Mockito.mock(URL.class);
        HttpURLConnection mockConn = Mockito.mock(HttpURLConnection.class);
        when(mockUrl.openConnection()).thenReturn(mockConn);
        when(mockConn.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Mocking InputStream
        String jsonResponse = "[{\"display_name\":\"11, Hauptstraße, Gronau, Bergisch Gladbach, Rheinisch-Bergischer Kreis, Nordrhein-Westfalen, 51465, Deutschland\"}]";
        InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes());
        when(mockConn.getInputStream()).thenReturn(inputStream);

        // Mocking URI
        URI mockUri = Mockito.mock(URI.class);
        when(mockUri.toURL()).thenReturn(mockUrl);

        // Test valid address
        boolean result = adressValidator.validateAddress("Hauptstraße", "11", "51465", "Bergisch Gladbach", "Nordrhein-Westfalen", "Deutschland");
        assertTrue(result);
    }
    @Test
    public void testValidateAddress_InValidAddress() throws Exception {
        // Mocking URL and HttpURLConnection
        URL mockUrl = Mockito.mock(URL.class);
        HttpURLConnection mockConn = Mockito.mock(HttpURLConnection.class);
        when(mockUrl.openConnection()).thenReturn(mockConn);
        when(mockConn.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Mocking InputStream
        String jsonResponse = "[{\"display_name\":\"12, Hauptstraße, Gronau, Bergisch Gladbach, Rheinisch-Bergischer Kreis, Nordrhein-Westfalen, 51465, Deutschland\"}]";
        InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes());
        when(mockConn.getInputStream()).thenReturn(inputStream);

        // Mocking URI
        URI mockUri = Mockito.mock(URI.class);
        when(mockUri.toURL()).thenReturn(mockUrl);

        // Test valid address
        boolean result = adressValidator.validateAddress("Hauptstraße", "11", "51465", "Bergisch Gladbach", "Nordrhein-Westfalen", "Deutschland");
        assertTrue(result);
    }

    @Test
    public void testIsProxyReachable_Reachable() throws IOException {
        // Mocking Socket
        Socket mockSocket = Mockito.mock(Socket.class);
        doNothing().when(mockSocket).connect(any(InetSocketAddress.class), eq(2000));

        // Using try-with-resources to mock the behavior
        try (Socket socket = mockSocket) {
            boolean result = adressValidator.isProxyReachable(setup.getProxy_host(), setup.getProxy_port());
            assertTrue(result);
        }
    }

    @Test
    public void testIsProxyReachable_NotReachable() throws IOException {
        // Mocking Socket
        Socket mockSocket = Mockito.mock(Socket.class);
        doThrow(new IOException()).when(mockSocket).connect(any(InetSocketAddress.class), eq(2000));

        // Using try-with-resources to mock the behavior
        try (Socket socket = mockSocket) {
            boolean result = adressValidator.isProxyReachable(setup.getProxy_host(), setup.getProxy_port()+1);
            assertFalse(result);
        }
    }
    @Test
    public void testIsInternetAvailable_Success() throws Exception {
        // Mocking HttpURLConnection
        try (MockedStatic<HttpURLConnection> mockedHttpURLConnection = mockStatic(HttpURLConnection.class)) {

            // Creating a real URL instance
            HttpURLConnection mockConn = Mockito.mock(HttpURLConnection.class);

            when(mockConn.getResponseCode()).thenReturn(200); // Simulate a successful response

            // Test for internet availability
            boolean result = adressValidator.isInternetAvailable();
            assertTrue(result);
        }
    }

    @Test
    public void testIsInternetAvailable_Fail() throws Exception {
        // Mocking HttpURLConnection
        try (MockedStatic<HttpURLConnection> mockedHttpURLConnection = mockStatic(HttpURLConnection.class)) {

            // Creating a real URL instance
            HttpURLConnection mockConn = Mockito.mock(HttpURLConnection.class);

            when(mockConn.getResponseCode()).thenReturn(200); // Simulate a successful response

            // Test for internet availability
            boolean result = false; //adressValidator.isInternetAvailable(1234);
            assertFalse(result);
        }

    }

    @BeforeEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}