package de.hsbi.interpreter.ast;

/**
 * represents a type in the language
 */
public class Type extends ASTNode {
    public enum BaseType {
        BOOL, INT, CHAR, STRING, VOID, CLASS
    }

    private BaseType baseType;
    private String className; // for class types

    // for primitive types
    public Type(BaseType baseType) {
        this.baseType = baseType;
        this.className = null;
    }

    // for class types
    public Type(String className) {
        this.baseType = BaseType.CLASS;
        this.className = className;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    public String getClassName() {
        return className;
    }

    public boolean isPrimitive() {
        return baseType != BaseType.CLASS;
    }

    public boolean isVoid() {
        return baseType == BaseType.VOID;
    }

    @Override
    public String toString() {
        if (baseType == BaseType.CLASS) {
            return className;
        }
        return baseType.toString().toLowerCase();
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitType(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Type)) return false;
        Type other = (Type) obj;
        if (baseType != other.baseType) return false;
        if (baseType == BaseType.CLASS) {
            return className.equals(other.className);
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (baseType == BaseType.CLASS) {
            return className.hashCode();
        }
        return baseType.hashCode();
    }
}
