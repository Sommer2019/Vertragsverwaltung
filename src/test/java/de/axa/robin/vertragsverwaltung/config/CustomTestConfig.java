package de.axa.robin.vertragsverwaltung.config;

import de.axa.robin.vertragsverwaltung.services.CreateUnsetableData;
import de.axa.robin.vertragsverwaltung.api.Mapper;
import de.axa.robin.vertragsverwaltung.controller.CreateVertrag;
import de.axa.robin.vertragsverwaltung.controller.EditPreis;
import de.axa.robin.vertragsverwaltung.controller.HandleVertrag;
import de.axa.robin.vertragsverwaltung.services.VertragsService;
import de.axa.robin.vertragsverwaltung.validators.AdressValidator;
import de.axa.robin.vertragsverwaltung.validators.InputValidator;
import de.axa.robin.vertragsverwaltung.controller.MenuSpring;
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
    public Mapper vertragMapper() {
        return mock(Mapper.class);
    }

    @Bean
    public CreateUnsetableData create() {
        return mock(CreateUnsetableData.class);
    }

    @Bean
    public VertragsService vertragsverwaltung() {
        return mock(VertragsService.class);
    }

    @Bean
    public MenuSpring menuSpring() {
        return mock(MenuSpring.class);
    }

    @Bean
    public InputValidator inputValidator() {
        return mock(InputValidator.class);
    }

    @Bean
    public HandleVertrag handleVertrag() {
        return mock(HandleVertrag.class);
    }

    @Bean
    public EditPreis editPreis() {
        return mock(EditPreis.class);
    }

    @Bean
    public CreateVertrag createVertrag() {
        return mock(CreateVertrag.class);
    }

    @Bean
    public AdressValidator adressValidator() {
        return mock(AdressValidator.class);
    }
}
