package de.hsbi.interpreter.symbols;

import de.hsbi.interpreter.ast.*;

import java.util.ArrayList;
import java.util.List;

/**
 * builds the symbol table from the AST
 * performs two passes:
 * 1. first pass: register all classes and functions (to allow forward references)
 * 2. second pass: process class members and function bodies
 */
public class SymbolTableBuilder implements ASTVisitor<Void> {
    private SymbolTable symbolTable;
    private ClassSymbol currentClass;
    private boolean firstPass;
    private List<String> errors;

    public SymbolTableBuilder() {
        this.symbolTable = new SymbolTable();
        this.currentClass = null;
        this.firstPass = true;
        this.errors = new ArrayList<>();
    }

    /**
     * Constructor for incremental building (e.g., REPL mode)
     * @param existingSymbolTable the existing symbol table to extend
     */
    public SymbolTableBuilder(SymbolTable existingSymbolTable) {
        this.symbolTable = existingSymbolTable;
        this.currentClass = null;
        this.firstPass = true;
        this.errors = new ArrayList<>();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    private void error(String message) {
        errors.add(message);
    }

    /**
     * build the symbol table from a program (performs two passes)
     * @param program the AST root node
     * @return the populated symbol table
     */
    public SymbolTable build(Program program) {
        // first pass: register all classes and functions
        firstPass = true;
        visitProgram(program);

        // resolve base classes
        for (ClassSymbol classSymbol : symbolTable.getClasses().values()) {
            if (classSymbol.getBaseClassName() != null) {
                ClassSymbol baseClass = symbolTable.getClass(classSymbol.getBaseClassName());
                if (baseClass == null) {
                    throw new RuntimeException("base class '" + classSymbol.getBaseClassName() + "' not found for class '" + classSymbol.getName() + "'");
                }
                classSymbol.setBaseClass(baseClass);
            }
        }

        // second pass: process class members and function bodies
        firstPass = false;
        visitProgram(program);

        return symbolTable;
    }

    @Override
    public Void visitProgram(Program node) {
        // visit all classes
        for (ClassDecl classDecl : node.getClasses()) {
            classDecl.accept(this);
        }

        // visit all functions
        for (FunctionDecl functionDecl : node.getFunctions()) {
            functionDecl.accept(this);
        }

        return null;
    }

    @Override
    public Void visitClassDecl(ClassDecl node) {
        if (firstPass) {
            // first pass: register the class
            ClassSymbol classSymbol = new ClassSymbol(node.getName(), node.getBaseClass(), node);
            symbolTable.registerClass(classSymbol);
        } else {
            // second pass: process class members
            currentClass = symbolTable.getClass(node.getName());

            // process fields
            for (VarDecl field : node.getFields()) {
                VarSymbol fieldSymbol = new VarSymbol(field.getName(), field.getType(), field.isReference());
                currentClass.addField(fieldSymbol);
            }

            // process methods
            for (MethodDecl method : node.getMethods()) {
                MethodSymbol methodSymbol = new MethodSymbol(
                    method.getName(),
                    method.getReturnType(),
                    method.getParameters(),
                    method.isVirtual(),
                    method,
                    currentClass
                );
                try {
                    currentClass.addMethod(methodSymbol);
                } catch (RuntimeException e) {
                    // Build signature string for error message
                    StringBuilder sig = new StringBuilder(method.getName()).append("(");
                    for (int i = 0; i < method.getParameters().size(); i++) {
                        if (i > 0) sig.append(", ");
                        Parameter p = method.getParameters().get(i);
                        sig.append(p.getType().toString());
                        if (p.isReference()) sig.append("&");
                    }
                    sig.append(")");

                    String lineInfo = method.getLine() > 0 ? " (line " + method.getLine() + ")" : "";
                    error("method '" + currentClass.getName() + "::" + sig + "' is already defined" + lineInfo);
                }
            }

            // process constructors
            for (ConstructorDecl constructor : node.getConstructors()) {
                ConstructorSymbol constructorSymbol = new ConstructorSymbol(
                    constructor.getName(),
                    constructor.getParameters(),
                    constructor,
                    currentClass
                );
                currentClass.addConstructor(constructorSymbol);
            }

            currentClass = null;
        }

        return null;
    }

    @Override
    public Void visitFunctionDecl(FunctionDecl node) {
        if (firstPass) {
            // first pass: register the function
            FunctionSymbol functionSymbol = new FunctionSymbol(
                node.getName(),
                node.getReturnType(),
                node.getParameters(),
                node
            );
            try {
                symbolTable.getGlobalScope().define(functionSymbol);
            } catch (RuntimeException e) {
                // Build signature string for error message
                StringBuilder sig = new StringBuilder(node.getName()).append("(");
                for (int i = 0; i < node.getParameters().size(); i++) {
                    if (i > 0) sig.append(", ");
                    Parameter p = node.getParameters().get(i);
                    sig.append(p.getType().toString());
                    if (p.isReference()) sig.append("&");
                }
                sig.append(")");

                String lineInfo = node.getLine() > 0 ? " (line " + node.getLine() + ")" : "";
                error("function '" + sig + "' is already defined" + lineInfo);
            }
        } else {
            // second pass: process function body
            symbolTable.enterScope("function:" + node.getName());

            // define parameters
            for (Parameter param : node.getParameters()) {
                VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
                symbolTable.define(paramSymbol);
            }

            // visit body
            if (node.getBody() != null) {
                node.getBody().accept(this);
            }

            symbolTable.exitScope();
        }

        return null;
    }

    @Override
    public Void visitMethodDecl(MethodDecl node) {
        // methods are handled in visitClassDecl
        return null;
    }

    @Override
    public Void visitConstructorDecl(ConstructorDecl node) {
        // constructors are handled in visitClassDecl
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl node) {
        if (!firstPass) {
            // Check if variable already exists in current scope
            if (symbolTable.getCurrentScope().isDefined(node.getName())) {
                String lineInfo = node.getLine() > 0 ? " (line " + node.getLine() + ")" : "";
                error("variable '" + node.getName() + "' is already defined in this scope" + lineInfo);
            } else {
                VarSymbol varSymbol = new VarSymbol(node.getName(), node.getType(), node.isReference());
                symbolTable.define(varSymbol);
            }

            // visit initializer if present
            if (node.getInitializer() != null) {
                node.getInitializer().accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visitParameter(Parameter node) {
        // parameters are handled in visitFunctionDecl
        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt node) {
        if (!firstPass) {
            symbolTable.enterScope("block");

            for (Statement stmt : node.getStatements()) {
                stmt.accept(this);
            }

            symbolTable.exitScope();
        }
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt node) {
        if (!firstPass) {
            node.getCondition().accept(this);
            node.getThenStmt().accept(this);
            if (node.getElseStmt() != null) {
                node.getElseStmt().accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt node) {
        if (!firstPass) {
            node.getCondition().accept(this);
            node.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt node) {
        if (!firstPass) {
            if (node.getValue() != null) {
                node.getValue().accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt node) {
        if (!firstPass) {
            node.getExpression().accept(this);
        }
        return null;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr node) {
        if (!firstPass) {
            node.getLeft().accept(this);
            node.getRight().accept(this);
        }
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr node) {
        if (!firstPass) {
            node.getOperand().accept(this);
        }
        return null;
    }

    @Override
    public Void visitAssignExpr(AssignExpr node) {
        if (!firstPass) {
            node.getTarget().accept(this);
            node.getValue().accept(this);
        }
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr node) {
        // variable references don't define symbols
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr node) {
        if (!firstPass) {
            for (Expression arg : node.getArguments()) {
                arg.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visitMemberAccessExpr(MemberAccessExpr node) {
        if (!firstPass) {
            node.getObject().accept(this);
            if (node.isMethodCall()) {
                for (Expression arg : node.getArguments()) {
                    arg.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visitConstructorCallExpr(ConstructorCallExpr node) {
        if (!firstPass) {
            for (Expression arg : node.getArguments()) {
                arg.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visitLiteralExpr(LiteralExpr node) {
        // literals don't define symbols
        return null;
    }

    @Override
    public Void visitType(Type node) {
        // types don't define symbols
        return null;
    }
}
