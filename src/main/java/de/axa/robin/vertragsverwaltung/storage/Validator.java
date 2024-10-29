package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.user_interaction.Input;
import de.axa.robin.vertragsverwaltung.user_interaction.Output;

import jakarta.json.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Validator {
    // Klassen einlesen
    private final Output output = new Output();
    private final Input input = new Input();
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public boolean validateAddress(String street, String houseNumber, String plz, String place, String bundesland, String land) {
        try {
            String query = URLEncoder.encode(street + " " + houseNumber + ", " + plz + " " + place + ", " + bundesland + ", " + land, StandardCharsets.UTF_8);
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

            // Check internet connection
            if (!isInternetAvailable()) {
                if (isProxyReachable(host, port)) {
                    output.errorvalidate("Proxy ist erreichbar, aber keine Internetverbindung.");
                } else {
                    output.errorvalidate("Keine Internetverbindung und Proxy ist nicht erreichbar.");
                }
                return input.getChar(null, "") != 'n';
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
                output.errorvalidate("HTTP-Status Code " + status + " empfangen.");
                output.eventuell();
                output.invalidinput();
                return input.getChar(null, "") != 'n';
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

                if (displayName.contains(street.toLowerCase()+",") &&
                        displayName.contains(houseNumber.toLowerCase()+",") &&
                        displayName.contains(plz.toLowerCase()+",") &&
                        displayName.contains(place.toLowerCase()+",") &&
                        displayName.contains(bundesland.toLowerCase()+",") &&
                        displayName.contains(land.toLowerCase())) {
                    return true;
                } else {
                    output.errorvalidate("Eventuell Fehler in Adresse!");
                    return input.getChar(null, "") != 'n';
                }
            } else {
                output.errorvalidate("Adresse existiert eventuell nicht!");
                return input.getChar(null, "") != 'n';
            }

        } catch (ConnectException | SocketTimeoutException e) {
            output.connection(e.getMessage());
            e.printStackTrace();
            output.eventuell();
            output.invalidinput();
            return input.getChar(null, "") != 'n';
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isProxyReachable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isInternetAvailable() {
        try {
            HttpURLConnection urlConn = (HttpURLConnection) new URL("http://www.google.com").openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(5000); // 5 seconds
            urlConn.connect();
            return urlConn.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }
    public boolean string(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return input.isEmpty();
    }
    public boolean isStringInJsonFile(String searchString) {
        String filePath = "src/main/resources/brands.json";
        try (InputStream fis = new FileInputStream(filePath);
             JsonReader jsonReader = Json.createReader(fis)) {
            JsonObject jsonObject = jsonReader.readObject();
            return jsonObject.toString().contains(searchString);
        } catch (FileNotFoundException e) {
            output.errorvalidate("File not found: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
