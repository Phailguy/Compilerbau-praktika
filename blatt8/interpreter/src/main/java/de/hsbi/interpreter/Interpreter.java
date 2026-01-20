package de.hsbi.interpreter;

import de.hsbi.interpreter.ast.*;
import de.hsbi.interpreter.runtime.*;
import de.hsbi.interpreter.symbols.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tree-walking interpreter
 * executes the AST using the visitor pattern
 */
public class Interpreter implements ASTVisitor<Value> {
    private SymbolTable symbolTable;
    private Environment globalEnv;
    private Environment currentEnv;
    private Map<String, FunctionDecl> functions;
    private Map<String, ClassSymbol> classes;

    public Interpreter(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.globalEnv = new Environment(null);
        this.currentEnv = globalEnv;
        this.functions = new HashMap<>();
        this.classes = symbolTable.getClasses();
    }

    public Environment getGlobalEnv() {
        return globalEnv;
    }

    public Environment getCurrentEnv() {
        return currentEnv;
    }

    /**
     * execute a program
     */
    public void execute(Program program) {
        // register all functions
        for (FunctionDecl func : program.getFunctions()) {
            functions.put(func.getName(), func);
        }

        // find and execute main function if it exists
        if (functions.containsKey("main")) {
            FunctionDecl main = functions.get("main");
            executeFunction(main, new ArrayList<>());
        }
    }

    /**
     * execute a function
     */
    public Value executeFunction(FunctionDecl func, List<Value> arguments) {
        // create new environment for function
        Environment funcEnv = new Environment(globalEnv);
        Environment previousEnv = currentEnv;
        currentEnv = funcEnv;

        try {
            // bind parameters
            for (int i = 0; i < func.getParameters().size(); i++) {
                Parameter param = func.getParameters().get(i);
                Value arg = arguments.get(i);

                if (param.isReference()) {
                    // parameter is a reference - create reference value
                    currentEnv.define(param.getName(), new Value(arg));
                } else {
                    // parameter is not a reference - copy the value (with slicing)
                    currentEnv.define(param.getName(), copyValueWithSlicing(arg, param.getType()));
                }
            }

            // execute function body
            if (func.getBody() != null) {
                func.getBody().accept(this);
            }

            // if no return, return default value
            return Value.defaultValue(func.getReturnType());

        } catch (ReturnException e) {
            return e.getValue();
        } finally {
            currentEnv = previousEnv;
        }
    }

    /**
     * execute a method
     */
    public Value executeMethod(MethodDecl method, ObjectValue obj, List<Value> arguments) {
        // create new environment for method
        Environment methodEnv = new Environment(globalEnv);
        Environment previousEnv = currentEnv;
        currentEnv = methodEnv;

        // collect parameter names for shadowing check
        java.util.Set<String> parameterNames = new java.util.HashSet<>();
        for (Parameter param : method.getParameters()) {
            parameterNames.add(param.getName());
        }

        try {
            // make object fields accessible in method scope
            for (Map.Entry<String, Value> field : obj.getFields().entrySet()) {
                currentEnv.define(field.getKey(), field.getValue());
            }

            // bind parameters (overwrite fields if same name - parameter shadows field)
            for (int i = 0; i < method.getParameters().size(); i++) {
                Parameter param = method.getParameters().get(i);
                Value arg = arguments.get(i);

                if (param.isReference()) {
                    // parameter is a reference - create reference value
                    currentEnv.define(param.getName(), new Value(arg));
                } else {
                    // parameter is not a reference - copy the value (with slicing)
                    currentEnv.define(param.getName(), copyValueWithSlicing(arg, param.getType()));
                }
            }

            // execute method body
            if (method.getBody() != null) {
                method.getBody().accept(this);
            }

            // copy back field values from environment (but not shadowed by parameters)
            for (Map.Entry<String, Value> field : obj.getFields().entrySet()) {
                if (!parameterNames.contains(field.getKey()) && currentEnv.has(field.getKey())) {
                    Value updatedValue = currentEnv.get(field.getKey());
                    obj.setField(field.getKey(), updatedValue);
                }
            }

            // if no return, return default value
            return Value.defaultValue(method.getReturnType());

        } catch (ReturnException e) {
            // copy back field values before returning (but not shadowed by parameters)
            for (Map.Entry<String, Value> field : obj.getFields().entrySet()) {
                if (!parameterNames.contains(field.getKey()) && currentEnv.has(field.getKey())) {
                    Value updatedValue = currentEnv.get(field.getKey());
                    obj.setField(field.getKey(), updatedValue);
                }
            }
            return e.getValue();
        } finally {
            currentEnv = previousEnv;
        }
    }

