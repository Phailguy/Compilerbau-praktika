package de.hsbi.interpreter.semantic;

import de.hsbi.interpreter.ast.*;
import de.hsbi.interpreter.symbols.*;

import java.util.ArrayList;
import java.util.List;

/**
 * performs semantic analysis on the AST
 * - type checking
 * - lvalue checking
 * - function overload resolution
 * - virtual method validation
 */
public class SemanticAnalyzer implements ASTVisitor<Type> {
    private SymbolTable symbolTable;
    private ClassSymbol currentClass;
    private Type currentFunctionReturnType;
    private List<String> errors;

    public SemanticAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.currentClass = null;
        this.currentFunctionReturnType = null;
        this.errors = new ArrayList<>();
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

    private void error(String message, ASTNode node) {
        if (node != null && node.getLine() > 0) {
            errors.add(message + " (line " + node.getLine() + ")");
        } else {
            errors.add(message);
        }
    }

    /**
     * analyze the program
     * @param program the AST root
     * @return true if no errors were found
     */
    public boolean analyze(Program program) {
        visitProgram(program);
        return !hasErrors();
    }

    @Override
    public Type visitProgram(Program node) {
        // analyze all functions
        for (FunctionDecl func : node.getFunctions()) {
            func.accept(this);
        }

        // analyze all classes
        for (ClassDecl cls : node.getClasses()) {
            cls.accept(this);
        }

        return null;
    }

    @Override
    public Type visitClassDecl(ClassDecl node) {
        currentClass = symbolTable.getClass(node.getName());

        // check for inheritance cycles
        if (hasInheritanceCycle(currentClass)) {
            error("class '" + node.getName() + "' has cyclic inheritance", node);
        }

        // analyze fields
        for (VarDecl field : node.getFields()) {
            // check that field is not a reference
            if (field.isReference()) {
                error("class '" + node.getName() + "': field '" + field.getName() + "' cannot be a reference", field);
            }
            field.accept(this);
        }

        // analyze methods
        for (MethodDecl method : node.getMethods()) {
            method.accept(this);
        }

        // analyze constructors
        for (ConstructorDecl constructor : node.getConstructors()) {
            constructor.accept(this);
        }

        // check virtual method overrides
        checkVirtualMethodOverrides(currentClass);

        currentClass = null;
        return null;
    }

    @Override
    public Type visitFunctionDecl(FunctionDecl node) {
        currentFunctionReturnType = node.getReturnType();

        symbolTable.enterScope("function:" + node.getName());

        // define parameters in function scope
        for (Parameter param : node.getParameters()) {
            VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
            symbolTable.define(paramSymbol);
            param.accept(this);
        }

        // analyze body
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        symbolTable.exitScope();
        currentFunctionReturnType = null;
        return null;
    }

    @Override
    public Type visitMethodDecl(MethodDecl node) {
        currentFunctionReturnType = node.getReturnType();

        symbolTable.enterScope("method:" + node.getName());

        // First, define parameters in method scope (they take precedence over fields)
        for (Parameter param : node.getParameters()) {
            VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
            symbolTable.define(paramSymbol);
            param.accept(this);
        }

        // Then define class fields in method scope (including inherited fields)
        // but only if not already shadowed by a parameter
        if (currentClass != null) {
            defineClassFieldsInScope(currentClass);
        }

        // analyze body
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        symbolTable.exitScope();
        currentFunctionReturnType = null;
        return null;
    }

    @Override
    public Type visitConstructorDecl(ConstructorDecl node) {
        currentFunctionReturnType = new Type(Type.BaseType.VOID);

        symbolTable.enterScope("constructor:" + node.getName());

        // First, define parameters in constructor scope (they take precedence over fields)
        for (Parameter param : node.getParameters()) {
            VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
            symbolTable.define(paramSymbol);
            param.accept(this);
        }

        // Then define class fields in constructor scope (including inherited fields)
        // but only if not already shadowed by a parameter
        if (currentClass != null) {
            defineClassFieldsInScope(currentClass);
        }

        // analyze body
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        symbolTable.exitScope();
        currentFunctionReturnType = null;
        return null;
    }

