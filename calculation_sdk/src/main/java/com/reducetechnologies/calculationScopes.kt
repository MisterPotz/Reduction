package com.reducetechnologies

fun calculateDitch(datas: List<Any>) {
    val dopnScope = DOPNScope()
    val dopnMethods: DOPN_Methods = DOPN_Methods
    for (data in datas) {
        DOPN_Methods.dopn(DOPN_Methods.Arguments(data), dopnScope)

        // Отсылаем все к чертям
        ReducerFabric(CreationData(dopnScope.copy())).createReduce()
    }
}

object DOPN_Methods {
    data class Arguments(val chto_to: Any)

    fun dopn(args: Arguments, dopnScope: DOPNScope) {
        // Получаем некую полезную информацию из Arguments
        dopnScope.apply {
            sgth = 4f
            sgthmd = 2f
        }
    }
}

data class DOPNScope(
    var sgth: Float? = null,
    var sgthmd: Float? = null
)

data class CreationData(
    val scope: DOPNScope
)


abstract class Reducer {
    abstract val stage: Stage
}

abstract class Stage {
    abstract val type: Int
}

class ReducerFabric(val dataToCreateWith: CreationData) {
    fun createReduce(): Reducer {
        return object : Reducer() {
            override val stage: Stage = object : Stage() {
                override val type: Int
                    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            }
        }
    }
}