package parser

import Lexer
import ast.*
import types.Token
import types.TokenType

class Parser() {
    private var tokens = ArrayList<Token>()
    private var tokenPosition = 0
    private fun isNotEOF(): Boolean {
        return at().type != TokenType.EOF
    }

    private fun except(type: TokenType, message: String): Token {
        val current = at()
        val prev = next(true)

        val res = (prev.type == type) or (current.type == type)

        if (!res) {
            throw Error(message)
        }

        return prev
    }

    private fun at(): Token {
        return tokens[tokenPosition]
    }

    private fun next(isOverrideValue: Boolean = false): Token {
        var prev = at()
        tokenPosition++

        if (at().type == TokenType.LineTerminatorSequence) {
            val overrideVal = next(isOverrideValue)
            if (isOverrideValue) {
                prev = overrideVal
            }
        }
        return prev
    }

    fun produceAst(sourceCode: String): Program {
        tokens = Lexer().tokenize(sourceCode)

        val program = Program()

        for (x in tokens) {

            println(x.toString())
        }


        while (isNotEOF()) {

            program.body.add(parseStmt())
        }
        return program
    }

    private fun parseProgram() {

    }

    private fun parseBlockStatement(): BlockStatement {
        val block = BlockStatement()
        except(TokenType.OpenBrace, "NotFond Open brace statement")
        do {
            block.body.add(parseStmt())
        } while (at().type != TokenType.CloseBrace)
        except(TokenType.CloseBrace, "NotFond Open brace statement")
        return block
    }

    private fun parseStmt(): Stmt {

        var result: Stmt
        when (at().type) {
            TokenType.VAR,
            TokenType.VAL -> {
                result = variable_declaration()
            }
            TokenType.Return -> {
                result = return_statement()
            }
            TokenType.Function -> {
                result = function_declaration()
            }
            TokenType.If -> {
                result = selection_statement()
            }
            TokenType.OpenBrace -> {
                result = parseBlockStatement()
            }

            else -> {
                result = parseExpression()

            }
        }

        return result

    }

    private fun return_statement(): Stmt {
        next() // eat return
        return ReturnStatement(root_expression())
    }


    private fun parseExpression(): Expression {
        return assign_expression()
    }

    private fun assign_expression(): Expression {
        val left = object_expression()
        if (at().type == TokenType.Equals) {
            next()
            val value = assign_expression()
            return Assignment(left, value)
        }
        return left
    }


    private fun object_expression(): Expression {
        if (at().type != TokenType.OpenBrace) {
            return root_expression()
        }
        val rootObject = ObjectLiteral()
        next() // eat {
        while (isNotEOF() && at().type != TokenType.CloseBrace) {
            val key = except(TokenType.Identifier, "Not fond identifier").value

            // Allows shorthand key: pair -> { key, }
            if (at().type == TokenType.Comma) {
                next(); // advance past comma
                rootObject.properties.add(Property(key, NullLiteral()))
                continue
            } // Allows shorthand key: pair -> { key }
            else if (at().type == TokenType.CloseBrace) {
                rootObject.properties.add(Property(key, NullLiteral()))
                continue
            }

            except(TokenType.Colon, "Missing colon following identifier in ObjectExpr") // :
            val value = parseExpression()
            rootObject.properties.add(Property(key, value))

            if (at().type != TokenType.CloseBrace) {
                except(TokenType.Comma, "Expected comma or closing bracket following property")
            }
        }
        except(TokenType.CloseBrace, "Expected comma or closing bracket following property")
        return rootObject
    }

    private fun function_declaration(): Stmt {
        next()
        val functionName = Identifier(next().value)
        val args = parse_arguments()
        val functionBody = parseBlockStatement()
        return FunctionDeclaration(functionName, args, functionBody)
    }

    private fun variable_declaration(): Stmt {
        val isConst = next().type == TokenType.VAL
        val ident = Identifier(next().value)
        except(TokenType.Equals, " Not fond = ")
        val value = parseExpression()
        //except(TokenType.SemiColon,"Semicolon not fond")
        return VarDeclaration(ident, value, isConst)
    }


    /**
     * <selection-statement> ::= if ( <expression> ) <statement>
    | if ( <expression> ) <statement> else <statement>
    | switch ( <expression> ) <statement>
     */
    private fun selection_statement(): Stmt {
        except(TokenType.If, "HzError")

        except(TokenType.OpenParen, "Not fond open paren")
        val testExpression = and_expression()
        except(TokenType.CloseParen, "Not fond close paren")
        except(TokenType.OpenBrace, "Not fond open brace")
        val consequent = parseStmt()
        except(TokenType.CloseBrace, "Not fond close brace")
        var alternate: Stmt? = null
        if (at().type == TokenType.Else) {
            next()
            if (at().type == TokenType.If) {
                alternate = selection_statement()
            } else {
                except(TokenType.OpenBrace, "Not fond open brace")
                alternate = parseStmt()
                except(TokenType.CloseBrace, "Not fond close brace")
            }
        } else {

        }


        return IfStatement(testExpression, consequent, alternate)
    }


    /**
     * <and-expression> ::= <equality-expression>
    | <and-expression> & <equality-expression>
     */

