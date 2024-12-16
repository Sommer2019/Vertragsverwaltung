package de.axa.robin.vertragsverwaltung.backend.modell;

import de.axa.robin.vertragsverwaltung.backend.config.Setup;
import de.axa.robin.vertragsverwaltung.backend.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.backend.storage.editor.Edit;
import org.junit.jupiter.api.Test;


class PreisTest {
    private final Edit edit = new Edit(new Vertragsverwaltung(new Setup()));
    @Test
    void editPreisHandlesNegativeFactors() {
    }

    @Test
    void editPreisHandlesHighSpeed() {
    }

    @Test
    void editPreisHandlesHighAge() {
    }

    @Test
    void editPreisHandlesZeroSpeed() {
    }

}