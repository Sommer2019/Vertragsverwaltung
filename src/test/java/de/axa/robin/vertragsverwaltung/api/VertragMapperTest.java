package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VertragMapperTest {

    @Test
    void testToVertrag() {
        VertragDTO vertragDTO = new VertragDTO();
        // Set properties on vertragDTO as needed for the test

        Vertrag vertrag = VertragMapper.INSTANCE.toVertrag(vertragDTO);

        assertNotNull(vertrag);
        // Add more assertions to verify the mapping
    }
}