package de.hsbi.interpreter.symbols;

import de.hsbi.interpreter.ast.ClassDecl;
import de.hsbi.interpreter.ast.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * represents a class in the symbol table
 */
public class ClassSymbol extends Symbol {
    private String baseClassName;
    private ClassSymbol baseClass;
    private Map<String, VarSymbol> fields;
    private Map<String, List<MethodSymbol>> methods;  // supports method overloading
    private List<ConstructorSymbol> constructors;
    private ClassDecl declaration;

    public ClassSymbol(String name, String baseClassName, ClassDecl declaration) {
        super(name, new Type(name));
        this.baseClassName = baseClassName;
        this.baseClass = null;
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
        this.constructors = new ArrayList<>();
        this.declaration = declaration;
    }

    public String getBaseClassName() {
        return baseClassName;
    }

    public ClassSymbol getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(ClassSymbol baseClass) {
        this.baseClass = baseClass;
    }

    public Map<String, VarSymbol> getFields() {
        return fields;
    }

    public Map<String, List<MethodSymbol>> getMethods() {
        return methods;
    }

    public List<MethodSymbol> getMethodOverloads(String name) {
        List<MethodSymbol> overloads = methods.get(name);
        if (overloads != null) {
            return overloads;
        }
        return new ArrayList<>();
    }

    public List<ConstructorSymbol> getConstructors() {
        return constructors;
    }

    public ClassDecl getDeclaration() {
        return declaration;
    }

    public void addField(VarSymbol field) {
        fields.put(field.getName(), field);
    }

    public void addMethod(MethodSymbol method) {
        List<MethodSymbol> overloads = methods.computeIfAbsent(method.getName(), k -> new ArrayList<>());

        // Check for duplicate signature
        for (MethodSymbol existing : overloads) {
            if (hasSameSignature(existing, method)) {
                throw new RuntimeException("method '" + method.getName() +
                    "' with same signature already defined in class '" + getName() + "'");
            }
        }

        overloads.add(method);
    }

    private boolean hasSameSignature(MethodSymbol a, MethodSymbol b) {
        var paramsA = a.getParameters();
        var paramsB = b.getParameters();

        if (paramsA.size() != paramsB.size()) {
            return false;
        }

        for (int i = 0; i < paramsA.size(); i++) {
            var paramA = paramsA.get(i);
            var paramB = paramsB.get(i);

            if (!paramA.getType().equals(paramB.getType())) {
                return false;
            }
            if (paramA.isReference() != paramB.isReference()) {
                return false;
            }
        }

        return true;
    }

    public void addConstructor(ConstructorSymbol constructor) {
        constructors.add(constructor);
    }

    // lookup field including inherited fields
    public VarSymbol lookupField(String name) {
        VarSymbol field = fields.get(name);
        if (field != null) {
            return field;
        }
        if (baseClass != null) {
            return baseClass.lookupField(name);
        }
        return null;
    }

    // lookup method including inherited methods - returns first overload (use lookupMethodOverloads for all)
    public MethodSymbol lookupMethod(String name) {
        List<MethodSymbol> overloads = methods.get(name);
        if (overloads != null && !overloads.isEmpty()) {
            return overloads.get(0);
        }
        if (baseClass != null) {
            return baseClass.lookupMethod(name);
        }
        return null;
    }

    // lookup all method overloads including inherited methods
    public List<MethodSymbol> lookupMethodOverloads(String name) {
        List<MethodSymbol> result = new ArrayList<>();
        List<MethodSymbol> overloads = methods.get(name);
        if (overloads != null) {
            result.addAll(overloads);
        }
        if (baseClass != null) {
            result.addAll(baseClass.lookupMethodOverloads(name));
        }
        return result;
    }

    @Override
    public SymbolKind getKind() {
        return SymbolKind.CLASS;
    }
}