    @Override
    public Type visitVarDecl(VarDecl node) {
        // check that type exists
        if (!isValidType(node.getType())) {
            error("variable '" + node.getName() + "': unknown type '" + node.getType() + "'", node);
        }

        // check initializer if present
        if (node.getInitializer() != null) {
            Type initType = node.getInitializer().accept(this);

            // check that initializer type matches variable type
            if (!typesMatch(node.getType(), initType)) {
                error("variable '" + node.getName() + "': initializer type '" + initType + "' does not match variable type '" + node.getType() + "'", node);
            }

            // if variable is a reference, initializer must be an lvalue
            if (node.isReference() && !isLValue(node.getInitializer())) {
                error("variable '" + node.getName() + "': reference must be initialized with an lvalue", node);
            }
        } else {
            // references must be initialized
            if (node.isReference()) {
                error("variable '" + node.getName() + "': reference must be initialized", node);
            }
        }

        // Only define variable in symbol table if we're NOT inside a class definition
        // (class fields are already stored in ClassSymbol, not in the global scope)
        if (currentClass == null) {
            VarSymbol varSymbol = new VarSymbol(node.getName(), node.getType(), node.isReference());
            symbolTable.define(varSymbol);
        }

        return null;
    }

    @Override
    public Type visitParameter(Parameter node) {
        // check that type exists
        if (!isValidType(node.getType())) {
            error("parameter '" + node.getName() + "': unknown type '" + node.getType() + "'", node);
        }
        return null;
    }

    @Override
    public Type visitBlockStmt(BlockStmt node) {
        symbolTable.enterScope("block");

        for (Statement stmt : node.getStatements()) {
            stmt.accept(this);
        }

        symbolTable.exitScope();
        return null;
    }

    @Override
    public Type visitIfStmt(IfStmt node) {
        Type condType = node.getCondition().accept(this);

        // condition can be bool, int, char, or string (implicit conversion to bool)
        if (condType != null && !canConvertToBool(condType)) {
            error("if condition must be bool, int, char, or string", node);
        }

        node.getThenStmt().accept(this);

        if (node.getElseStmt() != null) {
            node.getElseStmt().accept(this);
        }

        return null;
    }

    @Override
    public Type visitWhileStmt(WhileStmt node) {
        Type condType = node.getCondition().accept(this);

        // condition can be bool, int, char, or string (implicit conversion to bool)
        if (condType != null && !canConvertToBool(condType)) {
            error("while condition must be bool, int, char, or string", node);
        }

        node.getBody().accept(this);
        return null;
    }

    @Override
    public Type visitReturnStmt(ReturnStmt node) {
        if (currentFunctionReturnType == null) {
            error("return statement outside of function", node);
            return null;
        }

        if (node.getValue() == null) {
            // return without value - function must be void
            if (!currentFunctionReturnType.isVoid()) {
                error("non-void function must return a value", node);
            }
        } else {
            // return with value
            Type returnType = node.getValue().accept(this);

            if (currentFunctionReturnType.isVoid()) {
                error("void function cannot return a value", node);
            } else if (!typesMatch(currentFunctionReturnType, returnType)) {
                error("return type '" + returnType + "' does not match function return type '" + currentFunctionReturnType + "'", node);
            }
        }

        return null;
    }

    @Override
    public Type visitExprStmt(ExprStmt node) {
        node.getExpression().accept(this);
        return null;
    }

