package de.axa.robin.vertragsverwaltung.storage.editor;
//ToDO Tests
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.axa.robin.vertragsverwaltung.config.CustomTestConfig;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.user_interaction.MenuSpring;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(HandleVertrag.class)
@ExtendWith(SpringExtension.class)
@Import({CustomTestConfig.class, HandleVertragTest.TestConfig.class})
public class HandleVertragTest {

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private CreateData createData;

    @Mock
    private InputValidator inputValidator;

    @Mock
    private HandleVertrag handleVertrag;

    @Mock
    private MenuSpring menuSpring;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Configuration
    static class TestConfig {
        @Bean
        public MenuSpring menuSpring() {
            return org.mockito.Mockito.mock(MenuSpring.class);
        }
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testProcessPrintVertrag_EmptyVsnr() throws Exception {
        mockMvc.perform(post("/home")
                        .param("vsnr", "")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testProcessPrintVertrag_InvalidVsnr() throws Exception {
        mockMvc.perform(post("/home")
                        .param("vsnr", "abc")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", "Ungültige VSNR!"))
                .andExpect(view().name("home"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testProcessPrintVertrag_NotFound() throws Exception {
        int vsnr = 123;
        when(vertragsverwaltung.getVertrag(vsnr)).thenReturn(null);

        mockMvc.perform(post("/home")
                        .param("vsnr", String.valueOf(vsnr))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", "Vertrag nicht gefunden!"))
                .andExpect(view().name("home"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testProcessPrintVertrag_Found() throws Exception {
        int vsnr = 456;
        Partner partner = new Partner("John", "Doe", 'M', LocalDate.of(1990, 1, 1),
                "Germany", "Main Street", "10", "12345", "Berlin", "Berlin");
        Fahrzeug fahrzeug = new Fahrzeug("XYZ-123", "VW", "Golf", 200, 42);
        Vertrag vertrag = new Vertrag(vsnr, true, 1000.0, LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 1, 1), LocalDate.now(), fahrzeug, partner);
        when(vertragsverwaltung.getVertrag(vsnr)).thenReturn(vertrag);

        mockMvc.perform(post("/home")
                        .param("vsnr", String.valueOf(vsnr))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vsnr", vsnr))
                .andExpect(view().name("handleVertrag"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testShowDelete() throws Exception {
        mockMvc.perform(get("/showDelete")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("showFields", true))
                .andExpect(view().name("handleVertrag"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testDeleteVertrag() throws Exception {
        int vsnr = 789;
        when(menuSpring.getVsnr()).thenReturn(vsnr);

        mockMvc.perform(post("/showDelete")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("confirm", "Vertrag erfolgreich gelöscht!"))
                .andExpect(view().name("home"));

        verify(vertragsverwaltung).vertragLoeschen(vsnr);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testEditVertrag_WithErrors() {
        Partner partner = new Partner("Alice", "Smith", 'F', LocalDate.of(1985, 5, 15),
                "Germany", "Street", "5", "10115", "Berlin", "Berlin");
        Fahrzeug fahrzeug = new Fahrzeug("ABC-999", "BMW", "X5", 240, 99);
        Vertrag inputVertrag = new Vertrag(0, false, 0.0,
                LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), LocalDate.now(), fahrzeug, partner);

        BindingResult bindingResult = new BeanPropertyBindingResult(inputVertrag, "vertrag");
        bindingResult.reject("error", "Error message");

        when(vertragsverwaltung.getVertrag(anyInt())).thenReturn(inputVertrag);

        Model model = new ExtendedModelMap();
        String view = handleVertrag.editVertrag(inputVertrag, bindingResult, true, model);

        org.junit.jupiter.api.Assertions.assertEquals("handleVertrag", view);
        org.junit.jupiter.api.Assertions.assertTrue((Boolean) model.getAttribute("editVisible"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Disabled("not ready yet")
    public void testEditVertrag_Success() {
        Partner partner = new Partner("Bob", "Johnson", 'M', LocalDate.of(1975, 7, 20),
                "Germany", "Broadway", "20", "20095", "Hamburg", "Hamburg");
        Fahrzeug fahrzeug = new Fahrzeug("DEF-456", "Audi", "A4", 220, 88);
        Vertrag inputVertrag = new Vertrag(0, true, 0.0,
                LocalDate.of(2023, 6, 1), LocalDate.of(2024, 6, 1), LocalDate.now(), fahrzeug, partner);

        BindingResult bindingResult = new BeanPropertyBindingResult(inputVertrag, "vertrag");

        int vsnr = 321;
        when(menuSpring.getVsnr()).thenReturn(vsnr);
        when(createData.createVertragAndSave(any(Vertrag.class), anyBoolean(), anyInt())).thenReturn(1500.0);

        Model model = new ExtendedModelMap();
        String view = handleVertrag.editVertrag(inputVertrag, bindingResult, false, model);

        org.junit.jupiter.api.Assertions.assertEquals("home", view);
        String confirmMsg = (String) model.getAttribute("confirm");
        org.junit.jupiter.api.Assertions.assertTrue(confirmMsg.contains("321"));
        org.junit.jupiter.api.Assertions.assertTrue(confirmMsg.contains("1500,0€"));
        verify(vertragsverwaltung).vertragLoeschen(vsnr);
    }
}