package de.hsbi.interpreter.symbols;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * main symbol table that holds all scopes and provides scope management
 */
public class SymbolTable {
    private Scope globalScope;
    private Scope currentScope;
    private Map<String, ClassSymbol> classes;

    public SymbolTable() {
        this.globalScope = new Scope("global", null);
        this.currentScope = globalScope;
        this.classes = new HashMap<>();
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public Scope getCurrentScope() {
        return currentScope;
    }

    /**
     * enter a new scope
     * @param name the name of the new scope
     */
    public void enterScope(String name) {
        Scope newScope = new Scope(name, currentScope);
        currentScope = newScope;
    }

    /**
     * exit the current scope and return to parent scope
     */
    public void exitScope() {
        if (currentScope.getParent() == null) {
            throw new RuntimeException("cannot exit global scope");
        }
        currentScope = currentScope.getParent();
    }

    /**
     * define a symbol in the current scope
     * @param symbol the symbol to define
     */
    public void define(Symbol symbol) {
        currentScope.define(symbol);
    }

    /**
     * resolve a symbol by name in current scope or parent scopes
     * @param name the name to look up
     * @return the symbol, or null if not found
     */
    public Symbol resolve(String name) {
        return currentScope.resolve(name);
    }

    /**
     * resolve a symbol only in the current scope
     * @param name the name to look up
     * @return the symbol, or null if not found
     */
    public Symbol resolveLocal(String name) {
        return currentScope.resolveLocal(name);
    }

    /**
     * resolve all overloaded functions by name
     * @param name the function name to look up
     * @return list of function symbols with this name
     */
    public List<FunctionSymbol> resolveOverloadedFunctions(String name) {
        return currentScope.resolveOverloadedFunctions(name);
    }

    /**
     * register a class in the symbol table
     * @param classSymbol the class to register
     */
    public void registerClass(ClassSymbol classSymbol) {
        if (classes.containsKey(classSymbol.getName())) {
            throw new RuntimeException("class '" + classSymbol.getName() + "' already defined");
        }
        classes.put(classSymbol.getName(), classSymbol);
    }

    /**
     * get a class by name
     * @param name the class name
     * @return the class symbol, or null if not found
     */
    public ClassSymbol getClass(String name) {
        return classes.get(name);
    }

    /**
     * check if a class exists
     * @param name the class name
     * @return true if class exists
     */
    public boolean hasClass(String name) {
        return classes.containsKey(name);
    }

    public Map<String, ClassSymbol> getClasses() {
        return classes;
    }
}
