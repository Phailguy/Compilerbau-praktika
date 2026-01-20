package de.hsbi.interpreter.ast;

/**
 * represents a unary expression (e.g., -x, !flag)
 */
public class UnaryExpr extends Expression {
    public enum Operator {
        PLUS, MINUS, NOT
    }

    private Operator operator;
    private Expression operand;

    public UnaryExpr(Operator operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitUnaryExpr(this);
    }
}
