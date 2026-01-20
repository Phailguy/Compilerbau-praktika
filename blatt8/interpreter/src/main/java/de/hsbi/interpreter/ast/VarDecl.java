package de.hsbi.interpreter.ast;

/**
 * represents a variable declaration
 */
public class VarDecl extends Statement {
    private Type type;
    private boolean isReference;
    private String name;
    private Expression initializer; // can be null

    public VarDecl(Type type, boolean isReference, String name, Expression initializer) {
        this.type = type;
        this.isReference = isReference;
        this.name = name;
        this.initializer = initializer;
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

    public Expression getInitializer() {
        return initializer;
    }

    public boolean hasInitializer() {
        return initializer != null;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVarDecl(this);
    }
}
