package de.hsbi.interpreter.runtime;

/**
 * exception used to implement return statements
 * (using exception for control flow)
 */
public class ReturnException extends RuntimeException {
    private Value value;

    public ReturnException(Value value) {
        super(null, null, false, false); // disable stack trace for performance
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
