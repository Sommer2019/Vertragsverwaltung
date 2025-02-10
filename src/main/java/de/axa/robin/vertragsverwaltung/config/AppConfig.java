package de.axa.robin.vertragsverwaltung.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up web MVC configurations.
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    private static final String RESOURCE_PATH = "/static/**";
    private static final String CLASSPATH_RESOURCE_LOCATION = "classpath:/static/";

    /**
     * Adds resource handlers for serving static resources.
     *
     * @param registry the ResourceHandlerRegistry to configure
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map static resources under /static/ to the classpath folder static
        registry.addResourceHandler(RESOURCE_PATH)
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATION);
    }
}
