package de.hsbi.interpreter.ast;

import de.hsbi.interpreter.symbols.MethodSymbol;
import java.util.List;

/**
 * represents member access (field or method call)
 * e.g., obj.field or obj.method(args)
 */
public class MemberAccessExpr extends Expression {
    private Expression object;
    private String memberName;
    private boolean isMethodCall;
    private List<Expression> arguments; // null if not a method call
    private MethodSymbol resolvedMethod; // resolved method overload (set during semantic analysis)

    // for field access
    public MemberAccessExpr(Expression object, String memberName) {
        this.object = object;
        this.memberName = memberName;
        this.isMethodCall = false;
        this.arguments = null;
    }

    // for method call
    public MemberAccessExpr(Expression object, String memberName, List<Expression> arguments) {
        this.object = object;
        this.memberName = memberName;
        this.isMethodCall = true;
        this.arguments = arguments;
    }

    public Expression getObject() {
        return object;
    }

    public String getMemberName() {
        return memberName;
    }

    public boolean isMethodCall() {
        return isMethodCall;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public MethodSymbol getResolvedMethod() {
        return resolvedMethod;
    }

    public void setResolvedMethod(MethodSymbol resolvedMethod) {
        this.resolvedMethod = resolvedMethod;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMemberAccessExpr(this);
    }
}
