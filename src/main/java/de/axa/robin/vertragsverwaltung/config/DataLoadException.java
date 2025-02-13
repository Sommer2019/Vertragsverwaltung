package de.axa.robin.vertragsverwaltung.config;

public class DataLoadException extends RuntimeException  {
    public DataLoadException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}