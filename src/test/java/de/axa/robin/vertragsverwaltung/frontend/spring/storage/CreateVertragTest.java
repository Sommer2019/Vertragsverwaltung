package de.axa.robin.vertragsverwaltung.frontend.spring.storage;

import de.axa.robin.vertragsverwaltung.frontend.spring.user_interaction.MenuSpring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CreateVertragTest {

    @Mock
    private MenuSpring menuSpring;

    @Mock
    private Model model;

    @InjectMocks
    private CreateVertrag createVertrag;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createVertragPageLoadsCorrectly() {
        when(menuSpring.getVsnr()).thenReturn(12345678);
        String viewName = createVertrag.createVertrag(model);
        assertEquals("createVertrag", viewName);
    }
}