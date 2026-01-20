package de.hsbi.interpreter.runtime;

import de.hsbi.interpreter.ast.Type;

/**
 * represents a runtime value
 */
public class Value {
    private Type type;
    private Object data;
    private boolean isReference;
    private Value referencedValue; // if this is a reference, points to the actual value

    // constructor for non-reference values
    public Value(Type type, Object data) {
        this.type = type;
        this.data = data;
        this.isReference = false;
        this.referencedValue = null;
    }

    // constructor for reference values
    public Value(Value referencedValue) {
        this.type = referencedValue.getType();
        this.data = null;
        this.isReference = true;
        this.referencedValue = referencedValue;
    }

    public Type getType() {
        return type;
    }

    public boolean isReference() {
        return isReference;
    }

    public Value getReferencedValue() {
        return referencedValue;
    }

    // get the actual value (following references if necessary)
    public Object getData() {
        if (isReference) {
            return referencedValue.getData();
        }
        return data;
    }

    // set the actual value (following references if necessary)
    public void setData(Object newData) {
        if (isReference) {
            referencedValue.setData(newData);
        } else {
            this.data = newData;
        }
    }

    // for assignment: if this is a reference, write through to the referenced value
    public void assign(Value other) {
        if (isReference) {
            referencedValue.assign(other);
        } else {
            this.data = other.getData();
        }
    }

    // helper methods for specific types
    public int getIntValue() {
        return (Integer) getData();
    }

    public boolean getBoolValue() {
        return (Boolean) getData();
    }

    public char getCharValue() {
        return (Character) getData();
    }

    public String getStringValue() {
        return (String) getData();
    }

    public ObjectValue getObjectValue() {
        return (ObjectValue) getData();
    }

    @Override
    public String toString() {
        if (isReference) {
            return "Ref->" + referencedValue.toString();
        }
        return data != null ? data.toString() : "null";
    }

    // create default values for types
    public static Value defaultValue(Type type) {
        switch (type.getBaseType()) {
            case BOOL:
                return new Value(type, false);
            case INT:
                return new Value(type, 0);
            case CHAR:
                return new Value(type, '\0');
            case STRING:
                return new Value(type, "");
            case VOID:
                return new Value(type, null);
            case CLASS:
                return new Value(type, null); // will be initialized with constructor
            default:
                throw new RuntimeException("unknown type: " + type);
        }
    }
}
