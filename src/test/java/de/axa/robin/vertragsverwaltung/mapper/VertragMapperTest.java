package de.axa.robin.vertragsverwaltung.mapper;

import de.axa.robin.vertragsverwaltung.model.*;
import de.axa.robin.vertragsverwaltung.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class VertragMapperTest {

    private VertragMapper vertragMapper;

    @BeforeEach
    public void setUp() {
        vertragMapper = Mappers.getMapper(VertragMapper.class);
    }

    @Test
    public void testToVertrag() {
        AntragDTO antragDTO = new AntragDTO();
        // Set properties on vertragDTO as needed for the test
        antragDTO.setMonatlich(true);
        antragDTO.setVersicherungsbeginn(LocalDate.of(2023,1,1));
        antragDTO.setVersicherungsablauf(LocalDate.of(2023,12,31));
        antragDTO.setAntragsDatum(LocalDate.of(2022,12,1));
        antragDTO.setFahrzeug(new FahrzeugDTO());
        antragDTO.setPartner(new PartnerDTO());

        Vertrag vertrag = vertragMapper.toVertrag(antragDTO);

        assertNotNull(vertrag);
        assertEquals(antragDTO.getMonatlich(), vertrag.isMonatlich());
        assertEquals(antragDTO.getVersicherungsbeginn(), vertrag.getVersicherungsbeginn());
        assertEquals(antragDTO.getVersicherungsablauf(), vertrag.getVersicherungsablauf());
        assertEquals(antragDTO.getAntragsDatum(), vertrag.getAntragsDatum());
        assertNotNull(vertrag.getFahrzeug());
        assertNotNull(vertrag.getPartner());
    }

    @Test
    public void testToPartner() {
        PartnerDTO partnerDTO = new PartnerDTO();
        // Set properties on partnerDTO as needed for the test
        partnerDTO.setVorname("John");
        partnerDTO.setNachname("Doe");

        Partner partner = vertragMapper.toPartner(partnerDTO);

        assertNotNull(partner);
        assertEquals(partnerDTO.getVorname(), partner.getVorname());
        assertEquals(partnerDTO.getNachname(), partner.getNachname());
    }

    @Test
    public void testToFahrzeug() {
        FahrzeugDTO fahrzeugDTO = new FahrzeugDTO();
        // Set properties on fahrzeugDTO as needed for the test
        fahrzeugDTO.setAmtlichesKennzeichen("ABC123");
        fahrzeugDTO.setHersteller("BMW");

        Fahrzeug fahrzeug = vertragMapper.toFahrzeug(fahrzeugDTO);

        assertNotNull(fahrzeug);
        assertEquals(fahrzeugDTO.getAmtlichesKennzeichen(), fahrzeug.getAmtlichesKennzeichen());
        assertEquals(fahrzeugDTO.getHersteller(), fahrzeug.getHersteller());
    }

    @Test
    public void testToVertragApi() {
        Vertrag vertrag = new Vertrag();
        // Set properties on vertrag as needed for the test
        vertrag.setVsnr(123456789);
        vertrag.setPreis(100.0);
        vertrag.setMonatlich(true);
        vertrag.setVersicherungsbeginn(LocalDate.of(2023, 1, 1));
        vertrag.setVersicherungsablauf(LocalDate.of(2023, 12, 31));
        vertrag.setAntragsDatum(LocalDate.of(2022, 12, 1));
        vertrag.setFahrzeug(new Fahrzeug());
        vertrag.setPartner(new Partner());

        VertragDTO vertragDTO = vertragMapper.toVertragDTO(vertrag);

        assertNotNull(vertragDTO);
        assertEquals(vertrag.getVsnr(), vertragDTO.getVsnr());
        assertEquals(vertrag.getPreis(), vertragDTO.getPreis());
        assertEquals(vertrag.isMonatlich(), vertragDTO.getMonatlich());
        assertEquals(vertrag.getVersicherungsbeginn(), vertragDTO.getVersicherungsbeginn());
        assertEquals(vertrag.getVersicherungsablauf(), vertragDTO.getVersicherungsablauf());
        assertEquals(vertrag.getAntragsDatum(), vertragDTO.getAntragsDatum());
        assertNotNull(vertragDTO.getFahrzeug());
        assertNotNull(vertragDTO.getPartner());
    }
}