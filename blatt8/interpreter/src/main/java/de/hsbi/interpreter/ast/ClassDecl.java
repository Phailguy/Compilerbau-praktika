package de.hsbi.interpreter.ast;

import java.util.List;

/**
 * represents a class declaration
 */
public class ClassDecl extends ASTNode {
    private String name;
    private String baseClass; // can be null
    private List<VarDecl> fields;
    private List<MethodDecl> methods;
    private List<ConstructorDecl> constructors;

    public ClassDecl(String name, String baseClass, List<VarDecl> fields,
                     List<MethodDecl> methods, List<ConstructorDecl> constructors) {
        this.name = name;
        this.baseClass = baseClass;
        this.fields = fields;
        this.methods = methods;
        this.constructors = constructors;
    }

    public String getName() {
        return name;
    }

    public String getBaseClass() {
        return baseClass;
    }

    public boolean hasBaseClass() {
        return baseClass != null;
    }

    public List<VarDecl> getFields() {
        return fields;
    }

    public List<MethodDecl> getMethods() {
        return methods;
    }

    public List<ConstructorDecl> getConstructors() {
        return constructors;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitClassDecl(this);
    }
}
