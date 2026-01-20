// Generated from src/main/antlr4/CPP.g4 by ANTLR 4.13.1
package de.hsbi.interpreter.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CPPParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CPPVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CPPParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(CPPParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#classDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDecl(CPPParser.ClassDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#classMember}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassMember(CPPParser.ClassMemberContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#fieldDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDecl(CPPParser.FieldDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#methodDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDecl(CPPParser.MethodDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#constructorDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDecl(CPPParser.ConstructorDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#functionDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDecl(CPPParser.FunctionDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(CPPParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(CPPParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(CPPParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(CPPParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(CPPParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(CPPParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(CPPParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#whileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(CPPParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#returnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(CPPParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#exprStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(CPPParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(CPPParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#assignmentExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentExpr(CPPParser.AssignmentExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#logicalOrExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOrExpr(CPPParser.LogicalOrExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#logicalAndExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAndExpr(CPPParser.LogicalAndExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#equalityExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpr(CPPParser.EqualityExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#relationalExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpr(CPPParser.RelationalExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#additiveExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpr(CPPParser.AdditiveExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpr(CPPParser.MultiplicativeExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#unaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(CPPParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#postfixExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixExpr(CPPParser.PostfixExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#postfixOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixOp(CPPParser.PostfixOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(CPPParser.ArgumentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpr(CPPParser.PrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CPPParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(CPPParser.LiteralContext ctx);
}