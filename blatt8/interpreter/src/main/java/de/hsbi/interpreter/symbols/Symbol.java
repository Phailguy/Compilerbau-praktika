package de.hsbi.interpreter.symbols;

import de.hsbi.interpreter.ast.Type;

/**
 * base class for all symbols in the symbol table
 */
public abstract class Symbol {
    private String name;
    private Type type;

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public abstract SymbolKind getKind();

    public enum SymbolKind {
        VARIABLE, FUNCTION, CLASS, METHOD, CONSTRUCTOR
    }
}
