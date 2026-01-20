package de.hsbi.interpreter.ast;

import java.util.List;

/**
 * represents a function declaration
 */
public class FunctionDecl extends ASTNode {
    private Type returnType;
    private String name;
    private List<Parameter> parameters;
    private BlockStmt body;

    public FunctionDecl(Type returnType, String name, List<Parameter> parameters, BlockStmt body) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
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
        return visitor.visitFunctionDecl(this);
    }
}
