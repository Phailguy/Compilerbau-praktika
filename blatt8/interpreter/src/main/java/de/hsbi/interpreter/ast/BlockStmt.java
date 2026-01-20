package de.hsbi.interpreter.ast;

import java.util.List;

/**
 * represents a block of statements { ... }
 */
public class BlockStmt extends Statement {
    private List<Statement> statements;

    public BlockStmt(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBlockStmt(this);
    }
}
