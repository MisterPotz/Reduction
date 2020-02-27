package com.reducetechnologies.interfacesAndAbstractions

import com.reducetechnologies.specificationsAndRequests.Specifications

abstract class Wheel {
    /**
     * Prefer float to double in geometric parameters to increase effectiveness of calculations
     */
    abstract var wheelType: Specifications.WheelType
    abstract var wheelSubtype: Specifications.WheelSubtype
    abstract var SGHD: Int?// допускаемые напряжения при расч на сопр усталости при контактных нагр.
    abstract var SGHMD: Int?// допускаемые контактные напряжения при действии максимальной нагрузки
    abstract var SGFD: Int?// допускаемые напряжения при расч на сопр усталости при изгибе
    abstract var SGFMD: Int?// допускаемые напряжения изгиба при действии максимальной нагрузки
    abstract var HRC: Float?// твёрдость по Роквеллу поверхностей зубьев
    abstract var N: Float?// частота вращения колеса(и его вала) - подумать над вынесением в валы
    abstract var SGT: Float?// предел текучести материала
    abstract var NZAC: Int?// число зацеплений одной стороной зуба
    abstract var KFC: Float?// коэффициенты, учитывающие двустороннее приложение нагрузки к зубу


    abstract var geometricOne: Float?
    abstract var geometricTwo: Float?
}