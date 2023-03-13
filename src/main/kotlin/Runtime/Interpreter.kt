package Runtime

import ast.*
import environment.Environment

class Interpreter {

    fun eval_program_expr(program:Program,env: Environment):RuntimeVal{
        var lastEval:RuntimeVal = NullVal()
        for (statement in program.body){
            lastEval = evaluate(statement, env)
        }
        return lastEval
    }

    private fun eval_if_statement(statement: IfStatement,env: Environment): RuntimeVal {
        val test = evaluate(statement.test, env) as NumberVal
        if (test.value > 0){
            return evaluate(statement.consequent, env)
        }else if (statement.alternate!=null){
            return evaluate(statement.alternate, env)
        }

        return NullVal()

    }

    fun eval_binary_expr(binop:BinaryExpression,env: Environment):RuntimeVal{
        val lhs = evaluate(binop.left, env)
        val rhs = evaluate(binop.right, env)
        if (lhs.type == ValueType.Number && rhs.type == ValueType.Number ){
           return eval_numeric_binary_expr(lhs as NumberVal,rhs as NumberVal,binop.operator)
        }
        return NullVal()
    }


    private fun eval_identifier(node: Identifier,env: Environment): RuntimeVal {
        var value = env.getVariable(node.symbol)
        if (value is ExpressionVal){
            value = evaluate((value as ExpressionVal).value,env)
        }
        return value
    }

    private fun eqret(bool:Boolean):Int{
        if (bool){
            return 1
        }else{
            return 0
        }
    }

    private fun eval_numeric_binary_expr(lhs: NumberVal, rhs: NumberVal,operator:String):NumberVal {
        var result = 0
        when (operator){
            "+" -> {result = lhs.value + rhs.value}
            "-" -> {result = lhs.value - rhs.value}
            "*" -> {result = lhs.value * rhs.value}
            "/" -> {result = lhs.value / rhs.value}
            "%" -> {result = lhs.value % rhs.value}
            ">" -> {result = eqret(lhs.value > rhs.value)}
            "<" -> {result = eqret(lhs.value < rhs.value)}
            ">=" -> {result = eqret(lhs.value >= rhs.value)}
            "<=" -> {result = eqret(lhs.value <= rhs.value)}
            "!=" -> {result = eqret(lhs.value != rhs.value)}
            "==" -> {result = eqret(lhs.value == rhs.value)}
            else->{
                throw Error("Undef operator $operator")
            }
        }
        return NumberVal(result)
    }


    private fun eval_var_declaration(varDeclaration: VarDeclaration, env: Environment): RuntimeVal {
        var value = evaluate(varDeclaration.value,env)
         env.declareVar(varDeclaration.identifier.symbol,value,varDeclaration.isConstant)
        return value

    }
    private fun eval_assignment(assignment: Assignment, env: Environment): RuntimeVal {
        if (assignment.assign.kind != NodeType.Identifier){
            throw Error("Error Assign ")
        }

        val varname = (assignment.assign as Identifier).symbol
        return env.assignVar(varname,evaluate(assignment.value,env))

    }
    private fun eval_block_statement(block: BlockStatement, env: Environment): RuntimeVal {
        val newEnv = Environment(env)
        var lastEval:RuntimeVal = NullVal()
        for (statement in block.body){
            lastEval = evaluate(statement, newEnv)
        }
        return lastEval
    }


    private fun eval_object_expr(objectLiteral: ObjectLiteral, env: Environment): RuntimeVal {
        val root = ObjectVal()

        for (itm in objectLiteral.properties){
            val value = if (itm.value is NullLiteral){
                env.getVariable(itm.key)
            }else{
                evaluate(itm.value,env)
            }
            root.properties.set(itm.key,value)
        }

        return root
    }

    fun evaluate(astNode: Stmt, env: Environment):RuntimeVal{
        when (astNode.kind){

            NodeType.Identifier -> {
                return eval_identifier(astNode as Identifier,env)
            }
            NodeType.Assignment -> {
                return eval_assignment(astNode as Assignment,env)
            }
            NodeType.VarDeclaration -> {
                return eval_var_declaration(astNode as VarDeclaration,env)
            }
            NodeType.NumericLiteral -> {
                return NumberVal((astNode as NumericLiteral).value)
            }
            NodeType.Program ->{
                return eval_program_expr(astNode as Program,env)
            }
            NodeType.BinaryExpr ->{
                return eval_binary_expr(astNode as BinaryExpression,env)
            }
            NodeType.ObjectLiteral ->{
                return eval_object_expr(astNode as ObjectLiteral,env)
            }
            NodeType.IfStatement ->{
                return eval_if_statement(astNode as IfStatement,env)
            }
            NodeType.BlockStatement ->{
                return eval_block_statement(astNode as BlockStatement,env)
            }
            NodeType.NullLiteral -> {
                return NullVal()
            }
            else -> {
                throw Error("AST Error not eval statement ${astNode.kind}")

            }
        }
        return NullVal()
    }



}