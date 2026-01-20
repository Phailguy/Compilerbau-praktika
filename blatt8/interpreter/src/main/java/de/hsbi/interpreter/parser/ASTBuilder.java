package de.hsbi.interpreter.parser;

import de.hsbi.interpreter.ast.*;

import java.util.ArrayList;
import java.util.List;

/**
 * converts ANTLR parse tree to our custom AST
 * extends CPPBaseVisitor which returns ASTNode for all visit methods
 */
public class ASTBuilder extends CPPBaseVisitor<ASTNode> {

    // ========================================================================
    // program and declarations
    // ========================================================================

    @Override
    public ASTNode visitProgram(CPPParser.ProgramContext ctx) {
        List<ClassDecl> classes = new ArrayList<>();
        List<FunctionDecl> functions = new ArrayList<>();

        for (CPPParser.ClassDeclContext classCtx : ctx.classDecl()) {
            classes.add((ClassDecl) visit(classCtx));
        }

        for (CPPParser.FunctionDeclContext funcCtx : ctx.functionDecl()) {
            functions.add((FunctionDecl) visit(funcCtx));
        }

        return new Program(classes, functions);
    }

    @Override
    public ASTNode visitClassDecl(CPPParser.ClassDeclContext ctx) {
        String name = ctx.IDENTIFIER(0).getText();
        String baseClass = ctx.IDENTIFIER().size() > 1 ? ctx.IDENTIFIER(1).getText() : null;

        List<VarDecl> fields = new ArrayList<>();
        List<MethodDecl> methods = new ArrayList<>();
        List<ConstructorDecl> constructors = new ArrayList<>();

        for (CPPParser.ClassMemberContext memberCtx : ctx.classMember()) {
            if (memberCtx.fieldDecl() != null) {
                fields.add((VarDecl) visit(memberCtx.fieldDecl()));
            } else if (memberCtx.methodDecl() != null) {
                methods.add((MethodDecl) visit(memberCtx.methodDecl()));
            } else if (memberCtx.constructorDecl() != null) {
                constructors.add((ConstructorDecl) visit(memberCtx.constructorDecl()));
            }
        }

        ClassDecl classDecl = new ClassDecl(name, baseClass, fields, methods, constructors);
        classDecl.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return classDecl;
    }

    @Override
    public ASTNode visitFieldDecl(CPPParser.FieldDeclContext ctx) {
        Type type = (Type) visit(ctx.type());
        String name = ctx.IDENTIFIER().getText();
        // fields cannot be references and have no initializer in our grammar
        VarDecl field = new VarDecl(type, false, name, null);
        field.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return field;
    }

    @Override
    public ASTNode visitMethodDecl(CPPParser.MethodDeclContext ctx) {
        boolean isVirtual = ctx.VIRTUAL() != null;
        Type returnType = (Type) visit(ctx.type());
        String name = ctx.IDENTIFIER().getText();
        List<Parameter> parameters = ctx.parameterList() != null
                ? getParameterList(ctx.parameterList())
                : new ArrayList<>();
        BlockStmt body = (BlockStmt) visit(ctx.block());

        MethodDecl method = new MethodDecl(isVirtual, returnType, name, parameters, body);
        method.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return method;
    }

    @Override
    public ASTNode visitConstructorDecl(CPPParser.ConstructorDeclContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        List<Parameter> parameters = ctx.parameterList() != null
                ? getParameterList(ctx.parameterList())
                : new ArrayList<>();
        BlockStmt body = (BlockStmt) visit(ctx.block());

        ConstructorDecl constructor = new ConstructorDecl(name, parameters, body);
        constructor.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return constructor;
    }

    @Override
    public ASTNode visitFunctionDecl(CPPParser.FunctionDeclContext ctx) {
        Type returnType = (Type) visit(ctx.type());
        String name = ctx.IDENTIFIER().getText();
        List<Parameter> parameters = ctx.parameterList() != null
                ? getParameterList(ctx.parameterList())
                : new ArrayList<>();
        BlockStmt body = (BlockStmt) visit(ctx.block());

        FunctionDecl func = new FunctionDecl(returnType, name, parameters, body);
        func.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return func;
    }

    // helper method to extract parameter list
    private List<Parameter> getParameterList(CPPParser.ParameterListContext ctx) {
        List<Parameter> parameters = new ArrayList<>();
        for (CPPParser.ParameterContext paramCtx : ctx.parameter()) {
            parameters.add((Parameter) visit(paramCtx));
        }
        return parameters;
    }

    @Override
    public ASTNode visitParameter(CPPParser.ParameterContext ctx) {
        Type type = (Type) visit(ctx.type());
        boolean isReference = ctx.AMPERSAND() != null;
        String name = ctx.IDENTIFIER().getText();
        return new Parameter(type, isReference, name);
    }

