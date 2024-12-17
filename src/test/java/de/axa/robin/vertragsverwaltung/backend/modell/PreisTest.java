package de.axa.robin.vertragsverwaltung.backend.modell;

import de.axa.robin.vertragsverwaltung.backend.storage.Repository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PreisTest {

    private Repository repository;
    private Preis preis;

    @BeforeEach
    public void setUp() {
        repository = mock(Repository.class);
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("factor", 1.0)
                .add("factorage", 1.0)
                .add("factorspeed", 1.0)
                .build();
        when(repository.ladeFaktoren()).thenReturn(jsonObject);

        preis = new Preis();
    }

    @Test
    public void testConstructor() {
        assertEquals(1.0, preis.getFaktor());
        assertEquals(1.0, preis.getAge());
        assertEquals(1.0, preis.getSpeed());
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