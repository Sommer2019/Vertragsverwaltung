package de.axa.robin.vertragsverwaltung.frontend.spring.storage;

import de.axa.robin.vertragsverwaltung.backend.modell.Preis;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Edit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EditPreisTest {

    @Mock
    private Edit edit;

    @InjectMocks
    private EditPreis editPreis;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void editPreisPageLoadsCorrectly() {
        Model model = mock(Model.class);
        String viewName = editPreis.editPreis(model);
        assertEquals("editPreis", viewName);
    }

    @Test
    void editPreisCalculatesCorrectly() {
        Preis preismodell = new Preis();
        preismodell.setFaktor(1.7);
        preismodell.setAge(0.3);
        preismodell.setSpeed(0.6);
        when(edit.recalcpricerun(anyDouble(), anyInt(), anyInt(), anyList())).thenReturn(new BigDecimal("0.0"));

        Map<String, Object> response = editPreis.editPreis(preismodell);

        assertEquals("0,00 €", response.get("preis"));
    }

    @Test
    void editPreisUpdatesModelCorrectly() {
        Preis preismodell = new Preis();
        preismodell.setFaktor(1.7);
        preismodell.setAge(0.3);
        preismodell.setSpeed(0.6);
        when(edit.recalcpricerun(anyDouble(), anyInt(), anyInt(), anyList())).thenReturn(new BigDecimal("0.00"));
        Model model = mock(Model.class);

        String viewName = editPreis.editPreis(preismodell, model);

        verify(model).addAttribute("confirm", "Preise erfolgreich aktualisiert! neue Preissumme: 0,00€ pro Jahr");
        assertEquals("index", viewName);
    }
}