    @Override
    public ASTNode visitType(CPPParser.TypeContext ctx) {
        if (ctx.BOOL() != null) {
            return new Type(Type.BaseType.BOOL);
        } else if (ctx.INT() != null) {
            return new Type(Type.BaseType.INT);
        } else if (ctx.CHAR() != null) {
            return new Type(Type.BaseType.CHAR);
        } else if (ctx.STRING() != null) {
            return new Type(Type.BaseType.STRING);
        } else if (ctx.VOID() != null) {
            return new Type(Type.BaseType.VOID);
        } else if (ctx.IDENTIFIER() != null) {
            return new Type(ctx.IDENTIFIER().getText()); // class type
        }
        throw new RuntimeException("Unknown type");
    }

    // ========================================================================
    // statements
    // ========================================================================

    @Override
    public ASTNode visitBlock(CPPParser.BlockContext ctx) {
        List<Statement> statements = new ArrayList<>();
        for (CPPParser.StatementContext stmtCtx : ctx.statement()) {
            ASTNode node = visit(stmtCtx);
            // VarDecl is also a valid statement in our context
            statements.add((Statement) node);
        }
        return new BlockStmt(statements);
    }

    @Override
    public ASTNode visitStatement(CPPParser.StatementContext ctx) {
        if (ctx.block() != null) {
            return visit(ctx.block());
        } else if (ctx.varDecl() != null) {
            return visit(ctx.varDecl());
        } else if (ctx.ifStmt() != null) {
            return visit(ctx.ifStmt());
        } else if (ctx.whileStmt() != null) {
            return visit(ctx.whileStmt());
        } else if (ctx.returnStmt() != null) {
            return visit(ctx.returnStmt());
        } else if (ctx.exprStmt() != null) {
            return visit(ctx.exprStmt());
        }
        throw new RuntimeException("Unknown statement type");
    }

    @Override
    public ASTNode visitVarDecl(CPPParser.VarDeclContext ctx) {
        Type type = (Type) visit(ctx.type());
        boolean isReference = ctx.AMPERSAND() != null;
        String name = ctx.IDENTIFIER().getText();
        Expression initializer = ctx.expression() != null
                ? (Expression) visit(ctx.expression())
                : null;

        VarDecl varDecl = new VarDecl(type, isReference, name, initializer);
        varDecl.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return varDecl;
    }

    @Override
    public ASTNode visitIfStmt(CPPParser.IfStmtContext ctx) {
        Expression condition = (Expression) visit(ctx.expression());
        Statement thenStmt = (Statement) visit(ctx.statement(0));
        Statement elseStmt = ctx.statement().size() > 1
                ? (Statement) visit(ctx.statement(1))
                : null;

        return new IfStmt(condition, thenStmt, elseStmt);
    }

    @Override
    public ASTNode visitWhileStmt(CPPParser.WhileStmtContext ctx) {
        Expression condition = (Expression) visit(ctx.expression());
        Statement body = (Statement) visit(ctx.statement());
        return new WhileStmt(condition, body);
    }

    @Override
    public ASTNode visitReturnStmt(CPPParser.ReturnStmtContext ctx) {
        Expression value = ctx.expression() != null
                ? (Expression) visit(ctx.expression())
                : null;
        ReturnStmt stmt = new ReturnStmt(value);
        stmt.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return stmt;
    }

    @Override
    public ASTNode visitExprStmt(CPPParser.ExprStmtContext ctx) {
        Expression expr = (Expression) visit(ctx.expression());
        return new ExprStmt(expr);
    }

    // ========================================================================
    // expressions
    // ========================================================================

    @Override
    public ASTNode visitExpression(CPPParser.ExpressionContext ctx) {
        return visit(ctx.assignmentExpr());
    }

    @Override
    public ASTNode visitAssignmentExpr(CPPParser.AssignmentExprContext ctx) {
        Expression left = (Expression) visit(ctx.logicalOrExpr());

        if (ctx.assignmentExpr() != null) {
            Expression right = (Expression) visit(ctx.assignmentExpr());
            AssignExpr assign = new AssignExpr(left, right);
            assign.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            return assign;
        }

        return left;
    }

    @Override
    public ASTNode visitLogicalOrExpr(CPPParser.LogicalOrExprContext ctx) {
        Expression expr = (Expression) visit(ctx.logicalAndExpr(0));

        for (int i = 1; i < ctx.logicalAndExpr().size(); i++) {
            Expression right = (Expression) visit(ctx.logicalAndExpr(i));
            expr = new BinaryExpr(BinaryExpr.Operator.OR, expr, right);
        }

        return expr;
    }

