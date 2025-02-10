package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepoSQLTest {

    // In-Memory-Datenbank-Parameter für H2
    private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    // Test-Setup-Implementierung für die DB-Verbindungsparameter
    private static Setup testSetup;

    // Instanz der zu testenden Klasse
    private RepoSQL repoSQL;

    @BeforeEach
    void setUp() {
        // Implementierung von Setup, die die H2-Datenbank verwendet
        testSetup = new Setup() {
            @Override
            public String getDb_url() {
                return DB_URL;
            }
            @Override
            public String getDb_user() {
                return DB_USER;
            }
            @Override
            public String getDb_pass() {
                return DB_PASS;
            }
            // Für diese Tests sind die anderen Methoden nicht relevant
            @Override public String getCheckURL() { return ""; }
            @Override public String getTestURL() { return ""; }
            @Override public String getProxy_host() { return ""; }
            @Override public int getProxy_port() { return 0; }
        };

        repoSQL = new RepoSQL();
        // Manuelle Injektion des Test-Setup-Objekts
        repoSQL.setSetup(testSetup);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        // Nach jedem Test werden die Tabellen (bzw. deren Inhalte) bereinigt.
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM vertrage");
            stmt.execute("DELETE FROM fahrzeug");
            stmt.execute("DELETE FROM partner");
        }
    }
    @Disabled("not implemented yet")
    @Test
    void testSpeichereUndLadeVertrage() throws SQLException {
        // Beispiel-Vertrag erstellen
        Partner partner = new Partner("Max", "Mustermann", 'M', LocalDate.of(1980, 1, 1),
                "Deutschland", "Musterstraße", "10", "12345", "Musterstadt", "Bayern");
        Fahrzeug fahrzeug = new Fahrzeug("ABC123", "VW", "Golf", 220, 42);
        Vertrag vertrag = new Vertrag(1, true, 1000.0,
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2021, 12, 1),
                fahrzeug, partner);

        List<Vertrag> vertrage = new ArrayList<>();
        vertrage.add(vertrag);

        // Damit der SELECT-Join funktioniert, müssen auch die zugehörigen Datensätze in den Tabellen
        // fahrzeug und partner vorhanden sein.
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmtFahrzeug = conn.prepareStatement(
                     "INSERT INTO fahrzeug (amtlichesKennzeichen, hersteller, typ, hoechstgeschwindigkeit, wagnisskennziffer) VALUES (?, ?, ?, ?, ?)");
             PreparedStatement stmtPartner = conn.prepareStatement(
                     "INSERT INTO partner (vorname, nachname, geschlecht, geburtsdatum, land, strasse, hausnummer, plz, stadt, bundesland) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            stmtFahrzeug.setString(1, fahrzeug.getAmtlichesKennzeichen());
            stmtFahrzeug.setString(2, fahrzeug.getHersteller());
            stmtFahrzeug.setString(3, fahrzeug.getTyp());
            stmtFahrzeug.setInt(4, fahrzeug.getHoechstgeschwindigkeit());
            stmtFahrzeug.setInt(5, fahrzeug.getWagnisskennziffer());
            stmtFahrzeug.executeUpdate();

            stmtPartner.setString(1, partner.getVorname());
            stmtPartner.setString(2, partner.getNachname());
            stmtPartner.setString(3, String.valueOf(partner.getGeschlecht().charAt(0)));
            stmtPartner.setDate(4, Date.valueOf(partner.getGeburtsdatum()));
            stmtPartner.setString(5, partner.getLand());
            stmtPartner.setString(6, partner.getStrasse());
            stmtPartner.setString(7, partner.getHausnummer());
            stmtPartner.setString(8, partner.getPlz());
            stmtPartner.setString(9, partner.getStadt());
            stmtPartner.setString(10, partner.getBundesland());
            stmtPartner.executeUpdate();
        }

        // Vertrag speichern
        repoSQL.speichereVertrage(vertrage);

        // Verträge laden
        List<Vertrag> loadedVertraege = repoSQL.ladeVertrage();

        assertNotNull(loadedVertraege, "Die geladene Vertragsliste sollte nicht null sein.");
        assertEquals(1, loadedVertraege.size(), "Es sollte genau ein Vertrag geladen werden.");

        Vertrag loadedVertrag = loadedVertraege.get(0);
        assertEquals(vertrag.getVsnr(), loadedVertrag.getVsnr(), "Die Vertragsnummern sollten übereinstimmen.");
        assertEquals(vertrag.isMonatlich(), loadedVertrag.isMonatlich(), "Der Abrechnungszeitraum sollte übereinstimmen.");
        assertEquals(vertrag.getPreis(), loadedVertrag.getPreis(), 0.001, "Der Preis sollte übereinstimmen.");
        assertEquals(vertrag.getVersicherungsbeginn(), loadedVertrag.getVersicherungsbeginn(), "Der Versicherungsbeginn sollte übereinstimmen.");
        assertEquals(vertrag.getVersicherungsablauf(), loadedVertrag.getVersicherungsablauf(), "Der Versicherungsablauf sollte übereinstimmen.");
        assertEquals(vertrag.getAntragsDatum(), loadedVertrag.getAntragsDatum(), "Das Antragsdatum sollte übereinstimmen.");
        // Weitere Assertions (z. B. für Fahrzeug und Partner) können ergänzt werden.
    }
    @Disabled("not implemented yet")
    @Test
    void testSpeichereUndLadeFaktoren() {
        // Neue Faktorwerte festlegen
        double factor = 2.5;
        double factorage = 3.5;
        double factorspeed = 4.5;

        repoSQL.speichereFaktoren(factor, factorage, factorspeed);

        // Faktoren laden
        JsonObject faktoren = repoSQL.ladeFaktoren();
        assertNotNull(faktoren, "Das geladene JSON-Objekt sollte nicht null sein.");
        assertEquals(factor, faktoren.getJsonNumber("factor").doubleValue(), 0.001, "Der 'factor'-Wert sollte übereinstimmen.");
        assertEquals(factorage, faktoren.getJsonNumber("factorage").doubleValue(), 0.001, "Der 'factorage'-Wert sollte übereinstimmen.");
        assertEquals(factorspeed, faktoren.getJsonNumber("factorspeed").doubleValue(), 0.001, "Der 'factorspeed'-Wert sollte übereinstimmen.");
    }
}
