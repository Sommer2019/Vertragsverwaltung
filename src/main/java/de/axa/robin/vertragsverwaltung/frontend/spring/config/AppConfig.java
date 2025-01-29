package de.axa.robin.vertragsverwaltung.frontend.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    private static final String RESOURCE_PATH = "/static/**";
    private static final String CLASSPATH_RESOURCE_LOCATION = "classpath:/static/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mappt statische Ressourcen unter /static/ auf den classpath-Ordner static
        registry.addResourceHandler(RESOURCE_PATH)
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATION);
    }
}