    /**
     * execute a constructor
     */
    public void executeConstructor(ConstructorDecl constructor, ObjectValue obj, List<Value> arguments) {
        // create new environment for constructor
        Environment constructorEnv = new Environment(globalEnv);
        Environment previousEnv = currentEnv;
        currentEnv = constructorEnv;

        // collect parameter names for shadowing check
        java.util.Set<String> parameterNames = new java.util.HashSet<>();
        for (Parameter param : constructor.getParameters()) {
            parameterNames.add(param.getName());
        }

        try {
            // make object fields accessible in constructor scope
            for (Map.Entry<String, Value> field : obj.getFields().entrySet()) {
                currentEnv.define(field.getKey(), field.getValue());
            }

            // bind parameters (overwrite fields if same name - parameter shadows field)
            for (int i = 0; i < constructor.getParameters().size(); i++) {
                Parameter param = constructor.getParameters().get(i);
                Value arg = arguments.get(i);

                if (param.isReference()) {
                    // parameter is a reference - create reference value
                    currentEnv.define(param.getName(), new Value(arg));
                } else {
                    // parameter is not a reference - copy the value (with slicing)
                    currentEnv.define(param.getName(), copyValueWithSlicing(arg, param.getType()));
                }
            }

            // execute constructor body
            if (constructor.getBody() != null) {
                constructor.getBody().accept(this);
            }

            // copy back field values from environment (but not shadowed by parameters)
            for (Map.Entry<String, Value> field : obj.getFields().entrySet()) {
                if (!parameterNames.contains(field.getKey()) && currentEnv.has(field.getKey())) {
                    Value updatedValue = currentEnv.get(field.getKey());
                    obj.setField(field.getKey(), updatedValue);
                }
            }

        } catch (ReturnException e) {
            // constructors shouldn't return values, but handle it anyway
            for (Map.Entry<String, Value> field : obj.getFields().entrySet()) {
                if (!parameterNames.contains(field.getKey()) && currentEnv.has(field.getKey())) {
                    Value updatedValue = currentEnv.get(field.getKey());
                    obj.setField(field.getKey(), updatedValue);
                }
            }
        } finally {
            currentEnv = previousEnv;
        }
    }

    @Override
    public Value visitProgram(Program node) {
        // execute all top-level statements (if any)
        return null;
    }

    @Override
    public Value visitClassDecl(ClassDecl node) {
        // classes are not executed, only instantiated
        return null;
    }

    @Override
    public Value visitFunctionDecl(FunctionDecl node) {
        // functions are registered but not executed until called
        return null;
    }

    @Override
    public Value visitMethodDecl(MethodDecl node) {
        // methods are not executed directly
        return null;
    }

    @Override
    public Value visitConstructorDecl(ConstructorDecl node) {
        // constructors are not executed directly
        return null;
    }

    @Override
    public Value visitVarDecl(VarDecl node) {
        Value value;

        if (node.getInitializer() != null) {
            value = node.getInitializer().accept(this);

            if (node.isReference()) {
                // create reference to the value
                currentEnv.define(node.getName(), new Value(value));
            } else {
                // copy the value (with slicing if assigning derived to base)
                currentEnv.define(node.getName(), copyValueWithSlicing(value, node.getType()));
            }
        } else {
            // no initializer - use default value or call default constructor for classes
            if (node.getType().getBaseType() == Type.BaseType.CLASS) {
                // create object with default constructor
                ClassSymbol classSymbol = classes.get(node.getType().getClassName());
                if (classSymbol != null) {
                    ObjectValue obj = new ObjectValue(classSymbol);
                    initializeObjectFields(obj, classSymbol);

                    // call default constructor if exists
                    if (classSymbol.getBaseClass() != null) {
                        callBaseConstructor(obj, classSymbol.getBaseClass());
                    }
                    for (ConstructorSymbol constructor : classSymbol.getConstructors()) {
                        if (constructor.getParameters().isEmpty()) {
                            executeConstructor(constructor.getDeclaration(), obj, new ArrayList<>());
                            break;
                        }
                    }

                    value = new Value(node.getType(), obj);
                } else {
                    value = Value.defaultValue(node.getType());
                }
            } else {
                value = Value.defaultValue(node.getType());
            }
            currentEnv.define(node.getName(), value);
        }

        return null;
    }

