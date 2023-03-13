package types

class Token(var value: String, var type: TokenType){
    override fun toString():String{
        return "value:$value \ttype: $type "
    }
}
