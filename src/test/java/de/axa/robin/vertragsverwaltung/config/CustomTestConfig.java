package de.axa.robin.vertragsverwaltung.config;

import de.axa.robin.vertragsverwaltung.storage.editor.CreateData;
import de.axa.robin.vertragsverwaltung.storage.editor.EditVertrag;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.mockito.Mockito.mock;
import de.axa.robin.vertragsverwaltung.storage.Repository;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;

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
    public CreateData create() {
        return mock(CreateData.class);
    }

    @Bean
    public EditVertrag edit() {
        return mock(EditVertrag.class);
    }

    @Bean
    public Vertragsverwaltung vertragsverwaltung() {
        return mock(Vertragsverwaltung.class);
    }
}
