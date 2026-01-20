package de.hsbi.interpreter.ast;

import de.hsbi.interpreter.symbols.VarSymbol;

public class VarExpr extends Expression {
    private String name;
    private VarSymbol resolvedSymbol; // store resolved symbol

    public VarExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public VarSymbol getResolvedSymbol() {
        return resolvedSymbol;
    }

    public void setResolvedSymbol(VarSymbol resolvedSymbol) {
        this.resolvedSymbol = resolvedSymbol;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVarExpr(this);
    }
}
