package de.hsbi.interpreter.ast;

/**
 * base class for all AST nodes
 */
public abstract class ASTNode {
    // position information for error messages
    private int line;
    private int column;

    public ASTNode() {
        this.line = -1;
        this.column = -1;
    }

    public ASTNode(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public void setPosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    /**
     * accept method for visitor pattern
     */
    public abstract <T> T accept(ASTVisitor<T> visitor);
}
