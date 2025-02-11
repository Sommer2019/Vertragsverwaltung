package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.model.FahrzeugDTO;
import de.axa.robin.vertragsverwaltung.model.PartnerDTO;
import de.axa.robin.vertragsverwaltung.model.PreisDTO;
import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Preis;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.editor.CreateData;
import de.axa.robin.vertragsverwaltung.storage.editor.EditPreis;
import de.axa.robin.vertragsverwaltung.storage.editor.EditVertrag;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class VertragControllerTest {

    @Mock
    private InputValidator inputValidator;

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private EditVertrag editVertrag;

    @Mock
    private CreateData createData;

    @Mock
    private Mapper mapper;

    @Mock
    private Repository repository;

    @Mock
    private EditPreis editPreis;

    @InjectMocks
    private VertragController vertragController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllVertrage() {
        Vertrag vertrag1 = new Vertrag();
        Vertrag vertrag2 = new Vertrag();
        List<Vertrag> vertrage = Arrays.asList(vertrag1, vertrag2);

        when(vertragsverwaltung.getVertrage()).thenReturn(vertrage);

        ResponseEntity<List<Vertrag>> response = vertragController.getAllVertrage();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetVertragById() {
        Vertrag vertrag = new Vertrag();
        when(vertragsverwaltung.getVertrag(1)).thenReturn(vertrag);

        ResponseEntity<Vertrag> response = vertragController.getVertragById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(vertrag, response.getBody());
    }

    @Test
    void testCreateVertrag() {
        VertragDTO vertragDTO = new VertragDTO();
        vertragDTO.setPartner(new PartnerDTO());
        vertragDTO.setFahrzeug(new FahrzeugDTO());
        vertragDTO.setMonatlich(true);
        vertragDTO.getPartner().setGeburtsdatum(LocalDate.now().minusYears(18));
        vertragDTO.getFahrzeug().setHoechstgeschwindigkeit(200);
        Vertrag vertrag = new Vertrag();
        when(mapper.toVertrag(any(VertragDTO.class))).thenReturn(vertrag);
        when(createData.createvsnr()).thenReturn(12345);
        when(createData.createPreis(any(Boolean.class), any(LocalDate.class), any(Integer.class))).thenReturn(100.0);
        when(inputValidator.validateVertrag(any(Vertrag.class), any(BindingResult.class))).thenReturn(false);
        when(inputValidator.flexcheck(any(Vertrag.class))).thenReturn(false);
        when(vertragsverwaltung.vertragAnlegen(any(Vertrag.class))).thenReturn(vertrag);

        ResponseEntity<Vertrag> response = vertragController.createVertrag(vertragDTO, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(vertrag, response.getBody());
    }

    @Test
    void testUpdateVertrag() {
        VertragDTO vertragDTO = new VertragDTO();
        vertragDTO.setPartner(new PartnerDTO());
        vertragDTO.setFahrzeug(new FahrzeugDTO());
        vertragDTO.setMonatlich(true);
        vertragDTO.getPartner().setGeburtsdatum(LocalDate.now().minusYears(18));
        vertragDTO.getFahrzeug().setHoechstgeschwindigkeit(200);
        Vertrag vertrag = new Vertrag();
        vertrag.setPartner(new Partner());
        vertrag.setFahrzeug(new Fahrzeug());
        when(mapper.toVertrag(any(VertragDTO.class))).thenReturn(vertrag);
        when(editVertrag.editVertrag(any(Vertrag.class), anyInt())).thenReturn(vertrag);
        when(createData.createPreis(any(Boolean.class), any(LocalDate.class), any(Integer.class))).thenReturn(100.0);
        when(vertragsverwaltung.vertragLoeschen(anyInt())).thenReturn(true);
        when(vertragsverwaltung.vertragAnlegen(any(Vertrag.class))).thenReturn(vertrag);

        ResponseEntity<Vertrag> response = vertragController.updateVertrag(1, vertragDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(vertrag, response.getBody());
    }

    @Test
    void testDeleteVertrag() {
        when(vertragsverwaltung.vertragLoeschen(anyInt())).thenReturn(true);

        ResponseEntity<Void> response = vertragController.deleteVertrag(1);

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testGetPreismodell() {
        JsonObject jsonObject = Mockito.mock(JsonObject.class);
        when(repository.ladeFaktoren()).thenReturn(jsonObject);
        when(jsonObject.getJsonNumber("factor")).thenReturn(Json.createValue(1.5));
        when(jsonObject.getJsonNumber("factorage")).thenReturn(Json.createValue(0.2));
        when(jsonObject.getJsonNumber("factorspeed")).thenReturn(Json.createValue(0.1));

        ResponseEntity<String> response = vertragController.getPreismodell();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Preisberechnung: Formel: preis = (alter * 0.2 + hoechstGeschwindigkeit * 0.1) * 1.5", response.getBody());
    }

    @Test
    void testSetPreismodell() {
        PreisDTO preisDTO = new PreisDTO();
        Preis preis = new Preis();
        when(mapper.toPreis(any(PreisDTO.class))).thenReturn(preis);
        when(editPreis.recalcPrice(any(Double.class), any(Double.class), any(Double.class), any(List.class))).thenReturn(BigDecimal.valueOf(100.0));

        ResponseEntity<String> response = vertragController.setPreismodell(preisDTO);

        assertEquals(200, response.getStatusCodeValue());
    }
}