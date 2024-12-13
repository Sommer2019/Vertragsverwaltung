package de.axa.robin.vertragsverwaltung.backend.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetupTest {

    @Test
    void testConstructorAndGetters() {
        Setup setup = new Setup();

        assertEquals("src/main/resources/static/json/vertrage.json", setup.getRepositoryPath());
        assertEquals("src/main/resources/static/json/preiscalc.json", setup.getPreisPath());
        assertEquals("src/main/resources/static/json/brands.json", setup.getBrandsPath());
        assertEquals("https://www.google.com", setup.getTestURL());
        assertEquals("https://nominatim.openstreetmap.org/search?format=json&q=", setup.getCheckURL());
        assertEquals("localhost", setup.getHost());
        assertEquals(3128, setup.getPort());
    }
}