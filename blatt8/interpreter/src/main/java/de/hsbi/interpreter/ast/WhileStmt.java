package de.hsbi.interpreter.ast;

/**
 * represents a while statement
 */
public class WhileStmt extends Statement {
    private Expression condition;
    private Statement body;

    public WhileStmt(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitWhileStmt(this);
    }
}
