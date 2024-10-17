package org.carpetorgaddition.exception;

@SuppressWarnings("unused")
public class ProductionEnvironmentError extends Error {
    public ProductionEnvironmentError(String message) {
        super(message);
    }
}
