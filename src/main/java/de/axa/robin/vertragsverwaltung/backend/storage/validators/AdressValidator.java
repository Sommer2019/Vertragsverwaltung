package de.axa.robin.vertragsverwaltung.backend.storage.validators;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
public class AdressValidator {
    // Klassen einlesen
    private final Setup setup = new Setup();

    public boolean validateAddress(String street, String houseNumber, String plz, String place, String bundesland, String land) {
        try {
            String query = URLEncoder.encode(street + " " + houseNumber + ", " + plz + " " + place + ", " + bundesland + ", " + land, StandardCharsets.UTF_8);
            String NOMINATIM_URL = setup.getCheckURL();
            URI uri = new URI(NOMINATIM_URL + query);
            System.out.println(uri);
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");

            // Check if proxy is reachable
            if (isProxyReachable(setup.getHost(), setup.getPort())) {
                System.setProperty("http.proxyHost", setup.getHost());
                System.setProperty("http.proxyPort", String.valueOf(setup.getPort()));
                System.setProperty("https.proxyHost", setup.getHost());
                System.setProperty("https.proxyPort", String.valueOf(setup.getPort()));
            }

            // Check internet connection
            if (!isInternetAvailable(setup.getPort())) {
                if (isProxyReachable(setup.getHost(), setup.getPort())) {
                    System.err.println("Proxy ist erreichbar, aber keine Internetverbindung.");
                } else {
                    System.err.println("Keine Internetverbindung und Proxy ist nicht erreichbar.");
                }
                return false;
            }

            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
                String newUrl = conn.getHeaderField("Location");
                conn = (HttpURLConnection) new URI(newUrl).toURL().openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                status = conn.getResponseCode();
            }

            if (status != HttpURLConnection.HTTP_OK) {
                System.err.println("HTTP-Status Code " + status + " empfangen.");
                System.err.println("Eventuell ungültige Eingabe!");
                return false;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JsonReader jsonReader = Json.createReader(new StringReader(content.toString()));
            JsonArray jsonArray = jsonReader.readArray();
            jsonReader.close();

            if (!jsonArray.isEmpty()) {
                JsonObject address = jsonArray.getJsonObject(0);
                String displayName = address.getString("display_name").toLowerCase();
                if (displayName.contains(street.toLowerCase() + ",") &&
                        displayName.contains(houseNumber.toLowerCase() + ",") &&
                        displayName.contains(plz.toLowerCase() + ",") &&
                        displayName.contains(place.toLowerCase() + ",") &&
                        displayName.contains(bundesland.toLowerCase() + ",") &&
                        displayName.contains(land.toLowerCase())) {
                    return true;
                } else {
                    System.err.println("Eventuell Fehler in Adresse!");
                    return false;
                }
            } else {
                System.err.println("Adresse existiert eventuell nicht!");
                return false;
            }

        } catch (ConnectException | SocketTimeoutException e) {
            System.err.println("Connection timed out: " + e.getMessage());
            e.printStackTrace();
            System.err.println("Eventuell ungültige Eingabe!");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isProxyReachable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isInternetAvailable(int portfix) {
        if (isProxyReachable(setup.getHost(), portfix)) {
            System.setProperty("http.proxyHost", setup.getHost());
            System.setProperty("http.proxyPort", String.valueOf(portfix));
            System.setProperty("https.proxyHost", setup.getHost());
            System.setProperty("https.proxyPort", String.valueOf(portfix));
        }
        try {
            URI uri = new URI(setup.getTestURL());
            HttpURLConnection urlConn = (HttpURLConnection) uri.toURL().openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(5000); // 5 seconds
            urlConn.connect();
            return urlConn.getResponseCode() == 200;
        } catch (IOException | URISyntaxException e) {
            return false;
        }
    }

}
