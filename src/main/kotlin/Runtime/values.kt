package Runtime

import ast.Expression

enum class ValueType {
    Null,
    Number,
    Expression,
    Boolean,
    Object
}


open class RuntimeVal(val type: ValueType)

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
open class NumberVal(val value:Int) : RuntimeVal(ValueType.Number){
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