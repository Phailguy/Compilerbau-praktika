package de.hsbi.interpreter.ast;

/**
 * represents an assignment expression (e.g., x = 5)
 */
public class AssignExpr extends Expression {
    private Expression target; // must be an lvalue
    private Expression value;

    public AssignExpr(Expression target, Expression value) {
        this.target = target;
        this.value = value;
    }

    public Expression getTarget() {
        return target;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignExpr(this);
    }
}
