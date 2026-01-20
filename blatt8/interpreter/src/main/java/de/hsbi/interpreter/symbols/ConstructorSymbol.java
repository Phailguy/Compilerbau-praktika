package de.hsbi.interpreter.symbols;

import de.hsbi.interpreter.ast.ConstructorDecl;
import de.hsbi.interpreter.ast.Parameter;
import de.hsbi.interpreter.ast.Type;

import java.util.List;

/**
 * represents a constructor in the symbol table
 */
public class ConstructorSymbol extends Symbol {
    // Marker for implicit copy constructor
    public static final ConstructorSymbol IMPLICIT_COPY = new ConstructorSymbol("__implicit_copy__", null, null, null);

    private List<Parameter> parameters;
    private ConstructorDecl declaration;
    private ClassSymbol owningClass;

    public ConstructorSymbol(String name, List<Parameter> parameters, ConstructorDecl declaration, ClassSymbol owningClass) {
        super(name, new Type(Type.BaseType.VOID)); // constructors don't have a return type
        this.parameters = parameters;
        this.declaration = declaration;
        this.owningClass = owningClass;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public ConstructorDecl getDeclaration() {
        return declaration;
    }

    public ClassSymbol getOwningClass() {
        return owningClass;
    }

    @Override
    public SymbolKind getKind() {
        return SymbolKind.CONSTRUCTOR;
    }
}
