// Generated from src/main/antlr4/CPP.g4 by ANTLR 4.13.1
package de.hsbi.interpreter.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CPPParser}.
 */
public interface CPPListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CPPParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(CPPParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(CPPParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void enterClassDecl(CPPParser.ClassDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void exitClassDecl(CPPParser.ClassDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#classMember}.
	 * @param ctx the parse tree
	 */
	void enterClassMember(CPPParser.ClassMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#classMember}.
	 * @param ctx the parse tree
	 */
	void exitClassMember(CPPParser.ClassMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#fieldDecl}.
	 * @param ctx the parse tree
	 */
	void enterFieldDecl(CPPParser.FieldDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#fieldDecl}.
	 * @param ctx the parse tree
	 */
	void exitFieldDecl(CPPParser.FieldDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#methodDecl}.
	 * @param ctx the parse tree
	 */
	void enterMethodDecl(CPPParser.MethodDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#methodDecl}.
	 * @param ctx the parse tree
	 */
	void exitMethodDecl(CPPParser.MethodDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#constructorDecl}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDecl(CPPParser.ConstructorDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#constructorDecl}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDecl(CPPParser.ConstructorDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDecl(CPPParser.FunctionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDecl(CPPParser.FunctionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(CPPParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(CPPParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(CPPParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(CPPParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(CPPParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(CPPParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(CPPParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(CPPParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(CPPParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(CPPParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(CPPParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(CPPParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(CPPParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(CPPParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(CPPParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(CPPParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(CPPParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(CPPParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#exprStmt}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(CPPParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#exprStmt}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(CPPParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(CPPParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(CPPParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#assignmentExpr}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpr(CPPParser.AssignmentExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#assignmentExpr}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpr(CPPParser.AssignmentExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#logicalOrExpr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpr(CPPParser.LogicalOrExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#logicalOrExpr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpr(CPPParser.LogicalOrExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#logicalAndExpr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpr(CPPParser.LogicalAndExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#logicalAndExpr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpr(CPPParser.LogicalAndExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#equalityExpr}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpr(CPPParser.EqualityExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#equalityExpr}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpr(CPPParser.EqualityExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpr(CPPParser.RelationalExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpr(CPPParser.RelationalExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpr(CPPParser.AdditiveExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpr(CPPParser.AdditiveExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpr(CPPParser.MultiplicativeExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpr(CPPParser.MultiplicativeExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(CPPParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(CPPParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#postfixExpr}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpr(CPPParser.PostfixExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#postfixExpr}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpr(CPPParser.PostfixExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void enterPostfixOp(CPPParser.PostfixOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void exitPostfixOp(CPPParser.PostfixOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(CPPParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(CPPParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(CPPParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(CPPParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CPPParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(CPPParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link CPPParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(CPPParser.LiteralContext ctx);
}