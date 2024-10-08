package de.axa.robin.vertragsverwaltung;

import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.user_interaction.Menu;

public class Main {
    public static void main(String[] args) {
        Vertragsverwaltung.ladeVertrage();
        Menu.menu();
        Vertragsverwaltung.speichereVertrage();
    }

}