    @Override
    public Type visitBinaryExpr(BinaryExpr node) {
        Type leftType = node.getLeft().accept(this);
        Type rightType = node.getRight().accept(this);

        if (leftType == null || rightType == null) {
            return null; // error already reported
        }

        BinaryExpr.Operator op = node.getOperator();

        // check operator compatibility
        switch (op) {
            case PLUS:
            case MINUS:
            case MULT:
            case DIV:
            case MOD:
                // arithmetic operators: both operands must be int
                if (!leftType.equals(new Type(Type.BaseType.INT)) || !rightType.equals(new Type(Type.BaseType.INT))) {
                    error("arithmetic operator requires int operands", node);
                    return null;
                }
                node.setType(new Type(Type.BaseType.INT));
                return new Type(Type.BaseType.INT);

            case LT:
            case LEQ:
            case GT:
            case GEQ:
                // relational operators: int or char
                if (leftType.equals(new Type(Type.BaseType.INT)) && rightType.equals(new Type(Type.BaseType.INT))) {
                    node.setType(new Type(Type.BaseType.BOOL));
                    return new Type(Type.BaseType.BOOL);
                } else if (leftType.equals(new Type(Type.BaseType.CHAR)) && rightType.equals(new Type(Type.BaseType.CHAR))) {
                    node.setType(new Type(Type.BaseType.BOOL));
                    return new Type(Type.BaseType.BOOL);
                } else {
                    error("relational operator requires int or char operands", node);
                    return null;
                }

            case EQ:
            case NEQ:
                // equality operators: same type
                if (!typesMatch(leftType, rightType)) {
                    error("equality operator requires operands of same type", node);
                    return null;
                }
                node.setType(new Type(Type.BaseType.BOOL));
                return new Type(Type.BaseType.BOOL);

            case AND:
            case OR:
                // logical operators: both operands must be bool
                if (!leftType.equals(new Type(Type.BaseType.BOOL)) || !rightType.equals(new Type(Type.BaseType.BOOL))) {
                    error("logical operator requires bool operands", node);
                    return null;
                }
                node.setType(new Type(Type.BaseType.BOOL));
                return new Type(Type.BaseType.BOOL);

            default:
                error("unknown binary operator: " + op, node);
                return null;
        }
    }

    @Override
    public Type visitUnaryExpr(UnaryExpr node) {
        Type operandType = node.getOperand().accept(this);

        if (operandType == null) {
            return null; // error already reported
        }

        UnaryExpr.Operator op = node.getOperator();

        switch (op) {
            case PLUS:
            case MINUS:
                // unary plus/minus: operand must be int
                if (!operandType.equals(new Type(Type.BaseType.INT))) {
                    error("unary +/- requires int operand", node);
                    return null;
                }
                node.setType(new Type(Type.BaseType.INT));
                return new Type(Type.BaseType.INT);

            case NOT:
                // logical not: operand must be bool
                if (!operandType.equals(new Type(Type.BaseType.BOOL))) {
                    error("logical ! requires bool operand", node);
                    return null;
                }
                node.setType(new Type(Type.BaseType.BOOL));
                return new Type(Type.BaseType.BOOL);

            default:
                error("unknown unary operator: " + op, node);
                return null;
        }
    }

    @Override
    public Type visitAssignExpr(AssignExpr node) {
        // target must be an lvalue
        if (!isLValue(node.getTarget())) {
            error("left side of assignment must be an lvalue", node);
        }

        Type targetType = node.getTarget().accept(this);
        Type valueType = node.getValue().accept(this);

        if (targetType == null || valueType == null) {
            return null; // error already reported
        }

        // check for slicing: Base b; b = derived; (allowed)
        if (targetType.getBaseType() == Type.BaseType.CLASS && valueType.getBaseType() == Type.BaseType.CLASS) {
            ClassSymbol targetClass = symbolTable.getClass(targetType.getClassName());
            ClassSymbol valueClass = symbolTable.getClass(valueType.getClassName());

            if (targetClass != null && valueClass != null && isDerivedFrom(valueClass, targetClass)) {
                // slicing is allowed
                node.setType(valueType);
                return valueType;
            }
        }

        // types must match
        if (!typesMatch(targetType, valueType)) {
            error("assignment: type mismatch ('" + targetType + "' vs '" + valueType + "')", node);
            return null;
        }

        node.setType(valueType);
        return valueType;
    }

