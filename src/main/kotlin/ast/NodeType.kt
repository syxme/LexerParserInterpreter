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
    ReturnStatement,
    Assignment,
    Property,
    ObjectLiteral,
    MemberExpression,
    CallExpression,
    FunctionDeclaration
}