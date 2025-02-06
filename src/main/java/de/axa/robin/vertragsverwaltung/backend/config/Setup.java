package de.axa.robin.vertragsverwaltung.backend.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "input.setup")
public class Setup {
    @NotNull
    private String json_repositoryPath;
    @NotNull
    private String json_preisPath;
    @NotNull
    private String json_brandsPath;
    @NotNull
    private String testURL;
    @NotNull
    private String checkURL;
    private String proxy_host;
    private int proxy_port;
    private String db_url;
    private String db_user;
    private String db_pass;
}