    @Override
    public Type visitVarExpr(VarExpr node) {
        Symbol symbol = symbolTable.resolve(node.getName());

        if (symbol == null) {
            error("variable '" + node.getName() + "' not found", node);
            return null;
        }

        if (symbol.getKind() != Symbol.SymbolKind.VARIABLE) {
            error("'" + node.getName() + "' is not a variable", node);
            return null;
        }

        node.setType(symbol.getType());
        return symbol.getType();
    }

    @Override
    public Type visitCallExpr(CallExpr node) {
        // resolve function
        Symbol symbol = symbolTable.resolve(node.getFunctionName());

        if (symbol == null) {
            error("function '" + node.getFunctionName() + "' not found", node);
            return null;
        }

        if (symbol.getKind() != Symbol.SymbolKind.FUNCTION) {
            error("'" + node.getFunctionName() + "' is not a function", node);
            return null;
        }

        FunctionSymbol function = (FunctionSymbol) symbol;

        // check argument count
        if (node.getArguments().size() != function.getParameters().size()) {
            error("function '" + node.getFunctionName() + "': expected " + function.getParameters().size() + " arguments, got " + node.getArguments().size(), node);
            return null;
        }

        // check argument types
        for (int i = 0; i < node.getArguments().size(); i++) {
            Expression arg = node.getArguments().get(i);
            Parameter param = function.getParameters().get(i);

            Type argType = arg.accept(this);
            if (argType == null) {
                continue; // error already reported
            }

            if (!typesMatch(param.getType(), argType)) {
                error("function '" + node.getFunctionName() + "': argument " + (i + 1) + " type mismatch (expected '" + param.getType() + "', got '" + argType + "')", node);
            }

            // if parameter is a reference, argument must be an lvalue
            if (param.isReference() && !isLValue(arg)) {
                error("function '" + node.getFunctionName() + "': argument " + (i + 1) + " must be an lvalue (parameter is a reference)", node);
            }
        }

        node.setType(function.getType());
        return function.getType();
    }

    @Override
    public Type visitMemberAccessExpr(MemberAccessExpr node) {
        Type objectType = node.getObject().accept(this);

        if (objectType == null) {
            return null; // error already reported
        }

        if (objectType.getBaseType() != Type.BaseType.CLASS) {
            error("member access: object must be of class type", node);
            return null;
        }

        ClassSymbol classSymbol = symbolTable.getClass(objectType.getClassName());
        if (classSymbol == null) {
            error("member access: class '" + objectType.getClassName() + "' not found", node);
            return null;
        }

        if (node.isMethodCall()) {
            // method call - find best matching overload
            List<MethodSymbol> overloads = classSymbol.lookupMethodOverloads(node.getMemberName());
            if (overloads.isEmpty()) {
                error("class '" + objectType.getClassName() + "' has no method '" + node.getMemberName() + "'", node);
                return null;
            }

            // Find matching method overload
            MethodSymbol method = findBestMethodOverload(overloads, node.getArguments());
            if (method == null) {
                error("no matching method for call to '" + node.getMemberName() + "'", node);
                return null;
            }

            // check argument types (for error messages)
            for (int i = 0; i < node.getArguments().size(); i++) {
                Expression arg = node.getArguments().get(i);
                Parameter param = method.getParameters().get(i);

                Type argType = arg.accept(this);
                if (argType == null) {
                    continue; // error already reported
                }

                if (!typesMatch(param.getType(), argType)) {
                    error("method '" + node.getMemberName() + "': argument " + (i + 1) + " type mismatch (expected '" + param.getType() + "', got '" + argType + "')", node);
                }

                // if parameter is a reference, argument must be an lvalue
                if (param.isReference() && !isLValue(arg)) {
                    error("method '" + node.getMemberName() + "': argument " + (i + 1) + " must be an lvalue (parameter is a reference)", node);
                }
            }

            // Store the resolved method for the interpreter
            node.setResolvedMethod(method);
            node.setType(method.getType());
            return method.getType();
        } else {
            // field access
            VarSymbol field = classSymbol.lookupField(node.getMemberName());
            if (field == null) {
                error("class '" + objectType.getClassName() + "' has no field '" + node.getMemberName() + "'", node);
                return null;
            }

            node.setType(field.getType());
            return field.getType();
        }
    }

