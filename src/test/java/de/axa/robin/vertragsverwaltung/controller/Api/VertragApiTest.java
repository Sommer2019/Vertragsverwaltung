package de.axa.robin.vertragsverwaltung.controller.Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axa.robin.vertragsverwaltung.VertragsverwaltungApplication;
import de.axa.robin.vertragsverwaltung.mapper.VertragMapper;
import de.axa.robin.vertragsverwaltung.model.AntragDTO;
import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VertragsverwaltungApplication.class)
@AutoConfigureMockMvc
public class VertragApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VertragsService vertragsService;

    @MockitoBean
    private VertragMapper vertragMapper;

    @MockitoBean
    private PreisModelService preisModelService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Testet den GET-Endpunkt "/" zum Abrufen aller Verträge.
     */
    @Test
    @WithMockUser(username = "apiuser", roles = "API_USER")
    public void testRootGet() throws Exception {
        // Erzeuge zwei Dummy-Vertrag-Objekte und ihre gemappten API-Repräsentationen
        Vertrag vertrag1 = new Vertrag();
        Vertrag vertrag2 = new Vertrag();
        VertragDTO vertragDTO1 = new VertragDTO();
        VertragDTO vertragDTO2 = new VertragDTO();

        List<Vertrag> vertragsList = Arrays.asList(vertrag1, vertrag2);
        List<VertragDTO> vertragDTOList = Arrays.asList(vertragDTO1, vertragDTO2);

        given(vertragsService.getVertrage()).willReturn(vertragsList);
        Mockito.when(vertragMapper.toVertragDTO(vertrag1)).thenReturn(vertragDTO1);
        Mockito.when(vertragMapper.toVertragDTO(vertrag2)).thenReturn(vertragDTO2);

        mockMvc.perform(get("/api/vertragsverwaltung/"))
                .andExpect(status().isOk())
                // Es wird geprüft, ob ein JSON-Array mit mindestens zwei Elementen zurückkommt.
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Testet den GET-Endpunkt "/vertrage/{id}" zum Abrufen eines einzelnen Vertrags.
     */
    @Test
    @WithMockUser(username = "apiuser", roles = "API_USER")
    public void testVertrageIdGet() throws Exception {
        int id = 123;
        Vertrag vertrag = new Vertrag();
        VertragDTO vertragDTO = new VertragDTO();
        // Definiere das Verhalten der Mocks
        given(vertragsService.getVertrag(id)).willReturn(vertrag);
        Mockito.when(vertragMapper.toVertragDTO(vertrag)).thenReturn(vertragDTO);

        mockMvc.perform(get("/api/vertragsverwaltung/vertrage/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(vertragDTO)));
    }

    // Test für den PUT-Endpunkt "/api/vertragsverwaltung/"
    @Test
    @WithMockUser(username = "apiuser", roles = "API_USER")
    public void testRootPut() throws Exception {
        AntragDTO antragDTO = new AntragDTO();
        // Erzeuge Dummy-Objekte für den erstellten Vertrag
        Vertrag vertrag = new Vertrag();
        VertragDTO vertragDTO = new VertragDTO();

        Mockito.when(vertragMapper.toVertrag(ArgumentMatchers.any(AntragDTO.class))).thenReturn(vertrag);
        // Verwende thenReturn() statt doNothing(), da vertragAnlegen() einen Vertrag zurückgibt.
        Mockito.when(vertragsService.vertragAnlegen(
                        ArgumentMatchers.eq(vertrag),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.isNull()))
                .thenReturn(vertrag);
        Mockito.when(vertragMapper.toVertragDTO(vertrag)).thenReturn(vertragDTO);

        mockMvc.perform(put("/api/vertragsverwaltung/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vertragDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(vertragDTO)));
    }

    /**
     * Testet den POST-Endpunkt "/vertrage/{id}" zum Aktualisieren eines bestehenden Vertrags.
     */
    @Test
    @WithMockUser(username = "apiuser", roles = "API_USER")
    public void testVertrageIdPost() throws Exception {
        int id = 123;
        AntragDTO antragDTO = new AntragDTO();
        Vertrag vertrag = new Vertrag();
        Vertrag updatedVertrag = new Vertrag();
        VertragDTO vertragDTO = new VertragDTO();

        Mockito.when(vertragMapper.toVertrag(ArgumentMatchers.any(AntragDTO.class))).thenReturn(vertrag);
        Mockito.when(vertragsService.vertragBearbeiten(
                        ArgumentMatchers.eq(vertrag),
                        ArgumentMatchers.eq(id),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.isNull()))
                .thenReturn(updatedVertrag);
        Mockito.when(vertragMapper.toVertragDTO(updatedVertrag)).thenReturn(vertragDTO);

        mockMvc.perform(post("/api/vertragsverwaltung/vertrage/{id}", id)
                        .param("id", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(antragDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(vertragDTO)));
    }

    /**
     * Testet den DELETE-Endpunkt "/api/vertragsverwaltung/vertrage/id" zum Löschen eines Vertrags.
     * Dabei wird sichergestellt, dass der Service-Aufruf mit den korrekten Parametern erfolgt.
     */
    @Test
    @WithMockUser(username = "apiuser", roles = "API_USER")
    public void testVertrageIdDelete() throws Exception {
        int id = 123;
        Vertrag vertrag = new Vertrag();
        VertragDTO vertragDTO = new VertragDTO();

        given(vertragsService.getVertrag(id)).willReturn(vertrag);
        Mockito.when(vertragMapper.toVertragDTO(vertrag)).thenReturn(vertragDTO);
        Mockito.doNothing().when(vertragsService).vertragLoeschen(ArgumentMatchers.eq(id), ArgumentMatchers.anyList());

        mockMvc.perform(delete("/api/vertragsverwaltung/vertrage/{id}", id)
                        .param("id", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(vertragDTO)));

        Mockito.verify(vertragsService).vertragLoeschen(ArgumentMatchers.eq(id), ArgumentMatchers.anyList());
    }
}