    @Override
    public Value visitParameter(Parameter node) {
        // parameters are handled in function/method execution
        return null;
    }

    @Override
    public Value visitBlockStmt(BlockStmt node) {
        // create new environment for block
        Environment blockEnv = new Environment(currentEnv);
        Environment previousEnv = currentEnv;
        currentEnv = blockEnv;

        try {
            for (Statement stmt : node.getStatements()) {
                stmt.accept(this);
            }
        } finally {
            currentEnv = previousEnv;
        }

        return null;
    }

    @Override
    public Value visitIfStmt(IfStmt node) {
        Value condValue = node.getCondition().accept(this);

        if (convertToBool(condValue)) {
            node.getThenStmt().accept(this);
        } else if (node.getElseStmt() != null) {
            node.getElseStmt().accept(this);
        }

        return null;
    }

    @Override
    public Value visitWhileStmt(WhileStmt node) {
        while (true) {
            Value condValue = node.getCondition().accept(this);

            if (!convertToBool(condValue)) {
                break;
            }

            node.getBody().accept(this);
        }

        return null;
    }

    @Override
    public Value visitReturnStmt(ReturnStmt node) {
        Value value;

        if (node.getValue() != null) {
            value = node.getValue().accept(this);
        } else {
            value = Value.defaultValue(new Type(Type.BaseType.VOID));
        }

        throw new ReturnException(value);
    }

    @Override
    public Value visitExprStmt(ExprStmt node) {
        return node.getExpression().accept(this);
    }

    @Override
    public Value visitBinaryExpr(BinaryExpr node) {
        BinaryExpr.Operator op = node.getOperator();

        // short-circuit evaluation for && and ||
        if (op == BinaryExpr.Operator.AND) {
            Value left = node.getLeft().accept(this);
            if (!left.getBoolValue()) {
                return new Value(new Type(Type.BaseType.BOOL), false);
            }
            Value right = node.getRight().accept(this);
            return new Value(new Type(Type.BaseType.BOOL), right.getBoolValue());
        }

        if (op == BinaryExpr.Operator.OR) {
            Value left = node.getLeft().accept(this);
            if (left.getBoolValue()) {
                return new Value(new Type(Type.BaseType.BOOL), true);
            }
            Value right = node.getRight().accept(this);
            return new Value(new Type(Type.BaseType.BOOL), right.getBoolValue());
        }

        // evaluate both sides
        Value left = node.getLeft().accept(this);
        Value right = node.getRight().accept(this);

        switch (op) {
            case PLUS:
                return new Value(new Type(Type.BaseType.INT), left.getIntValue() + right.getIntValue());
            case MINUS:
                return new Value(new Type(Type.BaseType.INT), left.getIntValue() - right.getIntValue());
            case MULT:
                return new Value(new Type(Type.BaseType.INT), left.getIntValue() * right.getIntValue());
            case DIV:
                if (right.getIntValue() == 0) {
                    throw new RuntimeError("division by zero");
                }
                return new Value(new Type(Type.BaseType.INT), left.getIntValue() / right.getIntValue());
            case MOD:
                if (right.getIntValue() == 0) {
                    throw new RuntimeError("modulo by zero");
                }
                return new Value(new Type(Type.BaseType.INT), left.getIntValue() % right.getIntValue());
            case LT:
                if (left.getType().getBaseType() == Type.BaseType.INT) {
                    return new Value(new Type(Type.BaseType.BOOL), left.getIntValue() < right.getIntValue());
                } else {
                    return new Value(new Type(Type.BaseType.BOOL), left.getCharValue() < right.getCharValue());
                }
            case LEQ:
                if (left.getType().getBaseType() == Type.BaseType.INT) {
                    return new Value(new Type(Type.BaseType.BOOL), left.getIntValue() <= right.getIntValue());
                } else {
                    return new Value(new Type(Type.BaseType.BOOL), left.getCharValue() <= right.getCharValue());
                }
            case GT:
                if (left.getType().getBaseType() == Type.BaseType.INT) {
                    return new Value(new Type(Type.BaseType.BOOL), left.getIntValue() > right.getIntValue());
                } else {
                    return new Value(new Type(Type.BaseType.BOOL), left.getCharValue() > right.getCharValue());
                }
            case GEQ:
                if (left.getType().getBaseType() == Type.BaseType.INT) {
                    return new Value(new Type(Type.BaseType.BOOL), left.getIntValue() >= right.getIntValue());
                } else {
                    return new Value(new Type(Type.BaseType.BOOL), left.getCharValue() >= right.getCharValue());
                }
            case EQ:
                return new Value(new Type(Type.BaseType.BOOL), valuesEqual(left, right));
            case NEQ:
                return new Value(new Type(Type.BaseType.BOOL), !valuesEqual(left, right));
            default:
                throw new RuntimeError("unknown binary operator: " + op);
        }
    }

