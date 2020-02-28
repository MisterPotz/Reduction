package com.reducetechnologies.reducerLogic

import com.reducetechnologies.interfacesAndAbstractions.Engine
import com.reducetechnologies.interfacesAndAbstractions.Shaft
import com.reducetechnologies.specificationsAndRequests.Specifications
import com.reducetechnologies.interfacesAndAbstractions.Stage

abstract class Reducer {
    abstract var reducerType: Specifications.ReducerType?
    abstract var stagesAmount: Specifications.StagesAmount?

    abstract var u: Float?
    abstract var ed: Engine?
    abstract var stages: List<Stage>?
    abstract var shafts: List<Shaft>?

    /**
     * Initializes this reducer with the help of factories
     */
    abstract fun calculate()
}


