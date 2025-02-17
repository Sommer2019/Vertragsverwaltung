package de.axa.robin.vertragsverwaltung.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = Setup.class)
@EnableConfigurationProperties(Setup.class)
@TestPropertySource(properties = {
        "input.setup.json_repositoryPath=/repository",
        "input.setup.json_preisPath=/preis",
        "input.setup.json_brandsPath=/brands",
        "input.setup.testURL=http://example.com/test",
        "input.setup.checkURL=http://example.com/check",
        "input.setup.proxy_host=127.0.0.1",
        "input.setup.proxy_port=8080",
        "input.setup.db_url=jdbc:h2:mem:testdb",
        "input.setup.db_user=sa",
        "input.setup.db_pass=pass"
})
class SetupTest {

    @Autowired
    private Setup setup;

    @Test
    void testPropertiesBinding() {
        assertNotNull(setup, "Das Setup-Bean sollte erstellt werden.");
        assertEquals("/repository", setup.getJson_repositoryPath(), "json_repositoryPath sollte /repository sein.");
        assertEquals("/preis", setup.getJson_preisPath(), "json_preisPath sollte /preis sein.");
        assertEquals("/brands", setup.getJson_brandsPath(), "json_brandsPath sollte /brands sein.");
        assertEquals("http://example.com/test", setup.getTestURL(), "testURL sollte http://example.com/test sein.");
        assertEquals("http://example.com/check", setup.getCheckURL(), "checkURL sollte http://example.com/check sein.");
        assertEquals("127.0.0.1", setup.getProxy_host(), "proxy_host sollte 127.0.0.1 sein.");
        assertEquals(8080, setup.getProxy_port(), "proxy_port sollte 8080 sein.");
        assertEquals("jdbc:h2:mem:testdb", setup.getDb_url(), "db_url sollte jdbc:h2:mem:testdb sein.");
        assertEquals("sa", setup.getDb_user(), "db_user sollte sa sein.");
        assertEquals("pass", setup.getDb_pass(), "db_pass sollte pass sein.");
    }
}
