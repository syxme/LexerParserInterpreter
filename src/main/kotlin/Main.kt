import Runtime.BooleanVal
import Runtime.Interpreter
import Runtime.NullVal
import environment.Environment
import parser.Parser
import java.io.*
import java.util.*


fun main(args: Array<String>) {


    val env = Environment()
    env.declareVar("true", BooleanVal(true), true)
    env.declareVar("false", BooleanVal(false), true)
    env.declareVar("null", NullVal(), true)


    val program = Parser().produceAst(getResourceString("source.txt"))
    val reslt = Interpreter().evaluate(program,env)
    println("result = $reslt")
    val input = Scanner(System.`in`)
    while (true) {
        val line: String = input.nextLine()
        val program = Parser().produceAst(line)
        val reslt = Interpreter().evaluate(program,env)
        println("result = $reslt")
    }

}



private fun getResourceString(name: String):String{
    val classloader = Thread.currentThread().getContextClassLoader();
    return convertInputStreamToString(classloader.getResourceAsStream("source.txt"))
}
@Throws(IOException::class)
private fun convertInputStreamToString(`is`: InputStream): String {
    val result = ByteArrayOutputStream()
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var length: Int
    while (`is`.read(buffer).also { length = it } != -1) {
        result.write(buffer, 0, length)
    }

    // Java 1.1
    //return result.toString(StandardCharsets.UTF_8.name());
    return result.toString("UTF-8")

    // Java 10
    //return result.toString(StandardCharsets.UTF_8);
}