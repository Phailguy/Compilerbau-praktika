package de.hsbi.interpreter;

import de.hsbi.interpreter.ast.*;
import de.hsbi.interpreter.symbols.*;
/**
 * Name resolver / scope checker
 */
public class Resolver implements ASTVisitor<Void> {

    private final SymbolTable symbolTable;
    private FunctionDecl currentFunction = null;

    public Resolver(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /* ------------------------------------------------------------
     * Entry point
     * ------------------------------------------------------------ */

    public void resolve(Program program) {
        program.accept(this);
    }

    /* ------------------------------------------------------------
     * Program / Declarations
     * ------------------------------------------------------------ */

    @Override
    public Void visitProgram(Program node) {
        //Register classes first
        for (ClassDecl cls : node.getClasses()) {
            // create ClassSymbol using the constructor that takes ClassDecl
            ClassSymbol classSymbol = new ClassSymbol(cls.getName(), cls.getBaseClass(), cls);

            // register class in symbol table
            symbolTable.registerClass(classSymbol);

            // resolve fields, methods, constructors inside class
            cls.accept(this);
        }

        //Register functions
        for (FunctionDecl func : node.getFunctions()) {
            // create FunctionSymbol and pass AST node
            FunctionSymbol funcSymbol = new FunctionSymbol(func.getName(), func.getReturnType(), func.getParameters(), func);

            // define function in global symbol table
            symbolTable.define(funcSymbol);

            // resolve function body
            func.accept(this);
        }

        return null;
    }



    @Override
    public Void visitClassDecl(ClassDecl node) {
        // Resolve fields
        for (VarDecl field : node.getFields()) {
            VarSymbol fieldSymbol = new VarSymbol(field.getName(), field.getType(), field.isReference());
            symbolTable.define(fieldSymbol);
            field.accept(this);
        }

        // Resolve constructors
        for (ConstructorDecl ctor : node.getConstructors()) {
            ctor.accept(this);
        }

        // Resolve methods
        for (MethodDecl method : node.getMethods()) {
            method.accept(this);
        }

        return null;
    }

    @Override
    public Void visitFunctionDecl(FunctionDecl node) {
        currentFunction = node;

        // Create a new scope for parameters and local variables
        symbolTable.enterScope("function_" + node.getName());

        // Define parameters in the current scope
        for (Parameter param : node.getParameters()) {
            VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
            symbolTable.define(paramSymbol);
        }

        // Resolve the function body
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        symbolTable.exitScope();
        currentFunction = null;
        return null;
    }

    @Override
    public Void visitMethodDecl(MethodDecl node) {
        // Similar to function
        symbolTable.enterScope("method_" + node.getName());

        for (Parameter param : node.getParameters()) {
            VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
            symbolTable.define(paramSymbol);
        }

        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        symbolTable.exitScope();
        return null;
    }

    @Override
    public Void visitConstructorDecl(ConstructorDecl node) {
        symbolTable.enterScope("constructor");

        for (Parameter param : node.getParameters()) {
            VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
            symbolTable.define(paramSymbol);
        }

        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        symbolTable.exitScope();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl node) {
        VarSymbol varSymbol = new VarSymbol(node.getName(), node.getType(), node.isReference());
        symbolTable.define(varSymbol);

        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }

        return null;
    }

    @Override
    public Void visitParameter(Parameter node) {
        // Parameters already declared in their owner function/method
        return null;
    }

    /* ------------------------------------------------------------
     * Statements
     * ------------------------------------------------------------ */

    @Override
    public Void visitBlockStmt(BlockStmt node) {
        symbolTable.enterScope("block");

        for (Statement stmt : node.getStatements()) {
            stmt.accept(this);
        }

        symbolTable.exitScope();
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt node) {
        node.getCondition().accept(this);
        node.getThenStmt().accept(this);
        if (node.hasElse()) {
            node.getElseStmt().accept(this);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt node) {
        node.getCondition().accept(this);
        node.getBody().accept(this);
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt node) {
        if (currentFunction == null) {
            System.err.println("Return outside of function at line " + node.getLine());
        }

        if (node.getValue() != null) {
            node.getValue().accept(this);
        }
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt node) {
        node.getExpression().accept(this);
        return null;
    }

    /* ------------------------------------------------------------
     * Expressions
     * ------------------------------------------------------------ */

    @Override
    public Void visitVarExpr(VarExpr node) {
        Symbol sym = symbolTable.resolve(node.getName());
        if (sym == null || sym.getKind() != Symbol.SymbolKind.VARIABLE) {
            System.err.println("Undefined variable: " + node.getName() + " at line " + node.getLine());
        } else {
            node.setResolvedSymbol((VarSymbol) sym);
        }
        return null;
    }

    @Override
    public Void visitAssignExpr(AssignExpr node) {
        node.getTarget().accept(this);
        node.getValue().accept(this);
        return null;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr node) {
        node.getOperand().accept(this);
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr node) {
        Symbol sym = symbolTable.resolve(node.getFunctionName());
        if (sym == null || sym.getKind() != Symbol.SymbolKind.FUNCTION) {
            System.err.println("Undefined function: " + node.getFunctionName() + " at line " + node.getLine());
        } else {
            node.setResolvedFunction((FunctionSymbol) sym);
        }

        for (Expression arg : node.getArguments()) {
            arg.accept(this);
        }

        return null;
    }

    @Override
    public Void visitMemberAccessExpr(MemberAccessExpr node) {
        node.getObject().accept(this);
        return null;
    }

    @Override
    public Void visitConstructorCallExpr(ConstructorCallExpr node) {
        for (Expression arg : node.getArguments()) {
            arg.accept(this);
        }
        return null;
    }

    @Override
    public Void visitLiteralExpr(LiteralExpr node) {
        return null;
    }

    /* ------------------------------------------------------------
     * Types
     * ------------------------------------------------------------ */

    @Override
    public Void visitType(Type node) {
        return null;
    }
}
