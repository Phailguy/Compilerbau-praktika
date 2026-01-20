package de.hsbi.interpreter.symbols;

import de.hsbi.interpreter.ast.FunctionDecl;
import de.hsbi.interpreter.ast.Parameter;
import de.hsbi.interpreter.ast.Type;

import java.util.List;

/**
 * represents a function in the symbol table
 */
public class FunctionSymbol extends Symbol {
    private List<Parameter> parameters;
    private FunctionDecl declaration;

    public FunctionSymbol(String name, Type returnType, List<Parameter> parameters, FunctionDecl declaration) {
        super(name, returnType);
        this.parameters = parameters;
        this.declaration = declaration;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public FunctionDecl getDeclaration() {
        return declaration;
    }

    @Override
    public SymbolKind getKind() {
        return SymbolKind.FUNCTION;
    }
}