    @Override
    public Type visitConstructorCallExpr(ConstructorCallExpr node) {
        // first check if this is a built-in function
        if (isBuiltinFunction(node.getClassName())) {
            return checkBuiltinFunction(node);
        }

        // then check if this is a function call (not a constructor)
        List<FunctionSymbol> overloads = symbolTable.resolveOverloadedFunctions(node.getClassName());
        if (!overloads.isEmpty()) {
            // this is actually a function call, not a constructor
            // find the best matching overload
            FunctionSymbol function = findBestFunctionOverload(overloads, node.getArguments());

            if (function == null) {
                error("no matching function for call to '" + node.getClassName() + "'", node);
                return null;
            }

            // check argument types (for error messages)
            for (int i = 0; i < node.getArguments().size(); i++) {
                Expression arg = node.getArguments().get(i);
                Parameter param = function.getParameters().get(i);

                Type argType = arg.accept(this);
                if (argType == null) {
                    continue; // error already reported
                }

                if (!typesMatch(param.getType(), argType)) {
                    error("function '" + node.getClassName() + "': argument " + (i + 1) + " type mismatch (expected '" + param.getType() + "', got '" + argType + "')", node);
                }

                // if parameter is a reference, argument must be an lvalue
                if (param.isReference() && !isLValue(arg)) {
                    error("function '" + node.getClassName() + "': argument " + (i + 1) + " must be an lvalue (parameter is a reference)", node);
                }
            }

            // Store the resolved function for the interpreter
            node.setResolvedFunction(function);
            node.setType(function.getType());
            return function.getType();
        }

        // otherwise, it's a constructor call
        ClassSymbol classSymbol = symbolTable.getClass(node.getClassName());

        if (classSymbol == null) {
            error("class '" + node.getClassName() + "' not found", node);
            return null;
        }

        // find matching constructor
        ConstructorSymbol constructor = findConstructor(classSymbol, node.getArguments());

        if (constructor == null) {
            error("class '" + node.getClassName() + "': no matching constructor found", node);
            return null;
        }

        // Handle implicit copy constructor
        if (constructor == ConstructorSymbol.IMPLICIT_COPY) {
            // Just evaluate the argument type (already validated in findConstructor)
            for (Expression arg : node.getArguments()) {
                arg.accept(this);
            }
            node.setImplicitCopy(true);
            Type type = new Type(node.getClassName());
            node.setType(type);
            return type;
        }

        // check argument types
        for (int i = 0; i < node.getArguments().size(); i++) {
            Expression arg = node.getArguments().get(i);
            Parameter param = constructor.getParameters().get(i);

            Type argType = arg.accept(this);
            if (argType == null) {
                continue; // error already reported
            }

            if (!typesMatch(param.getType(), argType)) {
                error("constructor '" + node.getClassName() + "': argument " + (i + 1) + " type mismatch (expected '" + param.getType() + "', got '" + argType + "')", node);
            }

            // if parameter is a reference, argument must be an lvalue
            if (param.isReference() && !isLValue(arg)) {
                error("constructor '" + node.getClassName() + "': argument " + (i + 1) + " must be an lvalue (parameter is a reference)", node);
            }
        }

        Type type = new Type(node.getClassName());
        node.setType(type);
        return type;
    }

    @Override
    public Type visitLiteralExpr(LiteralExpr node) {
        Type type;
        switch (node.getLiteralType()) {
            case INT:
                type = new Type(Type.BaseType.INT);
                break;
            case BOOL:
                type = new Type(Type.BaseType.BOOL);
                break;
            case CHAR:
                type = new Type(Type.BaseType.CHAR);
                break;
            case STRING:
                type = new Type(Type.BaseType.STRING);
                break;
            default:
                error("unknown literal type", node);
                return null;
        }
        node.setType(type);
        return type;
    }

    @Override
    public Type visitType(Type node) {
        // types don't have types
        return null;
    }

    // helper methods

    private boolean isValidType(Type type) {
        if (type.isPrimitive()) {
            return true;
        }
        return symbolTable.hasClass(type.getClassName());
    }