    @Override
    public ASTNode visitLogicalAndExpr(CPPParser.LogicalAndExprContext ctx) {
        Expression expr = (Expression) visit(ctx.equalityExpr(0));

        for (int i = 1; i < ctx.equalityExpr().size(); i++) {
            Expression right = (Expression) visit(ctx.equalityExpr(i));
            expr = new BinaryExpr(BinaryExpr.Operator.AND, expr, right);
        }

        return expr;
    }

    @Override
    public ASTNode visitEqualityExpr(CPPParser.EqualityExprContext ctx) {
        Expression expr = (Expression) visit(ctx.relationalExpr(0));

        for (int i = 1; i < ctx.relationalExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Expression right = (Expression) visit(ctx.relationalExpr(i));

            BinaryExpr.Operator operator = op.equals("==")
                    ? BinaryExpr.Operator.EQ
                    : BinaryExpr.Operator.NEQ;

            expr = new BinaryExpr(operator, expr, right);
        }

        return expr;
    }

    @Override
    public ASTNode visitRelationalExpr(CPPParser.RelationalExprContext ctx) {
        Expression expr = (Expression) visit(ctx.additiveExpr(0));

        for (int i = 1; i < ctx.additiveExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Expression right = (Expression) visit(ctx.additiveExpr(i));

            BinaryExpr.Operator operator;
            switch (op) {
                case "<": operator = BinaryExpr.Operator.LT; break;
                case "<=": operator = BinaryExpr.Operator.LEQ; break;
                case ">": operator = BinaryExpr.Operator.GT; break;
                case ">=": operator = BinaryExpr.Operator.GEQ; break;
                default: throw new RuntimeException("Unknown relational operator: " + op);
            }

            expr = new BinaryExpr(operator, expr, right);
        }

        return expr;
    }

    @Override
    public ASTNode visitAdditiveExpr(CPPParser.AdditiveExprContext ctx) {
        Expression expr = (Expression) visit(ctx.multiplicativeExpr(0));

        for (int i = 1; i < ctx.multiplicativeExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Expression right = (Expression) visit(ctx.multiplicativeExpr(i));

            BinaryExpr.Operator operator = op.equals("+")
                    ? BinaryExpr.Operator.PLUS
                    : BinaryExpr.Operator.MINUS;

            expr = new BinaryExpr(operator, expr, right);
        }

        return expr;
    }

    @Override
    public ASTNode visitMultiplicativeExpr(CPPParser.MultiplicativeExprContext ctx) {
        Expression expr = (Expression) visit(ctx.unaryExpr(0));

        for (int i = 1; i < ctx.unaryExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Expression right = (Expression) visit(ctx.unaryExpr(i));

            BinaryExpr.Operator operator;
            switch (op) {
                case "*": operator = BinaryExpr.Operator.MULT; break;
                case "/": operator = BinaryExpr.Operator.DIV; break;
                case "%": operator = BinaryExpr.Operator.MOD; break;
                default: throw new RuntimeException("Unknown multiplicative operator: " + op);
            }

            expr = new BinaryExpr(operator, expr, right);
        }

        return expr;
    }

    @Override
    public ASTNode visitUnaryExpr(CPPParser.UnaryExprContext ctx) {
        if (ctx.postfixExpr() != null) {
            return visit(ctx.postfixExpr());
        }

        // unary operator
        String op = ctx.getChild(0).getText();
        Expression operand = (Expression) visit(ctx.unaryExpr());

        UnaryExpr.Operator operator;
        switch (op) {
            case "+": operator = UnaryExpr.Operator.PLUS; break;
            case "-": operator = UnaryExpr.Operator.MINUS; break;
            case "!": operator = UnaryExpr.Operator.NOT; break;
            default: throw new RuntimeException("Unknown unary operator: " + op);
        }

        return new UnaryExpr(operator, operand);
    }

