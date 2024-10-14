package de.axa.robin.vertragsverwaltung.storage.Checker;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddressValidator {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public static boolean validateAddress(String street, String houseNumber, String plz, String place, String bundesland) {
        try {
            // Build the query URL
            String query = street + " " + houseNumber + ", " + plz + " " + place + ", " + bundesland + ", Germany";
            String url = NOMINATIM_URL + query.replace(" ", "%20");

            // Send request to the Nominatim API
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Parse the response using javax.json
            JsonReader jsonReader = Json.createReader(new StringReader(content.toString()));
            JsonArray jsonArray = jsonReader.readArray();
            jsonReader.close();

            // If there are results, validate the first result
            if (!jsonArray.isEmpty()) {
                JsonObject address = jsonArray.getJsonObject(0);
                String displayName = address.getString("display_name").toLowerCase();

                // Simple validation check: whether all elements are in the result
                return displayName.contains(street.toLowerCase()) &&
                        displayName.contains(houseNumber.toLowerCase()) &&
                        displayName.contains(plz.toLowerCase()) &&
                        displayName.contains(place.toLowerCase()) &&
                        displayName.contains(bundesland.toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        String street = "Musterstraße";
        String houseNumber = "10";
        String plz = "10115";
        String place = "Berlin";
        String bundesland = "Berlin";

        boolean isValid = validateAddress(street, houseNumber, plz, place, bundesland);
        System.out.println("Is valid address: " + isValid);
    }
}
