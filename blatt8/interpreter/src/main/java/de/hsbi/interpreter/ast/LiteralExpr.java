package de.hsbi.interpreter.ast;

/**
 * represents a literal value (constant)
 */
public class LiteralExpr extends Expression {
    public enum LiteralType {
        INT, BOOL, CHAR, STRING
    }

    private LiteralType literalType;
    private Object value;

    public LiteralExpr(LiteralType literalType, Object value) {
        this.literalType = literalType;
        this.value = value;
    }

    public LiteralType getLiteralType() {
        return literalType;
    }

    public Object getValue() {
        return value;
    }

    public int getIntValue() {
        return (Integer) value;
    }

    public boolean getBoolValue() {
        return (Boolean) value;
    }

    public char getCharValue() {
        return (Character) value;
    }

    public String getStringValue() {
        return (String) value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteralExpr(this);
    }
}
