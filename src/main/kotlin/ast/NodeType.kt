package ast

enum class NodeType {
    Program,
    NumericLiteral,
    Identifier,
    BinaryExpr,
    NullLiteral,
    IfStatement,
    VarDeclaration,
    BlockStatement,
    Assignment,
    Property,
    ObjectLiteral,
    MemberExpression,
    CallExpression
}