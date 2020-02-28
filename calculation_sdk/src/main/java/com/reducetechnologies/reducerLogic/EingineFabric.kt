package com.reducetechnologies.reducerLogic

import com.reducetechnologies.interfacesAndAbstractions.Engine
import com.reducetechnologies.specificationsAndRequests.EngineRequest

abstract class EngineFabric(var engineRequest: EngineRequest) {
    fun createEngine(): Engine {
        return when (engineRequest.doCalculate) {
            true -> object :
                Engine() {
                override var NED: Float = 1000f
                    get() = println("Wrooom wroom motherfucka").run { return field }
            }
            false -> object :
                Engine() {
                override var NED: Float = 0f
                    get() = println("Wrooom wroom motherfucka").run { return field }
            }
        }
    }
}