package de.axa.robin.vertragsverwaltung.exceptions;

public class DataLoadException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "An error occurred while loading data.";

    public DataLoadException() {
        super(DEFAULT_MESSAGE);
    }

    public DataLoadException(String errorMessage) {
        super(errorMessage);
    }

    public DataLoadException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public DataLoadException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}