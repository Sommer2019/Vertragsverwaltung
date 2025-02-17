package de.axa.robin.vertragsverwaltung.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axa.robin.preisverwaltung.model.PreisDTO;
import de.axa.robin.vertragsverwaltung.VertragsverwaltungApplication;
import de.axa.robin.vertragsverwaltung.mapper.PreisModelMapper;
import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.services.PreisModelService;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VertragsverwaltungApplication.class)
@AutoConfigureMockMvc
class PreisApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PreisModelService preisModelService;

    @MockitoBean
    private PreisModelMapper preisModelMapper;

    @MockitoBean
    private VertragsService vertragsService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Testet den GET-Endpunkt "/api/preisverwaltung/".
     * Es wird ein Preis-Objekt mit definierten Werten zurückgegeben, sodass die Ausgabe den erwarteten String enthält.
     */
    @Test
    @WithMockUser(username = "apiuser", roles = "API_USER")
    void testPreismodellGet() throws Exception {
        // Arrange
        Preis preis = new Preis();
        preis.setAge(1.5);
        preis.setSpeed(2.0);
        preis.setFaktor(3.0);
        Mockito.when(preisModelService.getPreismodell()).thenReturn(preis);

        String expectedResponse = "Preisberechnung: Formel: preis = (alter * " + preis.getAge() +
                " + hoechstGeschwindigkeit * " + preis.getSpeed() + ") * " + preis.getFaktor();

        // Act & Assert
        mockMvc.perform(get("/api/preisverwaltung/"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    /**
     * Testet den POST-Endpunkt "/api/preisverwaltung/".
     * Es wird ein PreisDTO als JSON-Body gesendet. Der Mapper wandelt diesen in ein Preis-Objekt um,
     * und der Service liefert einen simulierten Einnahmenwert zurück, der in der Antwort validiert wird.
     */
    @Test
    @WithMockUser(username = "apiuser", roles = "API_USER")
    void testPreismodellPost() throws Exception {
        // Arrange
        PreisDTO preisDTO = new PreisDTO();
        // Optional: Felder von preisDTO setzen, sofern erforderlich, z. B.:
        // preisDTO.setAge(1.5);
        // preisDTO.setSpeed(2.0);
        // preisDTO.setFaktor(3.0);

        Preis preis = new Preis();
        preis.setAge(1.5);
        preis.setSpeed(2.0);
        preis.setFaktor(3.0);

        Mockito.when(preisModelMapper.toPreis(ArgumentMatchers.any(PreisDTO.class))).thenReturn(preis);
        Mockito.when(vertragsService.getVertrage()).thenReturn(java.util.Collections.emptyList());
        double updatedSum = 1000.0;
        Mockito.when(preisModelService.updatePreisAndModell(preis, false, java.util.Collections.emptyList()))
                .thenReturn(BigDecimal.valueOf(updatedSum));

        String expectedResponse = "Einnahmensumme:" + updatedSum + "€";

        // Act & Assert
        mockMvc.perform(post("/api/preisverwaltung/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preisDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
}