    private boolean typesMatch(Type t1, Type t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        if (t1.equals(t2)) {
            return true;
        }
        // check for inheritance: t2 can be assigned to t1 if t2 is derived from t1
        if (t1.getBaseType() == Type.BaseType.CLASS && t2.getBaseType() == Type.BaseType.CLASS) {
            ClassSymbol class1 = symbolTable.getClass(t1.getClassName());
            ClassSymbol class2 = symbolTable.getClass(t2.getClassName());
            if (class1 != null && class2 != null) {
                return isDerivedFrom(class2, class1);
            }
        }
        return false;
    }

    private boolean canConvertToBool(Type type) {
        // bool, int, char, string can be converted to bool
        Type.BaseType baseType = type.getBaseType();
        return baseType == Type.BaseType.BOOL ||
               baseType == Type.BaseType.INT ||
               baseType == Type.BaseType.CHAR ||
               baseType == Type.BaseType.STRING;
    }

    private boolean isLValue(Expression expr) {
        if (expr instanceof VarExpr) {
            return true;
        }
        if (expr instanceof MemberAccessExpr) {
            MemberAccessExpr memberAccess = (MemberAccessExpr) expr;
            return !memberAccess.isMethodCall(); // field access is lvalue, method call is not
        }
        return false;
    }

    private boolean hasInheritanceCycle(ClassSymbol cls) {
        ClassSymbol current = cls.getBaseClass();
        while (current != null) {
            if (current == cls) {
                return true;
            }
            current = current.getBaseClass();
        }
        return false;
    }

    /**
     * Define all fields of a class (including inherited fields) in the current scope.
     * This is used when analyzing methods and constructors.
     */
    private void defineClassFieldsInScope(ClassSymbol classSymbol) {
        // First, add inherited fields (from base classes, in order from oldest ancestor)
        if (classSymbol.getBaseClass() != null) {
            defineClassFieldsInScope(classSymbol.getBaseClass());
        }

        // Then add own fields
        for (VarSymbol field : classSymbol.getFields().values()) {
            // Only define if not already defined (could be shadowed by subclass)
            if (symbolTable.resolveLocal(field.getName()) == null) {
                symbolTable.define(new VarSymbol(field.getName(), field.getType(), field.isReference()));
            }
        }
    }

    private boolean isDerivedFrom(ClassSymbol derived, ClassSymbol base) {
        ClassSymbol current = derived.getBaseClass();
        while (current != null) {
            if (current == base) {
                return true;
            }
            current = current.getBaseClass();
        }
        return false;
    }

