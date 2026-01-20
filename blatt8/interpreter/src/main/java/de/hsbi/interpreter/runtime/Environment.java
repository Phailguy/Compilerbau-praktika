package de.hsbi.interpreter.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * represents a runtime environment (scope) that stores variable values
 */
public class Environment {
    private Environment parent;
    private Map<String, Value> variables;

    public Environment(Environment parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
    }

    public Environment getParent() {
        return parent;
    }

    /**
     * define a variable in this environment
     */
    public void define(String name, Value value) {
        variables.put(name, value);
    }

    /**
     * get a variable from this environment or parent environments
     */
    public Value get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (parent != null) {
            return parent.get(name);
        }
        throw new RuntimeException("variable '" + name + "' not found");
    }

    /**
     * check if variable exists
     */
    public boolean has(String name) {
        if (variables.containsKey(name)) {
            return true;
        }
        if (parent != null) {
            return parent.has(name);
        }
        return false;
    }

    /**
     * assign to a variable (finds it in this or parent environments)
     */
    public void assign(String name, Value value) {
        if (variables.containsKey(name)) {
            Value existing = variables.get(name);
            existing.assign(value);
            return;
        }
        if (parent != null) {
            parent.assign(name, value);
            return;
        }
        throw new RuntimeException("variable '" + name + "' not found");
    }
}
