package Runtime

import ast.*
import environment.Environment

class Interpreter {
    val root_env = Environment()
    init {

        root_env.declareVar("true", BooleanVal(true), true)
        root_env.declareVar("false", BooleanVal(false), true)
        root_env.declareVar("null", NullVal(), true)

        val global_System = ObjectVal()
        global_System.properties.set("currentTimeMillis",NativeFunctionVal(NativeFunctionCall { args, env ->
            return@NativeFunctionCall NumberVal(System.currentTimeMillis().toInt())
        }))

        root_env.declareVar("System",global_System,true)
        root_env.declareVar("print",NativeFunctionVal(NativeFunctionCall { args, env ->
            println("func:print::"+args)
            return@NativeFunctionCall NullVal()
        }),true)


    }


    fun execute(program: Program):RuntimeVal{
        return evaluate(program,root_env)
    }

    private fun eval_program_expr(program:Program,env: Environment):RuntimeVal{
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

    private fun eval_binary_expr(binop:BinaryExpression,env: Environment):RuntimeVal{
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
    private fun eval_call(callExpression: CallExpression, env: Environment): RuntimeVal {
        val function = evaluate(callExpression.callee,env)
        val args = ArrayList<RuntimeVal>()
        for (arg in callExpression.arguments){
            args.add(evaluate(arg,env))
        }

        if (function.type != ValueType.NativeFunction){
            throw Error("bad function name")
        }
       return (function as NativeFunctionVal).call.call(args,env)
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
            NodeType.MemberExpression ->{
                return eval_member_expression(astNode as MemberExpression,env)
            }
            NodeType.CallExpression ->{
                return eval_call(astNode as CallExpression,env)
            }
            NodeType.NullLiteral -> {
                return NullVal()
            }
            else -> {
                throw Error("AST Error not eval statement ${astNode.kind} ${astNode}")

            }
        }
        return NullVal()
    }

    private fun eval_member_expression(member: MemberExpression, env: Environment): RuntimeVal {
        val obj = evaluate(member.obj,env)

        if (obj.type != ValueType.Object){
            throw Error("$member not object")
        }

        var property = if (member.computed){
            (member.property as Identifier).symbol
        }else{
            evaluate(member.property,env).toString()
        }



        val result = (obj as ObjectVal).properties.get(property)
        if (result == null){
            return NullVal()
        }

        return result

    }


}