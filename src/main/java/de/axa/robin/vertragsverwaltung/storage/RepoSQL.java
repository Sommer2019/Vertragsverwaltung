package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import jakarta.json.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RepoSQL  { //ToDO: umbau auf echte DB, JBA, tests
    private final Logger logger = Logger.getLogger(RepoSQL.class.getName());
    @Setter
    @Autowired
    private Setup setup;

    private static final String INSERT_VERTRAG_SQL = "INSERT INTO vertrage (vsnr, abrechnungszeitraum_monatlich, preis, versicherungsbeginn, versicherungsablauf, antragsDatum, fahrzeug_kennzeichen, fahrzeug_hersteller, fahrzeug_typ, fahrzeug_hoechstgeschwindigkeit, fahrzeug_wagnisskennziffer, partner_vorname, partner_nachname, partner_geschlecht, partner_geburtsdatum, partner_land, partner_strasse, partner_hausnummer, partner_plz, partner_stadt, partner_bundesland) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_VERTRAG_SQL = "SELECT * FROM vertrage JOIN fahrzeug ON vertrage.fahrzeug_kennzeichen = fahrzeug.amtlichesKennzeichen JOIN partner ON vertrage.partner_vorname = partner.vorname";
    private static final String SELECT_FAKTOREN_SQL = "SELECT * FROM faktoren LIMIT 1"; // Assumes a single row of factors
    private static final String UPDATE_FAKTOREN_SQL = "UPDATE faktoren SET factor = ?, factorage = ?, factorspeed = ? WHERE id = 1"; // Assuming a single row for factors


    /**
     * Gets a database connection using the setup configuration.
     *
     * @return the database connection
     * @throws SQLException if a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(setup.getDb_url(), setup.getDb_user(), setup.getDb_pass());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error", e);
            throw e;
        }
    }

    /**
     * Saves a list of contracts to the database.
     *
     * @param vertrage the list of contracts to save
     */
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

    /**
     * Sets the parameters of a PreparedStatement for a contract.
     *
     * @param stmt the PreparedStatement
     * @param v the contract
     * @throws SQLException if a database access error occurs
     */
    private void setVertragParameters(PreparedStatement stmt, Vertrag v) throws SQLException {
        stmt.setInt(1, v.getVsnr());
        stmt.setBoolean(2, v.isMonatlich());
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
        stmt.setString(14, String.valueOf(v.getPartner().getGeschlecht().charAt(0)));
        stmt.setDate(15, Date.valueOf(v.getPartner().getGeburtsdatum()));
        stmt.setString(16, v.getPartner().getLand());
        stmt.setString(17, v.getPartner().getStrasse());
        stmt.setString(18, v.getPartner().getHausnummer());
        stmt.setString(19, v.getPartner().getPlz());
        stmt.setString(20, v.getPartner().getStadt());
        stmt.setString(21, v.getPartner().getBundesland());
    }

    /**
     * Loads a list of contracts from the database.
     *
     * @return the list of contracts
     */
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

    /**
     * Creates a contract from a ResultSet.
     *
     * @param rs the ResultSet
     * @return the contract
     * @throws SQLException if a database access error occurs
     */
    private Vertrag createVertragFromResultSet(ResultSet rs) throws SQLException {
        return new Vertrag(
                rs.getInt("vsnr"),
                rs.getBoolean("abrechnungszeitraum_monatlich"),
                rs.getDouble("preis"),
                rs.getDate("versicherungsbeginn").toLocalDate(),
                rs.getDate("versicherungsablauf").toLocalDate(),
                rs.getDate("antragsDatum").toLocalDate(),
                new Fahrzeug(
                        rs.getString("fahrzeug_ammtkennzeichen"),
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

    /**
     * Loads factors from the database.
     *
     * @return the JSON object representing the factors
     */
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

    /**
     * Saves factors to the database.
     *
     * @param factor the factor
     * @param factorage the age factor
     * @param factorspeed the speed factor
     */
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
