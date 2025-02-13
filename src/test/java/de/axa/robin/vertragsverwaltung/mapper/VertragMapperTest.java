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
        VertragDTO vertragDTO = new VertragDTO();
        // Set properties on vertragDTO as needed for the test
        vertragDTO.setMonatlich(true);
        vertragDTO.setVersicherungsbeginn(LocalDate.of(2023,1,1));
        vertragDTO.setVersicherungsablauf(LocalDate.of(2023,12,31));
        vertragDTO.setAntragsDatum(LocalDate.of(2022,12,1));
        vertragDTO.setFahrzeug(new FahrzeugDTO());
        vertragDTO.setPartner(new PartnerDTO());

        Vertrag vertrag = vertragMapper.toVertrag(vertragDTO);

        assertNotNull(vertrag);
        assertEquals(vertragDTO.getMonatlich(), vertrag.isMonatlich());
        assertEquals(vertragDTO.getVersicherungsbeginn(), vertrag.getVersicherungsbeginn());
        assertEquals(vertragDTO.getVersicherungsablauf(), vertrag.getVersicherungsablauf());
        assertEquals(vertragDTO.getAntragsDatum(), vertrag.getAntragsDatum());
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

        VertragApi vertragApi = vertragMapper.toVertragApi(vertrag);

        assertNotNull(vertragApi);
        assertEquals(vertrag.getVsnr(), vertragApi.getVsnr());
        assertEquals(vertrag.getPreis(), vertragApi.getPreis());
        assertEquals(vertrag.isMonatlich(), vertragApi.getMonatlich());
        assertEquals(vertrag.getVersicherungsbeginn(), vertragApi.getVersicherungsbeginn());
        assertEquals(vertrag.getVersicherungsablauf(), vertragApi.getVersicherungsablauf());
        assertEquals(vertrag.getAntragsDatum(), vertragApi.getAntragsDatum());
        assertNotNull(vertragApi.getFahrzeug());
        assertNotNull(vertragApi.getPartner());
    }
}