    @Override
    public Value visitUnaryExpr(UnaryExpr node) {
        Value operand = node.getOperand().accept(this);

        switch (node.getOperator()) {
            case PLUS:
                return new Value(new Type(Type.BaseType.INT), operand.getIntValue());
            case MINUS:
                return new Value(new Type(Type.BaseType.INT), -operand.getIntValue());
            case NOT:
                return new Value(new Type(Type.BaseType.BOOL), !operand.getBoolValue());
            default:
                throw new RuntimeError("unknown unary operator: " + node.getOperator());
        }
    }

    @Override
    public Value visitAssignExpr(AssignExpr node) {
        Value target = getLValue(node.getTarget());
        Value value = node.getValue().accept(this);

        // assignment writes through references
        target.assign(value);

        return value;
    }

    @Override
    public Value visitVarExpr(VarExpr node) {
        return currentEnv.get(node.getName());
    }

    @Override
    public Value visitCallExpr(CallExpr node) {
        // check for built-in functions
        if (node.getFunctionName().startsWith("print_")) {
            return executeBuiltinFunction(node.getFunctionName(), node.getArguments());
        }

        // get function
        FunctionDecl func = functions.get(node.getFunctionName());
        if (func == null) {
            throw new RuntimeError("function '" + node.getFunctionName() + "' not found");
        }

        // evaluate arguments
        List<Value> argValues = new ArrayList<>();
        for (Expression arg : node.getArguments()) {
            Value argValue = arg.accept(this);

            // check if parameter is a reference
            int index = argValues.size();
            Parameter param = func.getParameters().get(index);

            if (param.isReference()) {
                // pass the value directly (don't copy) - it's already a reference or will be made one
                argValues.add(argValue);
            } else {
                // copy the value
                argValues.add(copyValue(argValue));
            }
        }

        // execute function
        return executeFunction(func, argValues);
    }

    @Override
    public Value visitMemberAccessExpr(MemberAccessExpr node) {
        Value objValue = node.getObject().accept(this);
        ObjectValue obj = objValue.getObjectValue();

        if (node.isMethodCall()) {
            // Use resolved method from semantic analysis (handles overloading)
            MethodSymbol method = node.getResolvedMethod();

            if (method == null) {
                // Fallback to lookup if not resolved (shouldn't happen after semantic analysis)
                Type staticType = node.getObject().getType();
                ClassSymbol staticClass = classes.get(staticType.getClassName());
                method = staticClass.lookupMethod(node.getMemberName());
            }

            if (method == null) {
                throw new RuntimeError("method '" + node.getMemberName() + "' not found");
            }

            // Get the STATIC type from the expression (the declared type of the reference/variable)
            Type staticType = node.getObject().getType();
            ClassSymbol staticClass = classes.get(staticType.getClassName());

            // Check if the method (or any ancestor's version) is virtual
            boolean isVirtual = isMethodVirtual(staticClass, node.getMemberName());

            // resolve virtual method (if the method is virtual, use dynamic dispatch)
            if (isVirtual) {
                // dynamic dispatch: find matching method in the actual runtime type
                // We need to find a method with the same signature in the derived class
                MethodSymbol dynamicMethod = findMatchingMethod(obj.getClassSymbol(), node.getMemberName(), method);
                if (dynamicMethod != null) {
                    method = dynamicMethod;
                }
            }

            // evaluate arguments
            List<Value> argValues = new ArrayList<>();
            for (Expression arg : node.getArguments()) {
                Value argValue = arg.accept(this);

                // check if parameter is a reference
                int index = argValues.size();
                Parameter param = method.getParameters().get(index);

                if (param.isReference()) {
                    argValues.add(argValue);
                } else {
                    argValues.add(copyValueWithSlicing(argValue, param.getType()));
                }
            }

            // execute method
            return executeMethod(method.getDeclaration(), obj, argValues);
        } else {
            // field access
            Value field = obj.getField(node.getMemberName());
            if (field == null) {
                throw new RuntimeError("field '" + node.getMemberName() + "' not found in class '" + obj.getClassSymbol().getName() + "'");
            }
            return field;
        }
    }

