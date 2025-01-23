package de.axa.robin.vertragsverwaltung.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class Setup {
    @Setter
    private String repositoryPath;
    @Setter
    private String preisPath;
    private final String brandsPath;
    private final String testURL;
    private final String checkURL;
    private final String host;
    private final int port;

    public Setup() {
        this.repositoryPath = "src/main/resources/static/json/vertrage.json";
        this.preisPath = "src/main/resources/static/json/preiscalc.json";
        this.brandsPath = "src/main/resources/static/json/brands.json";
        this.testURL = "https://www.google.com";
        this.checkURL = "https://nominatim.openstreetmap.org/search?format=json&q=";
        this.host = "localhost";
        this.port = 3128;
    }

}
