package de.axa.robin.vertragsverwaltung.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testStaticResourceMapping() throws Exception {
        // Hinweis: Damit dieser Test erfolgreich ist, muss im Verzeichnis
        // src/main/resources/static eine Datei namens "favicon.ico" vorhanden sein.
        mockMvc.perform(get("/static/favicon.ico"))
                .andExpect(status().isOk());
    }
}
