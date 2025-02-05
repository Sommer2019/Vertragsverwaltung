package de.axa.robin.vertragsverwaltung.backend.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetupTest {

    @Test
    void testConstructorAndGetters() {
        Setup setup = new Setup();

        assertEquals("src/main/resources/static/json/vertrage.json", setup.getJson_repositoryPath());
        assertEquals("src/main/resources/static/json/preiscalc.json", setup.getJson_preisPath());
        assertEquals("src/main/resources/static/json/brands.json", setup.getJson_brandsPath());
        assertEquals("https://www.google.com", setup.getTestURL());
        assertEquals("https://nominatim.openstreetmap.org/search?format=json&q=", setup.getCheckURL());
        assertEquals("localhost", setup.getProxy_host());
        assertEquals(3128, setup.getProxy_port());
    }
}