package ast

open class Stmt(var kind:NodeType) {
}

open class Expression(kind: NodeType) : Stmt(kind) {

}
open class Program:Stmt(NodeType.Program) {
    val body = ArrayList<Stmt>()

}

// 1 - 2
open class BinaryExpression(val left: Expression,val right:Expression,val operator:String) : Expression(NodeType.BinaryExpr){

}
open class Property(val key: String,val value:Expression) : Expression(NodeType.Property)

open class ObjectLiteral : Expression(NodeType.ObjectLiteral){
    val properties = ArrayList<Property>()
}

open class BlockStatement:Stmt(NodeType.BlockStatement) {
    val body = ArrayList<Stmt>()
}
open class Assignment(val assign: Expression,val value:Expression):Expression(NodeType.Assignment)
open class VarDeclaration(val identifier: Identifier,val value:Stmt,  val isConstant:Boolean):Stmt(NodeType.VarDeclaration)


open class IfStatement(val test:Expression,val consequent:Stmt,val alternate:Stmt?):Expression(NodeType.IfStatement)


open class NumericLiteral (val value:Int): Expression(NodeType.NumericLiteral){

}

open class NullLiteral (val value:String = "null"): Expression(NodeType.NullLiteral){

}
open class LineTerminator (): Expression(NodeType.NullLiteral){

}
// foo - bar
open class Identifier (val symbol:String): Expression(NodeType.Identifier){

}