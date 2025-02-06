package de.axa.robin.vertragsverwaltung.backend.config;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    /**
     * Testet, ob der in-memory User "admin" mit der Rolle ADMIN korrekt konfiguriert wurde.
     */
    @Test
    public void testUserDetailsService() {
        UserDetailsService uds = context.getBean(UserDetailsService.class);
        UserDetails user = uds.loadUserByUsername("admin");
        assertThat(user).isNotNull();
        // Überprüft, ob der Benutzer über die Authority "ROLE_ADMIN" verfügt.
        assertThat(user.getAuthorities()).extracting("authority").contains("ROLE_ADMIN");
    }

    /**
     * Testet, dass öffentliche Endpunkte (z. B. "/" und "/login") ohne Authentifizierung
     * zugänglich sind. Da keine Controller implementiert sind, kann ein Status 404 zurückgegeben werden,
     * es darf jedoch keine Weiterleitung (302) erfolgen.
     */
    @Test
    public void testPublicAccess() throws Exception {
        String[] publicUrls = {"/", "/login", "/error", "/favicon.ico"};
        for (String url : publicUrls) {
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
    public void testAdminAccessWithoutAuthentication() throws Exception {
        String[] adminUrls = {"/home", "/printVertrage", "/editPreis", "/api/someEndpoint",
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
    public void testAdminAccessWithAuthentication() throws Exception {
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
    public void testLogout() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?logout"));
    }
}
