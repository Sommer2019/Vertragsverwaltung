package de.axa.robin.vertragsverwaltung.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    /**
     * Testet, ob der in-memory User "admin" mit der Rolle ADMIN korrekt konfiguriert wurde.
     */
    @Test
    void testUserDetailsService() {
        UserDetailsService uds = context.getBean(UserDetailsService.class);
        UserDetails user = uds.loadUserByUsername("admin");
        assertThat(user).isNotNull();
        // Überprüft, ob der Benutzer über die Authority "ROLE_ADMIN" verfügt.
        assertThat(user.getAuthorities()).extracting("authority").contains("ROLE_ADMIN");
    }

    /**
     * Testet, dass öffentliche Endpunkte (z. B. "/" und "/login") ohne Authentifizierung
     * zugänglich sind. Da keine konkrete Controller implementiert sind, kann ein Status 404 zurückgegeben werden,
     * es darf jedoch keine Weiterleitung (302) erfolgen.
     */
    @Test
    void testAccess() throws Exception {
        String[] Urls = {"/", "/login", "/error", "/favicon.ico"};
        for (String url : Urls) {
            mockMvc.perform(get(url))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        // Der Status darf nicht 302 (Redirect) sein, was auf eine Sicherheitsumleitung hinweisen würde.
                        assertThat(status).isNotEqualTo(302);
                    });
        }
    }

    /**
     * Testet, dass bei einem Zugriff auf Admin‑geschützte Endpunkte ohne Authentifizierung
     * eine Weiterleitung zur Login-Seite (in diesem Fall "/") erfolgt.
     */
    @Test
    void testAdminAccessWithoutAuthentication() throws Exception {
        String[] adminUrls = {"/home", "/printVertrage", "/editPreis",
                "/createVertrag", "/json/someEndpoint", "/showEdit", "/showDelete",
                "/precalcPreis", "/createPreis", "/logout"};
        for (String url : adminUrls) {
            mockMvc.perform(get(url))
                    .andExpect(status().is3xxRedirection());
        }
    }

    /**
     * Testet, dass ein authentifizierter Benutzer mit der Rolle ADMIN Zugriff auf einen
     * Admin‑geschützten Endpunkt erhält. Da keine konkrete Controller-Implementierung existiert,
     * wird hier mit einem 404 Not Found gearbeitet, was akzeptabel ist.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminAccessWithAuthentication() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * Testet das Logout-Verhalten: Bei einem Logout (POST an "/logout") wird
     * gemäß der Konfiguration eine Weiterleitung auf "/?logout" erwartet.
     * Da CSRF in der Konfiguration deaktiviert wurde, ist kein CSRF-Token erforderlich.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testLogout() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?logout"));
    }

    /**
     * Testet, dass API-Endpunkte ohne Authentifizierung nicht zugänglich sind.
     */
    @Test
    void testApiAccessWithoutAuthentication() throws Exception {
        String[] apiUrls = {"/api/preisverwaltung/", "/api/vertragsverwaltung/"};
        for (String url : apiUrls) {
            mockMvc.perform(get(url))
                    .andExpect(status().is4xxClientError());
        }
    }

    /**
     * Testet, dass API-Endpunkte mit HTTP Basic Auth und gültigen Anmeldeinformationen zugänglich sind.
     * Hier wird der Benutzer "apiuser" mit der Rolle API_USER verwendet.
     * Da in der Testumgebung eventuell kein Controller existiert, ist ein 404 Not Found akzeptabel,
     * solange keine Sicherheitsumleitung erfolgt.
     */
    @Test
    void testApiAccessWithBasicAuth() throws Exception {
        mockMvc.perform(get("/api/preisverwaltung/").with(httpBasic("apiuser", "apiuser")))
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * Testet, dass API-Endpunkte mit einem Mock-User, der die Rolle API_USER besitzt, erreichbar sind.
     * Auch hier ist ein 404 Not Found akzeptabel, sofern keine Sicherheitsumleitung erfolgt.
     */
    @Test
    @WithMockUser(username = "apiuser", roles = {"API_USER"})
    void testApiAccessWithMockUser() throws Exception {
        mockMvc.perform(get("/api/vertragsverwaltung/"))
                .andExpect(status().is2xxSuccessful());
    }
}
