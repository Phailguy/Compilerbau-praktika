package de.hsbi.interpreter.ast;

/**
 * represents a return statement
 */
public class ReturnStmt extends Statement {
    private Expression value; // can be null for void returns

    public ReturnStmt(Expression value) {
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public boolean hasValue() {
        return value != null;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitReturnStmt(this);
    }
}
