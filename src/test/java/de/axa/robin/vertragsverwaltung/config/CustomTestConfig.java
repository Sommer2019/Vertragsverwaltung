package de.axa.robin.vertragsverwaltung.config;

import de.axa.robin.vertragsverwaltung.controller.CreateVertragController;
import de.axa.robin.vertragsverwaltung.controller.HandleVertragController;
import de.axa.robin.vertragsverwaltung.controller.MenuController;
import de.axa.robin.vertragsverwaltung.mapper.VertragMapper;
import de.axa.robin.vertragsverwaltung.util.VertragUtil;
import de.axa.robin.vertragsverwaltung.controller.EditPreisModelController;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import de.axa.robin.vertragsverwaltung.validators.AdressValidator;
import de.axa.robin.vertragsverwaltung.validators.InputValidator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.mockito.Mockito.mock;
import de.axa.robin.vertragsverwaltung.storage.Repository;

@TestConfiguration
public class CustomTestConfig {
    @Bean
    public Repository repository() {
        return mock(Repository.class);
    }

    @Bean
    public Setup setup() {
        return mock(Setup.class);
    }

    @Bean
    public VertragMapper vertragMapper() {
        return mock(VertragMapper.class);
    }

    @Bean
    public VertragUtil create() {
        return mock(VertragUtil.class);
    }

    @Bean
    public VertragsService vertragsverwaltung() {
        return mock(VertragsService.class);
    }

    @Bean
    public MenuController menuSpring() {
        return mock(MenuController.class);
    }

    @Bean
    public InputValidator inputValidator() {
        return mock(InputValidator.class);
    }

    @Bean
    public HandleVertragController handleVertrag() {
        return mock(HandleVertragController.class);
    }

    @Bean
    public EditPreisModelController editPreis() {
        return mock(EditPreisModelController.class);
    }

    @Bean
    public CreateVertragController createVertrag() {
        return mock(CreateVertragController.class);
    }

    @Bean
    public AdressValidator adressValidator() {
        return mock(AdressValidator.class);
    }

}
