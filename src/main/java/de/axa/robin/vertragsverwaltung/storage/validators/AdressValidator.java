package de.axa.robin.vertragsverwaltung.storage.validators;

import de.axa.robin.vertragsverwaltung.config.Setup;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * This class provides methods to validate addresses using an external service.
 */
@Component
public class AdressValidator {
    @Autowired
    private Setup setup;
    private static final Logger logger = LoggerFactory.getLogger(AdressValidator.class);
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final int TIMEOUT = 5000;

    /**
     * Validates the given address by querying an external service.
     *
     * @param street the street name
     * @param houseNumber the house number
     * @param plz the postal code
     * @param place the place or city
     * @param bundesland the federal state
     * @param land the country
     * @return true if the address is valid, false otherwise
     */
    public boolean validateAddress(String street, String houseNumber, String plz, String place, String bundesland, String land) {
        try {
            String query = buildQuery(street, houseNumber, plz, place, bundesland, land);
            URI uri = new URI(setup.getCheckURL() + query);

            configureProxy();

            if (!isInternetAvailable()) {
                return false;
            }

            HttpURLConnection conn = createConnection(uri);
            int status = handleRedirects(conn);

            if (status != HttpURLConnection.HTTP_OK) {
                logger.error("HTTP-Status Code {} empfangen.", status);
                return false;
            }

            return processResponse(conn, street, houseNumber, plz, place, bundesland, land);
        } catch (Exception e) {
            logger.error("Fehler aufgetreten: {}", e.getMessage(), e);
            return false;
        }
    }

    private String buildQuery(String street, String houseNumber, String plz, String place, String bundesland, String land) {
        return URLEncoder.encode(street + " " + houseNumber + ", " + plz + " " + place + ", " + bundesland + ", " + land, StandardCharsets.UTF_8);
    }

    private void configureProxy() {
        if (isProxyReachable(setup.getProxy_host(), setup.getProxy_port())) {
            System.setProperty("http.proxyHost", setup.getProxy_host());
            System.setProperty("http.proxyPort", String.valueOf(setup.getProxy_port()));
            System.setProperty("https.proxyHost", setup.getProxy_host());
            System.setProperty("https.proxyPort", String.valueOf(setup.getProxy_port()));
        }
    }

    public boolean isInternetAvailable() {
        try {
            URI uri = new URI(setup.getTestURL());
            HttpURLConnection urlConn = (HttpURLConnection) uri.toURL().openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(TIMEOUT);
            urlConn.connect();
            return urlConn.getResponseCode() == 200;
        } catch (IOException | URISyntaxException e) {
            logger.error("Internet connection check failed", e);
            return false;
        }
    }

    private HttpURLConnection createConnection(URI uri) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        return conn;
    }

    private int handleRedirects(HttpURLConnection conn) throws IOException, URISyntaxException {
        int status = conn.getResponseCode();
        if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
            String newUrl = conn.getHeaderField("Location");
            conn = (HttpURLConnection) new URI(newUrl).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
            status = conn.getResponseCode();
        }
        return status;
    }

    private boolean processResponse(HttpURLConnection conn, String street, String houseNumber, String plz, String place, String bundesland, String land) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String content = in.lines().reduce("", String::concat);
            JsonReader jsonReader = Json.createReader(new StringReader(content));
            JsonArray jsonArray = jsonReader.readArray();

            if (!jsonArray.isEmpty()) {
                return validateAddressComponents(jsonArray.getJsonObject(0), street, houseNumber, plz, place, bundesland, land);
            } else {
                logger.warn("Adresse existiert eventuell nicht!");
                return false;
            }
        }
    }

    private boolean validateAddressComponents(JsonObject address, String street, String houseNumber, String plz, String place, String bundesland, String land) {
        String displayName = address.getString("display_name").toLowerCase();
        String addressToValidate = (street.toLowerCase() + ", " + houseNumber.toLowerCase() + ", " + plz.toLowerCase() + ", " + place.toLowerCase() + ", " + bundesland.toLowerCase() + ", " + land.toLowerCase());
        logger.info("Comparing: {} with: {}", displayName, addressToValidate);
        if (displayName.contains(street.toLowerCase() + ",") &&
                displayName.contains(houseNumber.toLowerCase() + ",") &&
                displayName.contains(plz.toLowerCase() + ",") &&
                displayName.contains(place.toLowerCase() + ",") &&
                displayName.contains(bundesland.toLowerCase() + ",") &&
                displayName.contains(land.toLowerCase())) {
            return true;
        } else {
            logger.warn("Eventuell Fehler in Adresse!");
            return false;
        }
    }

    public boolean isProxyReachable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            logger.error("Proxy is not reachable", e);
            return false;
        }
    }
}