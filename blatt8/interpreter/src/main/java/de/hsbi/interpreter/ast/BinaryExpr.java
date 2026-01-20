package de.hsbi.interpreter.ast;

/**
 * represents a binary expression (e.g., a + b, x < y)
 */
public class BinaryExpr extends Expression {
    public enum Operator {
        // arithmetic
        PLUS, MINUS, MULT, DIV, MOD,
        // comparison
        EQ, NEQ, LT, LEQ, GT, GEQ,
        // logical
        AND, OR
    }

    private Operator operator;
    private Expression left;
    private Expression right;

    public BinaryExpr(Operator operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBinaryExpr(this);
    }
}
