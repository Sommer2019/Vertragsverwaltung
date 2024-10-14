package de.axa.robin.vertragsverwaltung.storage.Checker;

import java.time.LocalDate;

public class Checker {
    public static boolean geburtsdatum(LocalDate geburtsdatum) {
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(18);
        LocalDate maxDate = now.minusYears(110);
        return !geburtsdatum.isBefore(minDate) || !geburtsdatum.isAfter(maxDate);
    }

    public static boolean string(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return input.isEmpty();
    }

}
