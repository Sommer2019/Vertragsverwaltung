package de.axa.robin.vertragsverwaltung.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuration class for Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PERMITTED_PATHS = {"/static/**", "/js/**", "/css/**", "/", "/login", "/error", "/favicon.ico"};
    private static final String[] ADMIN_PATHS = {"/home", "/printVertrage", "/editPreis", "/createVertrag", "/json/**",  "/showEdit", "/showDelete", "/precalcPreis", "/createPreis", "/logout"};
    private static final String[] API_PATHS = {"/api/**"};
    /**
     * Configures the security filter chain.
     *
     * @param http the HttpSecurity to modify
     * @return the SecurityFilterChain
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(API_PATHS)  // Disable CSRF for API paths
                        .csrfTokenRepository(csrfTokenRepository())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMITTED_PATHS).permitAll()
                        .requestMatchers(ADMIN_PATHS).hasRole("ADMIN")
                        .requestMatchers(API_PATHS).hasRole("API_USER")
                )
                .httpBasic(withDefaults())  // Enable Basic Authentication
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", false)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
                )
                .csrf(AbstractHttpConfigurer::disable);  // Disable CSRF globally

        return http.build();
    }

    /**
     * Custom logout success handler that redirects to the home page with a logout message.
     *
     * @return the LogoutSuccessHandler
     */
    @Bean
    public LogoutSuccessHandler customLogoutSuccessHandler() {
        return (request, response, authentication) -> response.sendRedirect("/?logout");
    }

    /**
     * In-memory user details service with a default admin user.
     *
     * @return the UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails apiUser = User.builder()
                .username("apiuser")
                .password(passwordEncoder().encode("apiuser"))
                .roles("API_USER")
                .build();

        return new InMemoryUserDetailsManager(admin, apiUser);
    }

    /**
     * Password encoder bean using BCrypt.
     *
     * @return the PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(true);
    }
    /**
     * CSRF token repository configuration.
     *
     * @return the CsrfTokenRepository
     */
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-Csrf-Token");
        return repository;
    }
}
