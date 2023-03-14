package Runtime

import ast.BlockStatement
import ast.Expression
import environment.Environment
import java.lang.Float.NaN

enum class ValueType {
    Null,

    Number,
    Expression,
    Boolean,
    Object,
    NativeFunction,
    RuntimeFunction,
    ReturnValue
}


open class RuntimeVal(val type: ValueType)
open class ReturnVal(val value: RuntimeVal):RuntimeVal(ValueType.ReturnValue)


open class NullVal : RuntimeVal(ValueType.Null){
    val value = "null"
    override fun toString():String{
        return "null"
    }
}
open class BooleanVal(var value: Boolean) : RuntimeVal(ValueType.Boolean){
    override fun toString():String{
        return value.toString()
    }
}
open class NumberVal : RuntimeVal(ValueType.Number){
    open var value:Number = 0
}
open class NaNVal : NumberVal(){
    override var value:Number = NaN
}
open class IntegerVal(override var value:Number) : NumberVal() {
    override fun toString():String{
        return value.toString()
    }
}
open class FloatVal(override var value:Number) : NumberVal() {
    override fun toString():String{
        return value.toString()
    }
}
open class LongVal(override var value:Number) :  NumberVal(){
    override fun toString():String{
        return value.toString()
    }
}

open class ExpressionVal(val value:Expression) : RuntimeVal(ValueType.Expression){
    override fun toString():String{
        return value.toString()
    }
}

open class ObjectVal() : RuntimeVal(ValueType.Object){

    val properties = HashMap<String,RuntimeVal>()
    override fun toString():String{
        return properties.toString()
    }
}
fun interface NativeFunctionCall{
    fun call(args:ArrayList<RuntimeVal>,env: Environment):RuntimeVal
}
open class NativeFunctionVal(val call: NativeFunctionCall):RuntimeVal(ValueType.NativeFunction)


open class RuntimeFunctionVal(val arguments:ArrayList<Expression>, val body: BlockStatement):RuntimeVal(ValueType.RuntimeFunction)
