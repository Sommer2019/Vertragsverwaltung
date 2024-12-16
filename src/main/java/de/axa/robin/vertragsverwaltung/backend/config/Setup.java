package de.axa.robin.vertragsverwaltung.backend.config;

public class Setup {
    private String repositoryPath;
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

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public String getPreisPath() {
        return preisPath;
    }

    public String getBrandsPath() {
        return brandsPath;
    }

    public String getTestURL() {
        return testURL;
    }

    public String getCheckURL() {
        return checkURL;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath=repositoryPath;
    }

    public void setPreisPath(String preisPath) {
        this.preisPath = preisPath;
    }
}
