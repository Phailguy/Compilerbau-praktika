package de.hsbi.interpreter.ast;

/**
 * visitor interface for traversing the AST
 */
public interface ASTVisitor<T> {
    // declarations
    T visitProgram(Program node);
    T visitClassDecl(ClassDecl node);
    T visitFunctionDecl(FunctionDecl node);
    T visitMethodDecl(MethodDecl node);
    T visitConstructorDecl(ConstructorDecl node);
    T visitVarDecl(VarDecl node);
    T visitParameter(Parameter node);

    // statements
    T visitBlockStmt(BlockStmt node);
    T visitIfStmt(IfStmt node);
    T visitWhileStmt(WhileStmt node);
    T visitReturnStmt(ReturnStmt node);
    T visitExprStmt(ExprStmt node);

    // expressions
    T visitBinaryExpr(BinaryExpr node);
    T visitUnaryExpr(UnaryExpr node);
    T visitAssignExpr(AssignExpr node);
    T visitVarExpr(VarExpr node);
    T visitCallExpr(CallExpr node);
    T visitMemberAccessExpr(MemberAccessExpr node);
    T visitConstructorCallExpr(ConstructorCallExpr node);
    T visitLiteralExpr(LiteralExpr node);

    // types
    T visitType(Type node);
}
