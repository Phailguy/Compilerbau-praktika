package de.hsbi.interpreter.ast;

/**
 * represents a function/method parameter
 */
public class Parameter extends ASTNode {
    private Type type;
    private boolean isReference;
    private String name;

    public Parameter(Type type, boolean isReference, String name) {
        this.type = type;
        this.isReference = isReference;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public boolean isReference() {
        return isReference;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitParameter(this);
    }
}
