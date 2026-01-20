package de.hsbi.interpreter.ast;

import java.util.List;

/**
 * represents a constructor declaration
 */
public class ConstructorDecl extends ASTNode {
    private String name; // same as class name
    private List<Parameter> parameters;
    private BlockStmt body;

    public ConstructorDecl(String name, List<Parameter> parameters, BlockStmt body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public BlockStmt getBody() {
        return body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitConstructorDecl(this);
    }
}
