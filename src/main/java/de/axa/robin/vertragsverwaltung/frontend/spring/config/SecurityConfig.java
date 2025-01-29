package de.axa.robin.vertragsverwaltung.frontend.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Constants for URL patterns
    private static final String[] PERMITTED_PATHS = {"/static/**", "/js/**", "/css/**", "/", "/api/**", "/login", "/error", "/favicon.ico"};
    private static final String[] ADMIN_PATHS = {"/home", "/printVertrage", "/editPreis", "/createVertrag", "/json/**", "/showEdit", "/showDelete", "/precalcPreis", "/createPreis", "/logout"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMITTED_PATHS).permitAll()
                        .requestMatchers(ADMIN_PATHS).hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
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
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // Custom logout success handler that redirects to the home page with a logout message
    @Bean
    public LogoutSuccessHandler customLogoutSuccessHandler() {
        return (request, response, authentication) -> response.sendRedirect("/?logout");
    }

    // In-memory user details service with a default admin user
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    // Password encoder bean using BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CSRF token repository configuration
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-Csrf-Token");
        return repository;
    }
}
