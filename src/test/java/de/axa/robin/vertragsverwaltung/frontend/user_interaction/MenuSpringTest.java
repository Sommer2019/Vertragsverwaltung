package de.axa.robin.vertragsverwaltung.frontend.user_interaction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MenuSpringTest {

    @Test
    void testStartWebsite() {
        MenuSpring menuSpring = new MenuSpring();
        String viewName = menuSpring.startWebsite();
        assertEquals("index", viewName);
    }

    @Test
    void testGetAndSetVsnr() {
        MenuSpring menuSpring = new MenuSpring();
        menuSpring.setVsnr(12345);
        assertEquals(12345, menuSpring.getVsnr());
    }
}