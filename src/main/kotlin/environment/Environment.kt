package environment

import Runtime.RuntimeVal

class Environment(private val parent: Environment? = null) {
    private val variables = HashMap<String, RuntimeVal>()
    private val constants = HashSet<String>()

    fun declareVar(name: String, value: RuntimeVal, constant: Boolean): RuntimeVal {
        if (variables.get(name) != null) {
            throw Error("Variable '$name' already is defined.")
        }
        variables.set(name, value)
        if (constant){
            constants.add(name)
        }
        return value
    }

    fun assignVar(name: String, value: RuntimeVal):RuntimeVal {
        val env = resolve(name)
        if (env.constants.contains(name)){
            throw Error("Cannot modify $name variable is constant")
        }
        env.variables.set(name, value)
        return value
    }

    private fun resolve(name: String): Environment {
        if (variables.get(name) != null) {
            return this
        }
        if (parent == null) {
            throw Error("Cannot resolve variable $name")
        }
        return parent.resolve(name)
    }
    private fun resolveVariable(name: String): RuntimeVal {
        val res = variables.get(name)
        if (res != null) {
            return res
        }
        if (parent == null) {
            throw Error("Cannot resolve variable $name")
        }
        return parent.resolveVariable(name)
    }

    fun getVariable(name: String): RuntimeVal {
        val res = variables.get(name)
        if (res != null) {
            return res
        }
        if (parent == null) {
            throw Error("Cannot resolve variable $name")
        }
        return parent.resolveVariable(name)

    }

}