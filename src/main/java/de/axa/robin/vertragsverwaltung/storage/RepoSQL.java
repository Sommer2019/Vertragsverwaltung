package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.config.Setup;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import jakarta.json.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class RepoSQL  { //ToDO: umbau auf echte DB, JBA, tests
    private final Logger logger = LoggerFactory.getLogger(RepoSQL.class.getName());
    @Setter
    @Autowired
    private Setup setup;

    private static final String INSERT_VERTRAG_SQL = "INSERT INTO vertrage (vsnr, abrechnungszeitraum_monatlich, preis, versicherungsbeginn, versicherungsablauf, antragsDatum, fahrzeug_kennzeichen, fahrzeug_hersteller, fahrzeug_typ, fahrzeug_hoechstgeschwindigkeit, fahrzeug_wagnisskennziffer, partner_vorname, partner_nachname, partner_geschlecht, partner_geburtsdatum, partner_land, partner_strasse, partner_hausnummer, partner_plz, partner_stadt, partner_bundesland) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_VERTRAG_SQL = "SELECT * FROM vertrage JOIN fahrzeug ON vertrage.fahrzeug_kennzeichen = fahrzeug.amtlichesKennzeichen JOIN partner ON vertrage.partner_vorname = partner.vorname";
    private static final String SELECT_FAKTOREN_SQL = "SELECT * FROM faktoren LIMIT 1"; // Assumes a single row of factors
    private static final String UPDATE_FAKTOREN_SQL = "UPDATE faktoren SET factor = ?, factorage = ?, factorspeed = ? WHERE id = 1"; // Assuming a single row for factors

    private Connection getConnection() throws SQLException {
        try {
            logger.info("Attempting to establish a database connection");
            return DriverManager.getConnection(setup.getDb_url(), setup.getDb_user(), setup.getDb_pass());
        } catch (SQLException e) {
            logger.error("Database connection error", e);
            throw e;
        }
    }

    public void speichereVertrage(List<Vertrag> vertrage) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_VERTRAG_SQL)) {

            for (Vertrag v : vertrage) {
                setVertragParameters(stmt, v);
                stmt.addBatch();
                logger.info("Added contract to batch: " + v);
            }
            stmt.executeBatch();
            logger.info("Batch execution completed successfully");
        } catch (SQLException e) {
            logger.error("Error saving contracts to the database", e);
        }
    }

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
        logger.info("Set parameters for contract: " + v);
    }

    public List<Vertrag> ladeVertrage() {
        List<Vertrag> vertrage = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_VERTRAG_SQL)) {

            while (rs.next()) {
                Vertrag vertrag = createVertragFromResultSet(rs);
                vertrage.add(vertrag);
                logger.info("Loaded contract from database: " + vertrag);
            }
        } catch (SQLException e) {
            logger.error("Error loading contracts from the database", e);
        }
        return vertrage;
    }

    private Vertrag createVertragFromResultSet(ResultSet rs) throws SQLException {
        Vertrag vertrag = new Vertrag(
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
        logger.info("Created contract from ResultSet: " + vertrag);
        return vertrag;
    }

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
                logger.info("Loaded factors from database: " + jsonObject);
            }
        } catch (SQLException e) {
            logger.error("Error loading factors from the database", e);
        }
        return jsonObject;
    }

    public void speichereFaktoren(double factor, double factorage, double factorspeed) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_FAKTOREN_SQL)) {
            stmt.setDouble(1, factor);
            stmt.setDouble(2, factorage);
            stmt.setDouble(3, factorspeed);
            stmt.executeUpdate();
            logger.info("Saved factors to database: factor=" + factor + ", factorage=" + factorage + ", factorspeed=" + factorspeed);
        } catch (SQLException e) {
            logger.error("Error saving factors to the database", e);
        }
    }
}