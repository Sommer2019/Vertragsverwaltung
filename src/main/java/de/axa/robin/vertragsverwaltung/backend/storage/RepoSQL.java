package de.axa.robin.vertragsverwaltung.backend.storage;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.backend.modell.Partner;
import de.axa.robin.vertragsverwaltung.backend.modell.Vertrag;
import jakarta.json.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepoSQL {
    private final Logger logger = Logger.getLogger(RepoSQL.class.getName());
    private final Setup setup;

    private static final String INSERT_VERTRAG_SQL = "INSERT INTO vertrage (vsnr, abrechnungszeitraum_monatlich, preis, versicherungsbeginn, versicherungsablauf, antragsDatum, fahrzeug_kennzeichen, fahrzeug_hersteller, fahrzeug_typ, fahrzeug_hoechstgeschwindigkeit, fahrzeug_wagnisskennziffer, partner_vorname, partner_nachname, partner_geschlecht, partner_geburtsdatum, partner_land, partner_strasse, partner_hausnummer, partner_plz, partner_stadt, partner_bundesland) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_VERTRAG_SQL = "SELECT * FROM vertrage JOIN fahrzeug ON vertrage.fahrzeug_kennzeichen = fahrzeug.amtlichesKennzeichen JOIN partner ON vertrage.partner_vorname = partner.vorname";
    private static final String SELECT_FAKTOREN_SQL = "SELECT * FROM faktoren LIMIT 1"; // Assumes a single row of factors
    private static final String UPDATE_FAKTOREN_SQL = "UPDATE faktoren SET factor = ?, factorage = ?, factorspeed = ? WHERE id = 1"; // Assuming a single row for factors

    public RepoSQL(Setup setup) {
        this.setup = setup;
    }

    // Get a database connection
    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(setup.getDb_url(), setup.getDb_user(), setup.getDb_pass());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error", e);
            throw e;
        }
    }

    // Method to save contracts to the database
    public void speichereVertrage(List<Vertrag> vertrage) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_VERTRAG_SQL)) {

            for (Vertrag v : vertrage) {
                setVertragParameters(stmt, v);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving contracts to the database", e);
        }
    }

    private void setVertragParameters(PreparedStatement stmt, Vertrag v) throws SQLException {
        stmt.setInt(1, v.getVsnr());
        stmt.setBoolean(2, v.getMonatlich());
        stmt.setDouble(3, v.getPreis());
        stmt.setDate(4, Date.valueOf(v.getVersicherungsbeginn()));
        stmt.setDate(5, Date.valueOf(v.getVersicherungsablauf()));
        stmt.setDate(6, Date.valueOf(v.getAntragsDatum()));
        stmt.setString(7, v.getFahrzeug().getAmtlichesKennzeichen());
        stmt.setString(8, v.getFahrzeug().getHersteller());
        stmt.setString(9, v.getFahrzeug().getTyp());
        stmt.setInt(10, v.getFahrzeug().getHoechstgeschwindigkeit());
        stmt.setInt(11, v.getFahrzeug().getWagnisskennziffer());
        stmt.setString(12, v.getPartner().getVorname());
        stmt.setString(13, v.getPartner().getNachname());
        stmt.setString(14, String.valueOf(v.getGender()));
        stmt.setDate(15, Date.valueOf(v.getPartner().getGeburtsdatum()));
        stmt.setString(16, v.getPartner().getLand());
        stmt.setString(17, v.getPartner().getStrasse());
        stmt.setString(18, v.getPartner().getHausnummer());
        stmt.setString(19, v.getPartner().getPlz());
        stmt.setString(20, v.getPartner().getStadt());
        stmt.setString(21, v.getPartner().getBundesland());
    }

    // Method to load contracts from the database
    public List<Vertrag> ladeVertrage() {
        List<Vertrag> vertrage = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_VERTRAG_SQL)) {

            while (rs.next()) {
                vertrage.add(createVertragFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading contracts from the database", e);
        }
        return vertrage;
    }

    private Vertrag createVertragFromResultSet(ResultSet rs) throws SQLException {
        return new Vertrag(
                rs.getInt("vsnr"),
                rs.getBoolean("abrechnungszeitraum_monatlich"),
                rs.getDouble("preis"),
                rs.getDate("versicherungsbeginn").toLocalDate(),
                rs.getDate("versicherungsablauf").toLocalDate(),
                rs.getDate("antragsDatum").toLocalDate(),
                new Fahrzeug(
                        rs.getString("fahrzeug_kennzeichen"),
                        rs.getString("fahrzeug_hersteller"),
                        rs.getString("fahrzeug_typ"),
                        rs.getInt("fahrzeug_hoechstgeschwindigkeit"),
                        rs.getInt("fahrzeug_wagnisskennziffer")
                ),
                new Partner(
                        rs.getString("partner_vorname"),
                        rs.getString("partner_nachname"),
                        rs.getString("partner_geschlecht").charAt(0),
                        rs.getDate("partner_geburtsdatum").toLocalDate(),
                        rs.getString("partner_land"),
                        rs.getString("partner_strasse"),
                        rs.getString("partner_hausnummer"),
                        rs.getString("partner_plz"),
                        rs.getString("partner_stadt"),
                        rs.getString("partner_bundesland")
                )
        );
    }

    // Method to load factors from the database
    public JsonObject ladeFaktoren() {
        JsonObject jsonObject = null;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_FAKTOREN_SQL)) {

            if (rs.next()) {
                jsonObject = Json.createObjectBuilder()
                        .add("factor", rs.getDouble("factor"))
                        .add("factorage", rs.getDouble("factorage"))
                        .add("factorspeed", rs.getDouble("factorspeed"))
                        .build();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading factors from the database", e);
        }
        return jsonObject;
    }

    // Method to save factors to the database
    public void speichereFaktoren(double factor, double factorage, double factorspeed) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_FAKTOREN_SQL)) {
            stmt.setDouble(1, factor);
            stmt.setDouble(2, factorage);
            stmt.setDouble(3, factorspeed);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving factors to the database", e);
        }
    }
}
