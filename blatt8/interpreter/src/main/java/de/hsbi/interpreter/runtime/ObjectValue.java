package de.hsbi.interpreter.runtime;

import de.hsbi.interpreter.symbols.ClassSymbol;

import java.util.HashMap;
import java.util.Map;

/**
 * represents a runtime object (instance of a class)
 */
public class ObjectValue {
    private ClassSymbol classSymbol;
    private Map<String, Value> fields;

    public ObjectValue(ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
        this.fields = new HashMap<>();
    }

    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    public Map<String, Value> getFields() {
        return fields;
    }

    public Value getField(String name) {
        return fields.get(name);
    }

    public void setField(String name, Value value) {
        fields.put(name, value);
    }

    public boolean hasField(String name) {
        return fields.containsKey(name);
    }

    @Override
    public String toString() {
        return classSymbol.getName() + "@" + Integer.toHexString(hashCode());
    }
}
