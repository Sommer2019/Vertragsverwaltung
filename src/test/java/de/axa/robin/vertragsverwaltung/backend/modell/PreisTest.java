package de.axa.robin.vertragsverwaltung.backend.modell;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreisTest {

    private Preis preis;

    @BeforeEach
    public void setUp() {
        Repository repository = mock(Repository.class);
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.7)
                .add("factorage", 0.3)
                .add("factorspeed", 0.6)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        preis = new Preis(new Setup());
    }

    @Test
    public void testConstructor() {
        assertEquals(1.7, preis.getFaktor());
        assertEquals(0.3, preis.getAge());
        assertEquals(0.6, preis.getSpeed());
    }

    @Test
    public void testGettersAndSetters() {
        preis.setFaktor(2.0);
        assertEquals(2.0, preis.getFaktor());

        preis.setAge(2.0);
        assertEquals(2.0, preis.getAge());

        preis.setSpeed(2.0);
        assertEquals(2.0, preis.getSpeed());
    }
}