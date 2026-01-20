package de.hsbi.interpreter.ast;

import java.util.List;

/**
 * represents a method declaration (function inside a class)
 */
public class MethodDecl extends ASTNode {
    private boolean isVirtual;
    private Type returnType;
    private String name;
    private List<Parameter> parameters;
    private BlockStmt body;

    public MethodDecl(boolean isVirtual, Type returnType, String name,
                      List<Parameter> parameters, BlockStmt body) {
        this.isVirtual = isVirtual;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public Type getReturnType() {
        return returnType;
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
        return visitor.visitMethodDecl(this);
    }
}
