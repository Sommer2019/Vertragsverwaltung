package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.editor.CreateData;
import de.axa.robin.vertragsverwaltung.storage.editor.EditVertrag;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
class VertragControllerTest {

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private InputValidator inputValidator;

    @Mock
    private CreateData createData;

    @Mock
    private EditVertrag editVertrag;

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

    @Disabled //ToDo fix
    @Test
    void testCreateVertrag() {
        VertragDTO vertragDTO = new VertragDTO();
        Vertrag vertrag = new Vertrag();
        vertrag.setVersicherungsbeginn(LocalDate.now().plusDays(1));
        when(inputValidator.validateVertrag(any(Vertrag.class), any(BindingResult.class))).thenReturn(false);
        when(vertragsverwaltung.kennzeichenExistiert(any(String.class))).thenReturn(false);
        when(vertragsverwaltung.vertragExistiert(anyInt())).thenReturn(false);
        when(createData.createvsnr()).thenReturn(12345);
        when(createData.createPreis(any(Boolean.class), any(LocalDate.class), any(Integer.class))).thenReturn(100.0);
        when(vertragsverwaltung.vertragAnlegen(any(Vertrag.class))).thenReturn(vertrag);

        ResponseEntity<Vertrag> response = vertragController.createVertrag(vertragDTO, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(vertrag, response.getBody());
    }

    @Disabled //ToDo fix
    @Test
    void testUpdateVertrag() {
        VertragDTO vertragDTO = new VertragDTO();
        Vertrag vertrag = new Vertrag();
        when(editVertrag.editVertrag(any(Vertrag.class), any(Integer.class))).thenReturn(vertrag);
        when(createData.createPreis(any(Boolean.class), any(LocalDate.class), any(Integer.class))).thenReturn(100.0);
        when(vertragsverwaltung.vertragLoeschen(1)).thenReturn(true);
        when(vertragsverwaltung.vertragAnlegen(any(Vertrag.class))).thenReturn(vertrag);

        ResponseEntity<Vertrag> response = vertragController.updateVertrag(1, vertragDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(vertrag, response.getBody());
    }

    @Test
    void testDeleteVertrag() {
        when(vertragsverwaltung.vertragLoeschen(1)).thenReturn(true);

        ResponseEntity<Void> response = vertragController.deleteVertrag(1);

        assertEquals(204, response.getStatusCodeValue());
    }

}