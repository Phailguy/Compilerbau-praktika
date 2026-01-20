package de.hsbi.interpreter.symbols;

import de.hsbi.interpreter.ast.MethodDecl;
import de.hsbi.interpreter.ast.Parameter;
import de.hsbi.interpreter.ast.Type;

import java.util.List;

/**
 * represents a method in the symbol table
 */
public class MethodSymbol extends Symbol {
    private List<Parameter> parameters;
    private boolean isVirtual;
    private MethodDecl declaration;
    private ClassSymbol owningClass;

    public MethodSymbol(String name, Type returnType, List<Parameter> parameters, boolean isVirtual, MethodDecl declaration, ClassSymbol owningClass) {
        super(name, returnType);
        this.parameters = parameters;
        this.isVirtual = isVirtual;
        this.declaration = declaration;
        this.owningClass = owningClass;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public MethodDecl getDeclaration() {
        return declaration;
    }

    public ClassSymbol getOwningClass() {
        return owningClass;
    }

    @Override
    public SymbolKind getKind() {
        return SymbolKind.METHOD;
    }
}
