import types.Token
import types.TokenType

class Lexer {

    val KEYWORDS = HashMap<String, TokenType>().apply {
        set("let", TokenType.Let)
        set("const", TokenType.Const)
        set("null", TokenType.Null)
        set("if", TokenType.If)
        set("else", TokenType.Else)
    }


    fun isSkippAble(char: Char): Boolean {
        return char == ' ' || char == '\n' || char == '\r'|| char == '\t'
    }

    var c = '0'
    var position = -1
    lateinit var source: CharArray

    fun next(): Boolean {
        position++
        if (position >= source.size) {
            return false
        }
        c = source[position]
        return true
    }
    fun isNext(char:Char):Boolean{
        if (position+1 >= source.size) {
            return false
        }
        return source[position+1] == char
    }

    fun tokenize(sourceCode: String): ArrayList<Token> {
        val tokens = ArrayList<Token>()
        source = sourceCode.toCharArray()


        while (next()) {


            when (c) {
                '(' -> {
                    tokens.add(Token(c.toString(), TokenType.OpenParen))
                }
                ')' -> {
                    tokens.add(Token(c.toString(), TokenType.CloseParen))
                }
                '{' -> {
                    tokens.add(Token(c.toString(), TokenType.OpenBrace))
                }
                '}' -> {
                    tokens.add(Token(c.toString(), TokenType.CloseBrace))
                }
                '>' -> {
                    if (isNext('>')){
                        next()
                        tokens.add(Token(">>", TokenType.R_SHIFT))
                    }else{
                        tokens.add(Token(c.toString(), TokenType.EQ_GT))

                    }
                }
                '<' -> {
                    if (isNext('<')){
                        next()
                        tokens.add(Token("<<", TokenType.L_SHIFT))
                    }else {
                        tokens.add(Token(c.toString(), TokenType.EQ_LT))
                    }
                }
                '+', '-', '*', '/', '%' -> {
                    tokens.add(Token(c.toString(), TokenType.BinaryOperator))
                }

                '=' -> {
                    tokens.add(Token(c.toString(), TokenType.Equals))
                }
                ':' -> {
                    tokens.add(Token(c.toString(), TokenType.Colon))
                }
                ';' -> {
                    tokens.add(Token(c.toString(), TokenType.SemiColon))
                }
                ',' -> {
                    tokens.add(Token(c.toString(), TokenType.Comma))
                }

                else -> {
                    if (c.isDigit()) {
                        var num = c.toString()
                        while (next() && c.isDigit()) {
                            num += c
                        }
                        position--


                        tokens.add(Token(num, TokenType.Number))
                    } else if (c.isLetter()) {
                        var ident = c.toString()
                        while (next() && c.isLetterOrDigit()) {
                            ident += c
                        }
                        position--

                        val reserved = KEYWORDS.get(ident)
                        if (reserved != null) {
                            tokens.add(Token(ident, reserved))
                        } else {
                            tokens.add(Token(ident, TokenType.Identifier))
                        }


                    } else if (isSkippAble(c)) {
                        continue
                    } else {
                        throw Error("Незизвестный токен $c" )
                    }
                }
            }
            // Build Number


        }

        tokens.add(Token("End of file ", TokenType.EOF))
        return tokens
    }
}