    private fun and_expression(): Expression {
        var left = equality_expression()

        while (at().value == "&") {
            val operator = next().value
            val right = and_expression()

            left = BinaryExpression(left, right, operator)
        }
        return left
    }

    /**
     * <equality-expression> ::= <relational-expression>
    | <equality-expression> == <relational-expression>
    | <equality-expression> != <relational-expression>
     */
    private fun equality_expression(): Expression {
        var left = relational_expression()

        while (at().value == "==" || at().value == "!=") {
            val operator = next().value
            val right = equality_expression()

            left = BinaryExpression(left, right, operator)
        }
        return left
    }

    /**
     * <relational-expression> ::= <shift-expression>
    | <relational-expression> < <shift-expression>
    | <relational-expression> > <shift-expression>
    | <relational-expression> <= <shift-expression>
    | <relational-expression> >= <shift-expression>
     */
    private fun relational_expression(): Expression {
        var left = root_expression()

        while (at().value == "<" || at().value == ">" || at().value == ">=" || at().value == "<=") {
            val operator = next().value
            val right = relational_expression()

            left = BinaryExpression(left, right, operator)
        }
        return left
    }


    /**
    <shift-expression> ::= <additive-expression>
    | <shift-expression> << <additive-expression>
    | <shift-expression> >> <additive-expression>
     */
    private fun root_expression(): Expression {
        var left = additive_expression()

        while (at().value == "<<" || at().value == ">>") {
            val operator = next().value
            val right = root_expression()

            left = BinaryExpression(left, right, operator)
        }
        return left
    }


    // 10 + 5 - 7
    // left = 10
    // operator = +
    // right = 5

    // left = BinaryExpression(10 + 5)
    // operator = -
    // right = 7
    // left = BinaryExpression(BinaryExpression(10 + 5) - 7)


    /**
     * <additive-expression> ::= <multiplicative-expression>
    | <additive-expression> + <multiplicative-expression>
    | <additive-expression> - <multiplicative-expression>
     */

    private fun additive_expression(): Expression {
        var left = multiplicative_expression()

        while (at().value == "+" || at().value == "-") {
            val operator = next().value
            val right = multiplicative_expression()

            left = BinaryExpression(left, right, operator)
        }
        return left
    }

    /**
     * <multiplicative-expression> ::= <cast-expression>
    | <multiplicative-expression> * <cast-expression>
    | <multiplicative-expression> / <cast-expression>
    | <multiplicative-expression> % <cast-expression>
     */
    private fun multiplicative_expression(): Expression {
        var left = call_expression()

        while (at().value == "/" || at().value == "*" || at().value == "%") {
            val operator = next().value
            val right = call_expression()

            left = BinaryExpression(left, right, operator)
        }
        return left
    }

    // foo.bar()
    private fun call_expression(): Expression {
        val left = member_expression()

        if (at().type == TokenType.OpenParen) {
            return parse_call_expression(left)
        }

        return left
    }

    // foo.bar(
    // foo.bar(a,b)(b,s)(a)
    private fun parse_call_expression(member: Expression): Expression {
        val args = parse_arguments()

        var callExpression = CallExpression(member, args)

        if (at().type == TokenType.OpenParen) {
            return parse_call_expression(callExpression)
        }
        return callExpression
    }

    private fun parse_arguments(): ArrayList<Expression> {
        except(TokenType.OpenParen, "")
        val args = ArrayList<Expression>()
        if (at().type != TokenType.CloseParen) {
            parse_argument_list(args)
        }
        except(TokenType.CloseParen, "")
        return args

    }

    private fun parse_argument_list(list: ArrayList<Expression>) {
        list.add(root_expression())
        while (at().type == TokenType.Comma) {
            next()//eat ,
            list.add(root_expression())
        }
    }

    //foo.bar
    private fun member_expression(): Expression {
        var left = primary_expression()

        while (at().type == TokenType.Dot || at().type == TokenType.OpenBracket) {
            val operator = next().type // eat . or [

            val right: Expression
            var computed = false

            if (operator == TokenType.Dot) {
                computed = true
                right = primary_expression()

                if (right.kind != NodeType.Identifier) {
                    throw Error("Property only identifer")
                }
            } else {

                right = root_expression()
                except(TokenType.CloseBracket, "Not fond close bracket")
            }

            left = MemberExpression(left, right, computed)

        }

        return left

    }

    private fun primary_expression(): Expression {
        val tk = at().type
        println("TOKEN:" + tk)
        when (tk) {


            TokenType.Identifier -> {
                return Identifier(next().value)
            }

            TokenType.Number -> {
                return NumericLiteral(next().value.toInt())
            }

            TokenType.LineTerminatorSequence -> {
                next()
                return LineTerminator()
            }

            TokenType.Null -> {
                return NullLiteral(next().value)
            }

            TokenType.SemiColon -> {
                return NullLiteral(next().value)
            }

            TokenType.Colon -> {
                return NullLiteral(next().value)
            }

            TokenType.OpenParen -> {
                next()
                val value = parseExpression()
                if (next().type != TokenType.CloseParen) {
                    throw Error("Not found close paren")
                }
                return value
            }

            else -> {
                throw Error("Token Error ${at().value}")
            }
        }
    }

}