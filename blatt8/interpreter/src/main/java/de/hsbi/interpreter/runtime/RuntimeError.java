package de.hsbi.interpreter.runtime;

/**
 * runtime error (e.g., division by zero)
 */
public class RuntimeError extends RuntimeException {
    public RuntimeError(String message) {
        super(message);
    }
}
