package de.hsbi.interpreter.ast;

import java.util.List;

/**
 * root node of the AST - represents the entire program
 */
public class Program extends ASTNode {
    private List<ClassDecl> classes;
    private List<FunctionDecl> functions;

    public Program(List<ClassDecl> classes, List<FunctionDecl> functions) {
        this.classes = classes;
        this.functions = functions;
    }

    public List<ClassDecl> getClasses() {
        return classes;
    }

    public List<FunctionDecl> getFunctions() {
        return functions;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitProgram(this);
    }
}
