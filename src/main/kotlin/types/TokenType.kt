package types

enum class TokenType {
    Number, Identifier, Equals, OpenParen, CloseParen, BinaryOperator, Let, EOF,Null,
    If,
    Else,
    OpenBrace,
    CloseBrace,

    Comma,
    Colon,
    SemiColon,
    R_SHIFT,
    L_SHIFT,
    EQ_GT,
    EQ_LT,

    EQ_GTE,
    EQ_LTE,
    Const,
    LineTerminatorSequence,

}
