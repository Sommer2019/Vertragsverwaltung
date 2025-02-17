package de.axa.robin.vertragsverwaltung.mapper;

import de.axa.robin.preisverwaltung.model.PreisDTO;
import de.axa.robin.vertragsverwaltung.models.Preis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class PreisModelMapperTest {

    private PreisModelMapper preisModelMapper;

    @BeforeEach
    void setUp() {
        // Initialisiert den Mapper über die MapStruct-Factory
        preisModelMapper = Mappers.getMapper(PreisModelMapper.class);
    }

    @Test
    void testToPreis() {
        // Arrange: Erstelle ein PreisDTO mit Testdaten
        PreisDTO preisDTO = new PreisDTO();
        preisDTO.setAge(1.5);
        preisDTO.setSpeed(2.0);
        preisDTO.setFaktor(3.0);

        // Act: Mapping durchführen
        Preis preis = preisModelMapper.toPreis(preisDTO);

        // Assert: Überprüfe, ob die Felder korrekt gemappt wurden
        assertNotNull(preis, "Das gemappte Preis-Objekt sollte nicht null sein.");
        assertEquals(preisDTO.getAge(), preis.getAge(), "Das Alter muss übereinstimmen.");
        assertEquals(preisDTO.getSpeed(), preis.getSpeed(), "Die Geschwindigkeit muss übereinstimmen.");
        assertEquals(preisDTO.getFaktor(), preis.getFaktor(), "Der Faktor muss übereinstimmen.");
    }
}
