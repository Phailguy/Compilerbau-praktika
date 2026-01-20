package de.hsbi.interpreter.ast;

/**
 * base class for all expressions
 */
public abstract class Expression extends ASTNode {
    // type of this expression (filled in during semantic analysis)
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