    @Override
    public Value visitConstructorCallExpr(ConstructorCallExpr node) {
        // first check if this is a built-in function
        if (node.getClassName().startsWith("print_")) {
            return executeBuiltinFunction(node.getClassName(), node.getArguments());
        }

        // then check if this is a function call (not a constructor)
        // Use the resolved function from semantic analysis (handles overloading)
        if (node.isFunctionCall()) {
            FunctionSymbol funcSymbol = node.getResolvedFunction();
            FunctionDecl func = funcSymbol.getDeclaration();

            // evaluate arguments
            List<Value> argValues = new ArrayList<>();
            for (Expression arg : node.getArguments()) {
                Value argValue = arg.accept(this);

                // check if parameter is a reference
                int index = argValues.size();
                Parameter param = func.getParameters().get(index);

                if (param.isReference()) {
                    argValues.add(argValue);
                } else {
                    argValues.add(copyValue(argValue));
                }
            }

            // execute function
            return executeFunction(func, argValues);
        }

        // otherwise, it's a constructor call
        ClassSymbol classSymbol = classes.get(node.getClassName());

        if (classSymbol == null) {
            throw new RuntimeError("class '" + node.getClassName() + "' not found");
        }

        // Handle implicit copy constructor
        if (node.isImplicitCopy()) {
            // Evaluate the source object
            Value sourceValue = node.getArguments().get(0).accept(this);
            ObjectValue sourceObj = sourceValue.getObjectValue();

            // Create new object by copying all fields
            ObjectValue newObj = new ObjectValue(classSymbol);
            for (String fieldName : sourceObj.getFields().keySet()) {
                Value fieldValue = sourceObj.getField(fieldName);
                newObj.setField(fieldName, copyValue(fieldValue));
            }

            return new Value(new Type(node.getClassName()), newObj);
        }

        // create new object
        ObjectValue obj = new ObjectValue(classSymbol);

        // initialize fields with default values (including inherited fields)
        initializeObjectFields(obj, classSymbol);

        // call base class constructor (if any) - parameterless default constructor
        if (classSymbol.getBaseClass() != null) {
            callBaseConstructor(obj, classSymbol.getBaseClass());
        }

        // find and call matching constructor
        if (!node.getArguments().isEmpty() || !classSymbol.getConstructors().isEmpty()) {
            ConstructorSymbol constructor = findMatchingConstructor(classSymbol, node.getArguments());

            if (constructor != null) {
                // evaluate arguments
                List<Value> argValues = new ArrayList<>();
                for (Expression arg : node.getArguments()) {
                    Value argValue = arg.accept(this);

                    int index = argValues.size();
                    Parameter param = constructor.getParameters().get(index);

                    if (param.isReference()) {
                        argValues.add(argValue);
                    } else {
                        argValues.add(copyValue(argValue));
                    }
                }

                // execute constructor
                executeConstructor(constructor.getDeclaration(), obj, argValues);
            }
        }

        return new Value(new Type(node.getClassName()), obj);
    }

