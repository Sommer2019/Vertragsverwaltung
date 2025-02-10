package de.axa.robin.vertragsverwaltung.storage.editor;
//ToDO Tests

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/*
@WebMvcTest(CreateVertrag.class)
@Import(CustomTestConfig.class)
@ExtendWith(MockitoExtension.class)
public class CreateVertragTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private Vertragsverwaltung vertragsverwaltung;

    @Mock
    private Create create;

    @Mock
    private MenuSpring menuSpring;

    @Mock
    private InputValidator inputValidator;

    @InjectMocks
    private CreateVertrag createVertrag;

    @Disabled("not ready yet")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetCreateVertrag() throws Exception {
        int generatedVsnr = 1000;
        Mockito.when(create.createvsnr()).thenReturn(generatedVsnr);
        Mockito.when(menuSpring.getVsnr()).thenReturn(generatedVsnr);

        mockMvc.perform(get("/createVertrag"))
                .andExpect(status().isOk())
                .andExpect(view().name("createVertrag"))
                .andExpect(model().attributeExists("vertrag"))
                .andExpect(model().attribute("vsnr", generatedVsnr));
    }

    @Disabled("not ready yet")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testPostCreateVertrag_Success() throws Exception {
        int generatedVsnr = 2000;
        double preis = 250.0;
        Mockito.when(create.createvsnr()).thenReturn(generatedVsnr);
        Mockito.when(create.createPreis(Mockito.anyBoolean(), Mockito.any(), Mockito.anyInt())).thenReturn(preis);
        Mockito.when(vertragsverwaltung.vertragAnlegen(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/createVertrag")
                        .param("versicherungsbeginn", LocalDate.now().toString())
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("antragsDatum", LocalDate.now().toString())
                        .param("monatlich", "true")
                        .param("partner.vorname", "Max")
                        .param("partner.nachname", "Mustermann")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("partner.geschlecht", "M")
                        .param("partner.land", "Deutschland")
                        .param("partner.strasse", "Hauptstr.")
                        .param("partner.hausnummer", "1")
                        .param("partner.plz", "12345")
                        .param("partner.stadt", "Berlin")
                        .param("partner.bundesland", "BE")
                        .param("fahrzeug.amtlichesKennzeichen", "ABC123")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("confirm"))
                .andExpect(model().attribute("confirm", containsString(String.valueOf(generatedVsnr))));
    }

    @Disabled("not ready yet")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testPostCreateVertrag_ValidationError() throws Exception {
        LocalDate pastDate = LocalDate.now().minusDays(1);

        mockMvc.perform(post("/createVertrag")
                        .param("versicherungsbeginn", pastDate.toString())
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("antragsDatum", LocalDate.now().toString())
                        .param("monatlich", "true")
                        .param("partner.vorname", "Max")
                        .param("partner.nachname", "Mustermann")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("partner.geschlecht", "M")
                        .param("partner.land", "Deutschland")
                        .param("partner.strasse", "Hauptstr.")
                        .param("partner.hausnummer", "1")
                        .param("partner.plz", "12345")
                        .param("partner.stadt", "Berlin")
                        .param("partner.bundesland", "BE")
                        .param("fahrzeug.amtlichesKennzeichen", "ABC123")
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                        .param("fahrzeug.wagnisskennziffer", "112")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("createVertrag"));
    }

    @Disabled("not ready yet")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreatePreis_WithValidPlz() throws Exception {
        double preisValue = 123.45;
        Mockito.when(create.createPreis(Mockito.anyBoolean(), Mockito.any(), Mockito.anyInt())).thenReturn(preisValue);

        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "12345")
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("monatlich", "true")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is(String.format(Locale.GERMANY, "%.2f €", preisValue))));
    }

    @Disabled("not ready yet")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreatePreis_WithExpiredInsurance() throws Exception {
        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "12345")
                        .param("versicherungsablauf", LocalDate.now().minusDays(1).toString())
                        .param("monatlich", "true")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is("0,00 €")));
    }

    @Disabled("not ready yet")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreatePreis_WithMissingPlz() throws Exception {
        mockMvc.perform(post("/createPreis")
                        .param("partner.plz", "")
                        .param("versicherungsablauf", LocalDate.now().plusDays(10).toString())
                        .param("monatlich", "true")
                        .param("partner.geburtsdatum", LocalDate.now().minusYears(30).toString())
                        .param("fahrzeug.hoechstgeschwindigkeit", "200")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preis", is("--,-- €")));
    }
}*/