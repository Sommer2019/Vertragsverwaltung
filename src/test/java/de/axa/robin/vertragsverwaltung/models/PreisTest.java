package de.axa.robin.vertragsverwaltung.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PreisTest {

    private Preis preis;

    @BeforeEach
    public void setUp() {
        preis = new Preis();
        preis.setFaktor(1.7);
        preis.setAge(0.3);
        preis.setSpeed(0.6);
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