package de.hsbi.interpreter.ast;

import de.hsbi.interpreter.symbols.FunctionSymbol;

import java.util.List;

/**
 * represents a constructor call (e.g., Dog("Bello"))
 * Also used for function calls (resolved during semantic analysis)
 */
public class ConstructorCallExpr extends Expression {
    private String className;
    private List<Expression> arguments;
    private FunctionSymbol resolvedFunction; // set if this is actually a function call
    private boolean implicitCopy; // set if this is an implicit copy constructor call

    public ConstructorCallExpr(String className, List<Expression> arguments) {
        this.className = className;
        this.arguments = arguments;
        this.implicitCopy = false;
    }

    public String getClassName() {
        return className;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public FunctionSymbol getResolvedFunction() {
        return resolvedFunction;
    }

    public void setResolvedFunction(FunctionSymbol resolvedFunction) {
        this.resolvedFunction = resolvedFunction;
    }

    public boolean isFunctionCall() {
        return resolvedFunction != null;
    }

    public boolean isImplicitCopy() {
        return implicitCopy;
    }

    public void setImplicitCopy(boolean implicitCopy) {
        this.implicitCopy = implicitCopy;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitConstructorCallExpr(this);
    }
}
