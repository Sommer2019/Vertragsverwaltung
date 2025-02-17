package de.axa.robin.vertragsverwaltung.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MenuControllerTest {

    @Test
    void testStartWebsite() {
        MenuController menuController = new MenuController();
        String viewName = menuController.startWebsite();
        assertEquals("index", viewName);
    }

    @Test
    void testHome() {
        MenuController menuController = new MenuController();
        String viewName = menuController.home();
        assertEquals("home", viewName);
    }

    @Test
    void testGetAndSetVsnr() {
        MenuController menuController = new MenuController();
        menuController.setVsnr(12345);
        assertEquals(12345, menuController.getVsnr());
    }
}