    private ConstructorSymbol findConstructor(ClassSymbol classSymbol, List<Expression> arguments) {
        // first, evaluate argument types if not already set
        List<Type> argTypes = new ArrayList<>();
        for (Expression arg : arguments) {
            Type argType = arg.getType();
            if (argType == null) {
                argType = arg.accept(this);
            }
            argTypes.add(argType);
        }

        // find constructor with matching signature
        for (ConstructorSymbol constructor : classSymbol.getConstructors()) {
            if (constructor.getParameters().size() == arguments.size()) {
                boolean match = true;
                for (int i = 0; i < arguments.size(); i++) {
                    Type argType = argTypes.get(i);
                    Type paramType = constructor.getParameters().get(i).getType();
                    if (argType == null || !typesMatch(paramType, argType)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return constructor;
                }
            }
        }

        // if no constructor found and no arguments, use default constructor
        if (arguments.isEmpty()) {
            // check if default constructor exists
            for (ConstructorSymbol constructor : classSymbol.getConstructors()) {
                if (constructor.getParameters().isEmpty()) {
                    return constructor;
                }
            }
        }

        // check for implicit copy constructor: C(C obj) or C(C& obj)
        // If one argument of same class type (or derived), allow copy construction
        if (arguments.size() == 1 && argTypes.size() == 1) {
            Type argType = argTypes.get(0);
            if (argType != null && argType.getBaseType() == Type.BaseType.CLASS) {
                String argClassName = argType.getClassName();
                if (argClassName.equals(classSymbol.getName()) ||
                    isDerivedFrom(symbolTable.getClass(argClassName), classSymbol)) {
                    // Return a marker for implicit copy constructor
                    // We'll use null but handle it specially in the caller
                    return ConstructorSymbol.IMPLICIT_COPY;
                }
            }
        }

        return null;
    }

    private void checkVirtualMethodOverrides(ClassSymbol classSymbol) {
        if (classSymbol.getBaseClass() == null) {
            return; // no base class, nothing to check
        }

        // check each method in this class
        for (List<MethodSymbol> overloads : classSymbol.getMethods().values()) {
            for (MethodSymbol method : overloads) {
                MethodSymbol baseMethod = classSymbol.getBaseClass().lookupMethod(method.getName());

                if (baseMethod != null) {
                    // method with same name exists in base class
                    // check if signature matches
                    if (parametersMatch(method.getParameters(), baseMethod.getParameters())) {
                        // signatures match
                        // In C++, a derived class can "hide" a non-virtual base method with a method
                        // of the same signature. This is valid - it just means:
                        // - If base method is virtual: dynamic dispatch applies
                        // - If base method is not virtual: static dispatch (method hiding)
                        // The derived class can also introduce 'virtual' on its own method

                        // check that return types match
                        if (!typesMatch(method.getType(), baseMethod.getType())) {
                            error("method '" + method.getName() + "' in class '" + classSymbol.getName() + "' has different return type than base method", method.getDeclaration());
                        }
                    }
                }
            }
        }
    }

    private boolean parametersMatch(List<Parameter> params1, List<Parameter> params2) {
        if (params1.size() != params2.size()) {
            return false;
        }

        for (int i = 0; i < params1.size(); i++) {
            Parameter p1 = params1.get(i);
            Parameter p2 = params2.get(i);

            if (!typesMatch(p1.getType(), p2.getType()) || p1.isReference() != p2.isReference()) {
                return false;
            }
        }

        return true;
    }

    private boolean isBuiltinFunction(String name) {
        return name.equals("print_bool") ||
               name.equals("print_int") ||
               name.equals("print_char") ||
               name.equals("print_string");
    }

    private Type checkBuiltinFunction(ConstructorCallExpr node) {
        String name = node.getClassName();

        // all built-in functions take exactly 1 argument
        if (node.getArguments().size() != 1) {
            error("built-in function '" + name + "' expects 1 argument, got " + node.getArguments().size(), node);
            return null;
        }

        Expression arg = node.getArguments().get(0);
        Type argType = arg.accept(this);

        if (argType == null) {
            return null; // error already reported
        }

        // check argument type
        Type expectedType = null;
        if (name.equals("print_bool")) {
            expectedType = new Type(Type.BaseType.BOOL);
        } else if (name.equals("print_int")) {
            expectedType = new Type(Type.BaseType.INT);
        } else if (name.equals("print_char")) {
            expectedType = new Type(Type.BaseType.CHAR);
        } else if (name.equals("print_string")) {
            expectedType = new Type(Type.BaseType.STRING);
        }

        if (!typesMatch(expectedType, argType)) {
            error("built-in function '" + name + "': argument type mismatch (expected '" + expectedType + "', got '" + argType + "')", node);
            return null;
        }

        // built-in functions return void
        Type returnType = new Type(Type.BaseType.VOID);
        node.setType(returnType);
        return returnType;
    }

    /**
     * Find the best matching function overload for the given arguments.
     * Returns null and reports error if call is ambiguous.
     * Prefers non-reference parameters when argument is not an lvalue (unambiguous case).
     */
    private FunctionSymbol findBestFunctionOverload(List<FunctionSymbol> overloads, List<Expression> arguments) {
        // First evaluate argument types
        List<Type> argTypes = new ArrayList<>();
        List<Boolean> argIsLValue = new ArrayList<>();
        for (Expression arg : arguments) {
            argTypes.add(arg.accept(this));
            argIsLValue.add(isLValue(arg));
        }

        // Collect all viable overloads
        List<FunctionSymbol> viableOverloads = new ArrayList<>();

        for (FunctionSymbol func : overloads) {
            if (func.getParameters().size() != arguments.size()) {
                continue; // wrong number of arguments
            }

            boolean matches = true;

            for (int i = 0; i < arguments.size(); i++) {
                Type argType = argTypes.get(i);
                Parameter param = func.getParameters().get(i);

                if (argType == null || !typesMatch(param.getType(), argType)) {
                    matches = false;
                    break;
                }

                // Reference parameter requires lvalue argument
                if (param.isReference() && !argIsLValue.get(i)) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                viableOverloads.add(func);
            }
        }

        if (viableOverloads.isEmpty()) {
            return null; // no matching function
        }

        if (viableOverloads.size() == 1) {
            return viableOverloads.get(0); // unambiguous
        }

        // Multiple viable overloads - check for ambiguity
        // For f(int) vs f(int&) with an lvalue argument, both are viable and it's ambiguous
        // Check if there's a uniquely best match based on reference vs non-reference

        // Count how many arguments could go either way (lvalue that matches both ref and non-ref)
        boolean hasAmbiguousArg = false;
        for (int i = 0; i < arguments.size(); i++) {
            if (argIsLValue.get(i)) {
                // Check if there's both a ref and non-ref overload for this position
                boolean hasRef = false;
                boolean hasNonRef = false;
                for (FunctionSymbol func : viableOverloads) {
                    if (func.getParameters().get(i).isReference()) {
                        hasRef = true;
                    } else {
                        hasNonRef = true;
                    }
                }
                if (hasRef && hasNonRef) {
                    hasAmbiguousArg = true;
                    break;
                }
            }
        }

        if (hasAmbiguousArg) {
            // Ambiguous call - report error
            StringBuilder sb = new StringBuilder();
            sb.append("call is ambiguous between: ");
            for (int i = 0; i < viableOverloads.size(); i++) {
                if (i > 0) sb.append(", ");
                FunctionSymbol f = viableOverloads.get(i);
                sb.append(f.getName()).append("(");
                for (int j = 0; j < f.getParameters().size(); j++) {
                    if (j > 0) sb.append(", ");
                    Parameter p = f.getParameters().get(j);
                    sb.append(p.getType());
                    if (p.isReference()) sb.append("&");
                }
                sb.append(")");
            }
            error(sb.toString());
            return null;
        }

        // If no ambiguous args, just return the first viable (should not happen in practice)
        return viableOverloads.get(0);
    }

    /**
     * Find the best matching method overload for the given arguments.
     * Similar to findBestFunctionOverload but for methods.
     */
    private MethodSymbol findBestMethodOverload(List<MethodSymbol> overloads, List<Expression> arguments) {
        // First evaluate argument types
        List<Type> argTypes = new ArrayList<>();
        List<Boolean> argIsLValue = new ArrayList<>();
        for (Expression arg : arguments) {
            argTypes.add(arg.accept(this));
            argIsLValue.add(isLValue(arg));
        }

        // Collect all viable overloads
        List<MethodSymbol> viableOverloads = new ArrayList<>();

        for (MethodSymbol method : overloads) {
            if (method.getParameters().size() != arguments.size()) {
                continue; // wrong number of arguments
            }

            boolean matches = true;

            for (int i = 0; i < arguments.size(); i++) {
                Type argType = argTypes.get(i);
                Parameter param = method.getParameters().get(i);

                if (argType == null || !typesMatch(param.getType(), argType)) {
                    matches = false;
                    break;
                }

                // Reference parameter requires lvalue argument
                if (param.isReference() && !argIsLValue.get(i)) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                viableOverloads.add(method);
            }
        }

        if (viableOverloads.isEmpty()) {
            return null; // no matching method
        }

        if (viableOverloads.size() == 1) {
            return viableOverloads.get(0); // unambiguous
        }

        // Multiple viable overloads - just return first one (could add ambiguity check like functions)
        return viableOverloads.get(0);
    }
}
