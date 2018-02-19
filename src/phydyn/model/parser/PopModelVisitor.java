// Generated from /home/igor/z/imperial/eclipsews/phydyndev/src/phydyn/model/parser/PopModel.g4 by ANTLR 4.5.2

package phydyn.model.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PopModelParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PopModelVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PopModelParser#stm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStm(PopModelParser.StmContext ctx);
	/**
	 * Visit a parse tree produced by {@link PopModelParser#equation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquation(PopModelParser.EquationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntExpr(PopModelParser.IntExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code powerExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPowerExpr(PopModelParser.PowerExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code condExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondExpr(PopModelParser.CondExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code vectorExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVectorExpr(PopModelParser.VectorExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code minusExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinusExpr(PopModelParser.MinusExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code callSpecialExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallSpecialExpr(PopModelParser.CallSpecialExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code cmpExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmpExpr(PopModelParser.CmpExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code identExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentExpr(PopModelParser.IdentExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code floatExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatExpr(PopModelParser.FloatExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpr(PopModelParser.NotExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sumExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSumExpr(PopModelParser.SumExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prodExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProdExpr(PopModelParser.ProdExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code callBinaryExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallBinaryExpr(PopModelParser.CallBinaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolExpr(PopModelParser.BoolExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthExpr}
	 * labeled alternative in {@link PopModelParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthExpr(PopModelParser.ParenthExprContext ctx);
}