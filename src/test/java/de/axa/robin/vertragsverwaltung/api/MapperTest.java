package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.model.*;
import de.axa.robin.vertragsverwaltung.modell.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTest {

    private Mapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(Mapper.class);
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

        Vertrag vertrag = mapper.toVertrag(vertragDTO);

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

        Partner partner = mapper.toPartner(partnerDTO);

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

        Fahrzeug fahrzeug = mapper.toFahrzeug(fahrzeugDTO);

        assertNotNull(fahrzeug);
        assertEquals(fahrzeugDTO.getAmtlichesKennzeichen(), fahrzeug.getAmtlichesKennzeichen());
        assertEquals(fahrzeugDTO.getHersteller(), fahrzeug.getHersteller());
    }

    @Test
    public void testToPreis() {
        PreisDTO preisDTO = new PreisDTO();
        // Set properties on preisDTO as needed for the test
        preisDTO.setSpeed(0.1);
        preisDTO.setAge(0.5);

        Preis preis = mapper.toPreis(preisDTO);

        assertNotNull(preis);
        assertEquals(preisDTO.getSpeed(), preis.getSpeed());
        assertEquals(preisDTO.getAge(), preis.getAge());
    }
}