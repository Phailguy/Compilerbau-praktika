package de.hsbi.interpreter.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * represents a scope (context) in which symbols are defined
 * scopes can be nested (e.g., global scope contains function scopes, which contain block scopes)
 */
public class Scope {
    private String name;
    private Scope parent;
    private Map<String, Symbol> symbols;
    private Map<String, List<FunctionSymbol>> overloadedFunctions;

    public Scope(String name, Scope parent) {
        this.name = name;
        this.parent = parent;
        this.symbols = new HashMap<>();
        this.overloadedFunctions = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Scope getParent() {
        return parent;
    }

    /**
     * define a symbol in this scope
     * @param symbol the symbol to define
     * @throws RuntimeException if symbol already exists in this scope (except for function overloading)
     */
    public void define(Symbol symbol) {
        // Special handling for function overloading
        if (symbol instanceof FunctionSymbol) {
            FunctionSymbol funcSymbol = (FunctionSymbol) symbol;
            List<FunctionSymbol> overloads = overloadedFunctions.computeIfAbsent(
                symbol.getName(), k -> new ArrayList<>());

            // Check for duplicate signature
            for (FunctionSymbol existing : overloads) {
                if (hasSameSignature(existing, funcSymbol)) {
                    throw new RuntimeException("function '" + symbol.getName() +
                        "' with same signature already defined in scope '" + name + "'");
                }
            }

            overloads.add(funcSymbol);
            // Also store in symbols map (first definition or overwrite for lookup)
            symbols.put(symbol.getName(), symbol);
            return;
        }

        if (symbols.containsKey(symbol.getName())) {
            throw new RuntimeException("symbol '" + symbol.getName() + "' already defined in scope '" + name + "'");
        }
        symbols.put(symbol.getName(), symbol);
    }

    /**
     * Check if two function symbols have the same signature
     * (same parameter count, types, and reference status)
     */
    private boolean hasSameSignature(FunctionSymbol a, FunctionSymbol b) {
        var paramsA = a.getParameters();
        var paramsB = b.getParameters();

        if (paramsA.size() != paramsB.size()) {
            return false;
        }

        for (int i = 0; i < paramsA.size(); i++) {
            var paramA = paramsA.get(i);
            var paramB = paramsB.get(i);

            // Check type match
            if (!paramA.getType().equals(paramB.getType())) {
                return false;
            }

            // Check reference status match
            if (paramA.isReference() != paramB.isReference()) {
                return false;
            }
        }

        return true;
    }

    /**
     * resolve a symbol by name in this scope or parent scopes
     * @param name the name to look up
     * @return the symbol, or null if not found
     */
    public Symbol resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        if (parent != null) {
            return parent.resolve(name);
        }
        return null;
    }

    /**
     * resolve all overloaded functions by name in this scope or parent scopes
     * @param name the function name to look up
     * @return list of function symbols with this name, or empty list if not found
     */
    public List<FunctionSymbol> resolveOverloadedFunctions(String name) {
        List<FunctionSymbol> result = new ArrayList<>();

        // Check this scope
        List<FunctionSymbol> local = overloadedFunctions.get(name);
        if (local != null) {
            result.addAll(local);
        }

        // Check parent scopes
        if (parent != null) {
            result.addAll(parent.resolveOverloadedFunctions(name));
        }

        return result;
    }

    /**
     * resolve a symbol only in this scope (not parent scopes)
     * @param name the name to look up
     * @return the symbol, or null if not found
     */
    public Symbol resolveLocal(String name) {
        return symbols.get(name);
    }

    /**
     * check if a symbol exists in this scope (not parent scopes)
     * @param name the name to check
     * @return true if symbol exists in this scope
     */
    public boolean isDefined(String name) {
        return symbols.containsKey(name);
    }

    /**
     * remove a symbol from this scope
     * @param name the name of the symbol to remove
     */
    public void remove(String name) {
        symbols.remove(name);
        overloadedFunctions.remove(name);
    }

    @Override
    public String toString() {
        return "Scope{" + name + ", symbols=" + symbols.keySet() + "}";
    }
}