    @Override
    public ASTNode visitPostfixExpr(CPPParser.PostfixExprContext ctx) {
        Expression expr = (Expression) visit(ctx.primaryExpr());

        for (CPPParser.PostfixOpContext opCtx : ctx.postfixOp()) {
            if (opCtx.DOT() != null && opCtx.IDENTIFIER() != null) {
                String memberName = opCtx.IDENTIFIER().getText();

                // check if it's a method call (has LPAREN)
                if (opCtx.LPAREN() != null) {
                    // method call: obj.method(args)
                    List<Expression> arguments = opCtx.argumentList() != null
                            ? getArgumentList(opCtx.argumentList())
                            : new ArrayList<>();
                    MemberAccessExpr memberExpr = new MemberAccessExpr(expr, memberName, arguments);
                    memberExpr.setPosition(opCtx.getStart().getLine(), opCtx.getStart().getCharPositionInLine());
                    expr = memberExpr;
                } else {
                    // field access: obj.field
                    MemberAccessExpr memberExpr = new MemberAccessExpr(expr, memberName);
                    memberExpr.setPosition(opCtx.getStart().getLine(), opCtx.getStart().getCharPositionInLine());
                    expr = memberExpr;
                }
            } else if (opCtx.LPAREN() != null) {
                // direct function call: expr(args)
                List<Expression> arguments = opCtx.argumentList() != null
                        ? getArgumentList(opCtx.argumentList())
                        : new ArrayList<>();

                if (expr instanceof VarExpr) {
                    // function call: func(args)
                    String name = ((VarExpr) expr).getName();
                    ConstructorCallExpr call = new ConstructorCallExpr(name, arguments);
                    call.setPosition(expr.getLine(), expr.getColumn());
                    expr = call;
                } else if (expr instanceof MemberAccessExpr) {
                    // method call via postfix: obj.method followed by (args)
                    // convert to method call
                    MemberAccessExpr memberAccess = (MemberAccessExpr) expr;
                    MemberAccessExpr methodCall = new MemberAccessExpr(memberAccess.getObject(), memberAccess.getMemberName(), arguments);
                    methodCall.setPosition(memberAccess.getLine(), memberAccess.getColumn());
                    expr = methodCall;
                } else {
                    // This shouldn't happen with our grammar, but handle it anyway
                    throw new RuntimeException("Cannot call non-identifier expression as function");
                }
            }
        }

        return expr;
    }

    @Override
    public ASTNode visitPrimaryExpr(CPPParser.PrimaryExprContext ctx) {
        if (ctx.literal() != null) {
            return visit(ctx.literal());
        } else if (ctx.IDENTIFIER() != null && ctx.LPAREN() != null) {
            // constructor call or function call: ID(args)
            String name = ctx.IDENTIFIER().getText();
            List<Expression> arguments = ctx.argumentList() != null
                    ? getArgumentList(ctx.argumentList())
                    : new ArrayList<>();

            // we treat this as constructor call for now
            // semantic analysis will determine if it's actually a function call
            ConstructorCallExpr call = new ConstructorCallExpr(name, arguments);
            call.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            return call;
        } else if (ctx.IDENTIFIER() != null) {
            // variable reference
            VarExpr var = new VarExpr(ctx.IDENTIFIER().getText());
            var.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            return var;
        } else if (ctx.LPAREN() != null) {
            // parenthesized expression
            return visit(ctx.expression());
        }

        throw new RuntimeException("Unknown primary expression");
    }

    // helper method to extract argument list
    private List<Expression> getArgumentList(CPPParser.ArgumentListContext ctx) {
        List<Expression> arguments = new ArrayList<>();
        for (CPPParser.ExpressionContext exprCtx : ctx.expression()) {
            arguments.add((Expression) visit(exprCtx));
        }
        return arguments;
    }

    @Override
    public ASTNode visitLiteral(CPPParser.LiteralContext ctx) {
        if (ctx.INT_LITERAL() != null) {
            int value = Integer.parseInt(ctx.INT_LITERAL().getText());
            return new LiteralExpr(LiteralExpr.LiteralType.INT, value);
        } else if (ctx.BOOL_LITERAL() != null) {
            boolean value = ctx.BOOL_LITERAL().getText().equals("true");
            return new LiteralExpr(LiteralExpr.LiteralType.BOOL, value);
        } else if (ctx.CHAR_LITERAL() != null) {
            String text = ctx.CHAR_LITERAL().getText();
            char value = parseCharLiteral(text);
            return new LiteralExpr(LiteralExpr.LiteralType.CHAR, value);
        } else if (ctx.STRING_LITERAL() != null) {
            String text = ctx.STRING_LITERAL().getText();
            String value = parseStringLiteral(text);
            return new LiteralExpr(LiteralExpr.LiteralType.STRING, value);
        }

        throw new RuntimeException("Unknown literal type");
    }

    // ========================================================================
    // helper methods for parsing literals with escape sequences
    // ========================================================================

    private char parseCharLiteral(String text) {
        // remove surrounding quotes
        String content = text.substring(1, text.length() - 1);
        return parseEscapeSequence(content).charAt(0);
    }

    private String parseStringLiteral(String text) {
        // remove surrounding quotes
        String content = text.substring(1, text.length() - 1);
        return parseEscapeSequence(content);
    }

    private String parseEscapeSequence(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                char next = text.charAt(i + 1);
                switch (next) {
                    case '0': result.append('\0'); break;
                    case 'n': result.append('\n'); break;
                    case 'r': result.append('\r'); break;
                    case 't': result.append('\t'); break;
                    case '\\': result.append('\\'); break;
                    case '\'': result.append('\''); break;
                    case '"': result.append('"'); break;
                    default: result.append(next); break;
                }
                i++; // skip next character
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
