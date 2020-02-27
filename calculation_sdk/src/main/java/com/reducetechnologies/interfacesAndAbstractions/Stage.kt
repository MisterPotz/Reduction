package com.reducetechnologies.interfacesAndAbstractions

import com.reducetechnologies.specificationsAndRequests.Specifications

abstract class Stage {
    abstract var stageType: Specifications.StageType
    abstract var wheels: List<Wheel>
    abstract var SGHMD: Int?
    abstract var SGHD: Int?
}