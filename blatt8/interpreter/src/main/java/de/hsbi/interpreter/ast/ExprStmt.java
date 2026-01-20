package de.hsbi.interpreter.ast;

/**
 * represents an expression statement (expression followed by semicolon)
 */
public class ExprStmt extends Statement {
    private Expression expression;

    public ExprStmt(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitExprStmt(this);
    }
}
