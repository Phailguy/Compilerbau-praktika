package de.hsbi.interpreter.ast;

/**
 * represents an if statement (with optional else)
 */
public class IfStmt extends Statement {
    private Expression condition;
    private Statement thenStmt;
    private Statement elseStmt; // can be null

    public IfStmt(Expression condition, Statement thenStmt, Statement elseStmt) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getThenStmt() {
        return thenStmt;
    }

    public Statement getElseStmt() {
        return elseStmt;
    }

    public boolean hasElse() {
        return elseStmt != null;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIfStmt(this);
    }
}