    @Override
    public Value visitLiteralExpr(LiteralExpr node) {
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
                throw new RuntimeError("unknown literal type");
        }
        return new Value(type, node.getValue());
    }

    @Override
    public Value visitType(Type node) {
        // types are not evaluated
        return null;
    }

    // helper methods

    private Value getLValue(Expression expr) {
        if (expr instanceof VarExpr) {
            VarExpr varExpr = (VarExpr) expr;
            return currentEnv.get(varExpr.getName());
        } else if (expr instanceof MemberAccessExpr) {
            MemberAccessExpr memberAccess = (MemberAccessExpr) expr;
            Value objValue = memberAccess.getObject().accept(this);
            ObjectValue obj = objValue.getObjectValue();
            return obj.getField(memberAccess.getMemberName());
        } else {
            throw new RuntimeError("expression is not an lvalue");
        }
    }

    private Value copyValue(Value value) {
        return copyValueWithSlicing(value, value.getType());
    }

    /**
     * Copy a value, potentially slicing to a target type.
     * For class types, if targetType is a base class of the value's type,
     * the object is sliced to contain only the base class fields and use
     * the base class's ClassSymbol (for correct virtual dispatch).
     */
    private Value copyValueWithSlicing(Value value, Type targetType) {
        if (value.getType().getBaseType() == Type.BaseType.CLASS) {
            ObjectValue original = value.getObjectValue();
            ClassSymbol targetClass;

            // Determine the target class (for slicing)
            if (targetType.getBaseType() == Type.BaseType.CLASS) {
                targetClass = classes.get(targetType.getClassName());
            } else {
                targetClass = original.getClassSymbol();
            }

            if (targetClass == null) {
                targetClass = original.getClassSymbol();
            }

            // Create a new object with the TARGET class (this enables correct slicing)
            ObjectValue copy = new ObjectValue(targetClass);

            // Only copy fields that belong to the target class (and its ancestors)
            copyFieldsForClass(copy, original, targetClass);

            return new Value(targetType, copy);
        } else {
            // primitive types - create new value with same data
            return new Value(value.getType(), value.getData());
        }
    }

    /**
     * Copy fields from original to copy, but only fields that belong to the given class
     * (including inherited fields from base classes).
     */
    private void copyFieldsForClass(ObjectValue copy, ObjectValue original, ClassSymbol classSymbol) {
        // First copy base class fields
        if (classSymbol.getBaseClass() != null) {
            copyFieldsForClass(copy, original, classSymbol.getBaseClass());
        }

        // Then copy this class's own fields
        for (String fieldName : classSymbol.getFields().keySet()) {
            Value fieldValue = original.getField(fieldName);
            if (fieldValue != null) {
                copy.setField(fieldName, copyValue(fieldValue));
            }
        }
    }

    private boolean convertToBool(Value value) {
        switch (value.getType().getBaseType()) {
            case BOOL:
                return value.getBoolValue();
            case INT:
                return value.getIntValue() != 0;
            case CHAR:
                return value.getCharValue() != '\0';
            case STRING:
                return !value.getStringValue().isEmpty();
            default:
                throw new RuntimeError("cannot convert type to bool: " + value.getType());
        }
    }

    private boolean valuesEqual(Value left, Value right) {
        if (left.getType().getBaseType() != right.getType().getBaseType()) {
            return false;
        }

        switch (left.getType().getBaseType()) {
            case INT:
                return left.getIntValue() == right.getIntValue();
            case BOOL:
                return left.getBoolValue() == right.getBoolValue();
            case CHAR:
                return left.getCharValue() == right.getCharValue();
            case STRING:
                return left.getStringValue().equals(right.getStringValue());
            case CLASS:
                // object equality: same instance
                return left.getObjectValue() == right.getObjectValue();
            default:
                return false;
        }
    }

    private void initializeObjectFields(ObjectValue obj, ClassSymbol classSymbol) {
        // initialize base class fields first
        if (classSymbol.getBaseClass() != null) {
            initializeObjectFields(obj, classSymbol.getBaseClass());
        }

        // initialize this class's fields
        for (VarSymbol field : classSymbol.getFields().values()) {
            Value fieldValue;
            if (field.getType().getBaseType() == Type.BaseType.CLASS) {
                // For class-typed fields, create a new object and call its default constructor
                ClassSymbol fieldClassSymbol = classes.get(field.getType().getClassName());
                if (fieldClassSymbol != null) {
                    ObjectValue fieldObj = new ObjectValue(fieldClassSymbol);
                    initializeObjectFields(fieldObj, fieldClassSymbol);

                    // call base constructors if any
                    if (fieldClassSymbol.getBaseClass() != null) {
                        callBaseConstructor(fieldObj, fieldClassSymbol.getBaseClass());
                    }

                    // call default constructor if exists
                    for (ConstructorSymbol constructor : fieldClassSymbol.getConstructors()) {
                        if (constructor.getParameters().isEmpty()) {
                            executeConstructor(constructor.getDeclaration(), fieldObj, new ArrayList<>());
                            break;
                        }
                    }

                    fieldValue = new Value(field.getType(), fieldObj);
                } else {
                    fieldValue = Value.defaultValue(field.getType());
                }
            } else {
                fieldValue = Value.defaultValue(field.getType());
            }
            obj.setField(field.getName(), fieldValue);
        }
    }

    private void callBaseConstructor(ObjectValue obj, ClassSymbol baseClass) {
        // recursively call base constructors
        if (baseClass.getBaseClass() != null) {
            callBaseConstructor(obj, baseClass.getBaseClass());
        }

        // call parameterless constructor of this base class
        for (ConstructorSymbol constructor : baseClass.getConstructors()) {
            if (constructor.getParameters().isEmpty()) {
                executeConstructor(constructor.getDeclaration(), obj, new ArrayList<>());
                return;
            }
        }
        // if no parameterless constructor, that's ok (default initialization)
    }

    private ConstructorSymbol findMatchingConstructor(ClassSymbol classSymbol, List<Expression> arguments) {
        for (ConstructorSymbol constructor : classSymbol.getConstructors()) {
            if (constructor.getParameters().size() == arguments.size()) {
                // check if types match
                boolean match = true;
                for (int i = 0; i < arguments.size(); i++) {
                    Type argType = arguments.get(i).getType();
                    Type paramType = constructor.getParameters().get(i).getType();

                    if (argType == null || !argType.equals(paramType)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    return constructor;
                }
            }
        }

        // if no constructor found and no arguments, use default constructor (or none)
        if (arguments.isEmpty()) {
            for (ConstructorSymbol constructor : classSymbol.getConstructors()) {
                if (constructor.getParameters().isEmpty()) {
                    return constructor;
                }
            }
        }

        return null;
    }

    /**
     * Check if a method is virtual, looking up the inheritance chain.
     * A method is virtual if it or any of its ancestors declared it as virtual.
     */
    private boolean isMethodVirtual(ClassSymbol classSymbol, String methodName) {
        ClassSymbol current = classSymbol;
        while (current != null) {
            List<MethodSymbol> overloads = current.getMethods().get(methodName);
            if (overloads != null) {
                for (MethodSymbol method : overloads) {
                    if (method.isVirtual()) {
                        return true;
                    }
                }
            }
            current = current.getBaseClass();
        }
        return false;
    }

    /**
     * Find a method with matching signature in the given class (for dynamic dispatch).
     */
    private MethodSymbol findMatchingMethod(ClassSymbol classSymbol, String methodName, MethodSymbol original) {
        List<MethodSymbol> overloads = classSymbol.lookupMethodOverloads(methodName);
        for (MethodSymbol method : overloads) {
            if (method.getParameters().size() == original.getParameters().size()) {
                boolean match = true;
                for (int i = 0; i < method.getParameters().size(); i++) {
                    Type t1 = method.getParameters().get(i).getType();
                    Type t2 = original.getParameters().get(i).getType();
                    if (!t1.equals(t2)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return method;
                }
            }
        }
        return null;
    }

    private Value executeBuiltinFunction(String name, List<Expression> arguments) {
        Value arg = arguments.get(0).accept(this);

        switch (name) {
            case "print_bool":
                System.out.println(arg.getBoolValue());
                break;
            case "print_int":
                System.out.println(arg.getIntValue());
                break;
            case "print_char":
                System.out.println(arg.getCharValue());
                break;
            case "print_string":
                System.out.println(arg.getStringValue());
                break;
            default:
                throw new RuntimeError("unknown built-in function: " + name);
        }

        return Value.defaultValue(new Type(Type.BaseType.VOID));
    }
}
