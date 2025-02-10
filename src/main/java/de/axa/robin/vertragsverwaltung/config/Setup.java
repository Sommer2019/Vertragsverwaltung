package de.axa.robin.vertragsverwaltung.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
/**
 * Configuration properties for the setup.
 */
@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "input.setup")
public class Setup {
    /**
     * Path to the JSON repository.
     */
    @NotNull
    private String json_repositoryPath;
    /**
     * Path to the JSON price file.
     */
    @NotNull
    private String json_preisPath;
    /**
     * Path to the JSON brands file.
     */
    @NotNull
    private String json_brandsPath;
    /**
     * URL for testing.
     */
    @NotNull
    private String testURL;
    /**
     * URL for checking.
     */
    @NotNull
    private String checkURL;
    /**
     * Proxy host.
     */
    private String proxy_host;
    /**
     * Proxy port.
     */
    private int proxy_port;
    /**
     * Database URL.
     */
    private String db_url;
    /**
     * Database user.
     */
    private String db_user;
    /**
     * Database password.
     */
    private String db_pass;
}
