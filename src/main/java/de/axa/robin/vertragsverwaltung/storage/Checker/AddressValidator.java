package de.axa.robin.vertragsverwaltung.storage.Checker;

import de.axa.robin.vertragsverwaltung.user_interaction.Input.Allgemein;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import jakarta.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class AddressValidator {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public static boolean validateAddress(String street, String houseNumber, String plz, String place, String bundesland) {
        try {
            String query = URLEncoder.encode(street + " " + houseNumber + ", " + plz + " " + place + ", " + bundesland, StandardCharsets.UTF_8);
            String url = NOMINATIM_URL + query;

            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");

            String host = "localhost";
            int port = 3128;
            // Check if proxy is reachable
            if (isProxyReachable(host, port)) {
                System.setProperty("http.proxyHost", host);
                System.setProperty("http.proxyPort", String.valueOf(port));
                System.setProperty("https.proxyHost", host);
                System.setProperty("https.proxyPort", String.valueOf(port));
            }

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
                String newUrl = conn.getHeaderField("Location");
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                status = conn.getResponseCode();
            }

            if (status != HttpURLConnection.HTTP_OK) {
                Output.errorvalidate("HTTP-Status Code " + status + " empfangen.");
                Output.eventuell();
                Output.invalidinput();
                return !Allgemein.skip();
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

                if (displayName.contains(street.toLowerCase()) &&
                        displayName.contains(houseNumber.toLowerCase()) &&
                        displayName.contains(plz.toLowerCase()) &&
                        displayName.contains(place.toLowerCase()) &&
                        displayName.contains(bundesland.toLowerCase())) {
                    return true;
                } else {
                    Output.errorvalidate("Eventuell Fehler in Adresse!");
                    return Allgemein.skip();
                }
            } else {
                Output.errorvalidate("Adresse existiert eventuell nicht!");
                return Allgemein.skip();
            }

        } catch (ConnectException | SocketTimeoutException e) {
            Output.connection(e.getMessage());
            Output.eventuell();
            Output.invalidinput();
            return Allgemein.skip();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isProxyReachable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
