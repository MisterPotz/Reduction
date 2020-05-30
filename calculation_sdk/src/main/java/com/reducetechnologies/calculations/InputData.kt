package com.reducetechnologies.calculations

import com.reducetechnologies.specificationsAndRequests.Specifications

data class InputData(
    /**
     * [isED] - нужен ли подбор электродвигателя
     * [TT] - момент на тихоходном валу, Н*м
     * [NT] - частота вращения тихоходного вала, мин(-1)
     * [LH] - ресурс работы, ч
     * [NRR] - номер режима работы (0...5)
     * [KOL] - количество редукторов в серии, шт. в год (по умолчанию 10000)
     * [U0] - передаточное отношение побочной цепи между редуктором и ЭД, если его нет - U0 = 1
     * [UREMA] - передаточное отношение редуктора (максимально желаемое, если будет производиться
     * подбор ЭД, и предварительно рассчитанное, если ЭД не будет подбираться)
     * [TIPRE] - тип редуктора (1...9)
     * [NP] - признак передачи
     * [BETMI] - минимальный угол наклона зубьев, градусы
     * [BETMA] - максимальный угол наклона зубьев, градусы
     * [OMEG] - коэффициент неравномерности нагрузки по потокам
     * [NW] - число потоков
     * [NZAC1, NZAC2] - число зацеплений шестерни и колеса
     * [NWR] - число полушевронов
     * [BKAN] - коэффициент наличия канавки между полушевронами
     * [SIGN] - признак зацепления (1 - внешнее, -1 - внутреннее), тихоходная ступень - 1 индекс в
     * массивеб быстроходная - 0
     * [CONSOL] - признак шестерни на консоли (false - нет, true - да)
     * [KPD] - КПД редуктора,
     * [ISTCol] - число ступеней
     * [ALF] - угол профиля зуба
     * [HL] - коэффициент граничной высоты зуба
     * [HA] - коэффициент высоты головки зуба
     * [HG] - коэффициент высоты модификации головки
     * [C] - коэффициент радиального зазора
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
    var BETMI: Float,//в таблице задан в градусах, поэтому нужно в инпуте переводить домножением на 180 и делением на ПИ
    var BETMA: Float,//в таблице задан в градусах, поэтому нужно в инпуте переводить домножением на 180 и делением на ПИ
    val OMEG: Float,
    val NW: Int,
    var NZAC: Array<Int>,
    val NWR: Int,
    val BKAN: Int,
    val SIGN: Array<Int>,
    val CONSOL: Array<Int>,
    val KPD: Float,
    val ISTCol: Int,//Просто задаём количество ступеней
    val wheelType: Specifications.WheelType,
    val wheelSubtype: Array<Specifications.WheelSubtype>,
    val PAR: Boolean,//характеризует наличие/отсутствие планетарной передачи

    //стандартный угол профиля 20 градусов или 0.349 радиан
    val ALF: Float = 0.349f,
    val HL: Float = 2f,//вроде взял из учебника, но лучше всё равно чекнуть
    val HA: Float = 1f,//вроде взял из учебника, но лучше всё равно чекнуть
    val HG: Float = 0.0f,//0.4f,//взял единственное найденное в интернете вроде более-менее стандартное значение
    val C: Float = 0.25f
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InputData

        if (isED != other.isED) return false
        if (TT != other.TT) return false
        if (NT != other.NT) return false
        if (LH != other.LH) return false
        if (NRR != other.NRR) return false
        if (KOL != other.KOL) return false
        if (U0 != other.U0) return false
        if (UREMA != other.UREMA) return false
        if (TIPRE != other.TIPRE) return false
        if (NP != other.NP) return false
        if (BETMI != other.BETMI) return false
        if (BETMA != other.BETMA) return false
        if (OMEG != other.OMEG) return false
        if (NW != other.NW) return false
        if (!NZAC.contentEquals(other.NZAC)) return false
        if (NWR != other.NWR) return false
        if (BKAN != other.BKAN) return false
        if (!SIGN.contentEquals(other.SIGN)) return false
        if (!CONSOL.contentEquals(other.CONSOL)) return false
        if (KPD != other.KPD) return false
        if (ISTCol != other.ISTCol) return false
        if (wheelType != other.wheelType) return false
        if (!wheelSubtype.contentEquals(other.wheelSubtype)) return false
        if (PAR != other.PAR) return false
        if (ALF != other.ALF) return false
        if (HL != other.HL) return false
        if (HA != other.HA) return false
        if (HG != other.HG) return false
        if (C != other.C) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isED.hashCode()
        result = 31 * result + TT.hashCode()
        result = 31 * result + NT.hashCode()
        result = 31 * result + LH
        result = 31 * result + NRR
        result = 31 * result + KOL
        result = 31 * result + U0.hashCode()
        result = 31 * result + UREMA.hashCode()
        result = 31 * result + TIPRE
        result = 31 * result + NP
        result = 31 * result + BETMI.hashCode()
        result = 31 * result + BETMA.hashCode()
        result = 31 * result + OMEG.hashCode()
        result = 31 * result + NW
        result = 31 * result + NZAC.contentHashCode()
        result = 31 * result + NWR
        result = 31 * result + BKAN
        result = 31 * result + SIGN.contentHashCode()
        result = 31 * result + CONSOL.contentHashCode()
        result = 31 * result + KPD.hashCode()
        result = 31 * result + ISTCol
        result = 31 * result + wheelType.hashCode()
        result = 31 * result + wheelSubtype.contentHashCode()
        result = 31 * result + PAR.hashCode()
        result = 31 * result + ALF.hashCode()
        result = 31 * result + HL.hashCode()
        result = 31 * result + HA.hashCode()
        result = 31 * result + HG.hashCode()
        result = 31 * result + C.hashCode()
        return result
    }

    override fun toString(): String {
        return "InputData(isED=$isED, TT=$TT, NT=$NT, LH=$LH, NRR=$NRR, KOL=$KOL, U0=$U0, UREMA=$UREMA, TIPRE=$TIPRE, NP=$NP, BETMI=$BETMI, BETMA=$BETMA, OMEG=$OMEG, NW=$NW, NZAC=${NZAC.contentToString()}, NWR=$NWR, BKAN=$BKAN, SIGN=${SIGN.contentToString()}, CONSOL=${CONSOL.contentToString()}, KPD=$KPD, ISTCol=$ISTCol, wheelType=$wheelType, wheelSubtype=${wheelSubtype.contentToString()}, PAR=$PAR, ALF=$ALF, HL=$HL, HA=$HA, HG=$HG, C=$C)"
    }

}