package de.hsbi.interpreter.symbols;

import de.hsbi.interpreter.ast.Type;

/**
 * represents a variable in the symbol table
 */
public class VarSymbol extends Symbol {
    private boolean isReference;

    public VarSymbol(String name, Type type, boolean isReference) {
        super(name, type);
        this.isReference = isReference;
    }

    public boolean isReference() {
        return isReference;
    }

    @Override
    public SymbolKind getKind() {
        return SymbolKind.VARIABLE;
    }
}
