package de.hsbi.interpreter.ast;

import de.hsbi.interpreter.symbols.FunctionSymbol;
import java.util.List;

public class CallExpr extends Expression {
    private String functionName;
    private List<Expression> arguments;
    private FunctionSymbol resolvedFunction; // store resolved symbol

    public CallExpr(String functionName, List<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
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

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitCallExpr(this);
    }
}
