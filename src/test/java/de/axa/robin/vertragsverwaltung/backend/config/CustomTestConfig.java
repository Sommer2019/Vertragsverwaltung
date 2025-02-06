package de.axa.robin.vertragsverwaltung.backend.config;

import de.axa.robin.vertragsverwaltung.backend.storage.editor.Create;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Edit;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.mockito.Mockito.mock;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;

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
    public Create create() {
        return mock(Create.class);
    }

    @Bean
    public Edit edit() {
        return mock(Edit.class);
    }

    @Bean
    public Vertragsverwaltung vertragsverwaltung() {
        return mock(Vertragsverwaltung.class);
    }
}
