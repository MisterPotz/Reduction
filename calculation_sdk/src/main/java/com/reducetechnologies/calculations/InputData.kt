package com.reducetechnologies.calculations

import com.reducetechnologies.specificationsAndRequests.Specifications

data class InputData(
    /**
     * [isED] - нужен ли подбор электродвигателя
     * [TT] - момент на тихоходном валу, Н*м
     * [NT] - частота вращения тихоходного вала, мин(-1)
     * [LH] - ресурс работы, ч
     * [NRR] - номер режима работы
     * [KOL] - количество редукторов в серии, шт. в год (по умолчанию 10000)
     * [U0] - передаточное отношение побочной цепи между редуктором и ЭД, если его не - U0 = 1
     * [UREMA] - передаточное отношение редуктора (максимально желаемое, если будет производиться
     * подбор ЭД, и предварительно рассчитанное, если ЭД не будет подбираться)
     * [TIPRE] - тип редуктора
     * [NP] - признак передачи
     * [BETMI] - минимальный угол наклона зубьев, градусы
     * [BETMA] - максимальный угол наклона зубьев, градусы
     * [OMEG] - коэффициент неравномерности нагрузки по потокам
     * [NW] - число потоков
     * [NZAC1, NZAC2] - число зацеплений шестерни и колеса
     * [NWR] - число полушевронов
     * [BKAN] - коэффициент наличия канавки между полушевронами
     * [SIGN] - признак зацепления (1 - внешнее, -1 - внутреннее)
     * [CONSOL] - признак шестерни на консоли (false - нет, true - да)
     * [KPD] - КПД редуктора,
     * [IST] - число ступеней
     */
    //С ввода пользователя
    val isED: Boolean,
    val TT: Float,
    val NT: Float,
    val LH: Int,
    val NRR: Int,
    val KOL: Int = 10000,
    val U0: Float = 1f,
    val UREMA: Float,

    //Рассчитываются исходя из картинки
    val TIPRE: Int,
    var NP: Int,
    var BETMI: Float,
    var BETMA: Float,
    val OMEG: Float,
    val NW: Int,
    var NZAC: Array<Int>,
    val NWR: Int,
    val BKAN: Int,
    val SIGN: Array<Int> = arrayOf(),
    val CONSOL: Array<Boolean>,
    val KPD: Float,
    val IST: Int,//Просто задаём количество ступеней
    val wheelType: Specifications.WheelType,
    val wheelSubtype: Array<Specifications.WheelSubtype>,
    val PAR: Boolean,//характеризует наличие/отсутствие планетарной передачи

    //стандартный угол профиля 20 градусов или 0.349 радиан
    val ALF: Float = 0.349f,
    val HL: Float = 0f,//сюда посмотреть истинные значения, пока это заглушка
    val HA: Float = 0f,//сюда посмотреть истинные значения, пока это заглушка
    val HG: Float = 0f,//сюда посмотреть истинные значения, пока это заглушка
    val C: Float = 0f
)