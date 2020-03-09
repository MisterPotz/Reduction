package com.reducetechnologies

import com.reducetechnologies.calculation_util.SelectionTree
import com.reducetechnologies.calculation_util.TreeBuilder
import com.reducetechnologies.specificationsAndRequests.EngineRequest
import com.reducetechnologies.specificationsAndRequests.ReducerCreationRequest
import com.reducetechnologies.specificationsAndRequests.Specifications
import com.reducetechnologies.tables.*
import java.lang.Exception
import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * [isED] - нужен ли подбор редуктора
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
 * [CONSOL] - признак шестерни на консоли (0 - нет, 1 - да)
 * [KPD] - КПД редуктора,
 * [IST] - число ступеней
 */
data class InputData(
    //С ввода пользователя
    val isED: Boolean,
    val TT: Float,
    val NT: Int,
    val LH: Int,
    val NRR: Int,
    val KOL: Int = 10000,
    val U0: Float = 1f,
    val UREMA: Float,

    //Рассчитываются исходя из картинки
    val TIPRE: Int,
    val NP: Int,
    val BETMI: Float,
    val BETMA: Float,
    val OMEG: Float,
    val NW: Int,
    val NZAC: Array<Int>,
    val NWR: Int,
    val BKAN: Int,
    val SIGN: Int,
    val CONSOL: Int,
    val KPD: Float,
    val IST: Int,
    val wheelType: Specifications.WheelType,
    val wheelSubtype: Array<Specifications.WheelSubtype>
)

data class ReducerOptionTemplate(
    var NED: Int? = null, val HRC: Array<Float>, val uB: Float, val uT: Float, val u: Float,
    val uRatio: Float, val uRED: Float, val PSB: Float, val PSIM: Int, val PED: Float,
    val EDScope: EDScope? = null
)

object allReducersOptions {
    /**
     * [NEDOptions] - частота вращения электродвигателя
     * 0 -> 3000, 1 -> 1500, 2 -> 1000, 3 -> 750
     * [HRC] - пары твёрдости для колёс одной ступени
     * [uRatio] - отношение передаточного числа быстроходной ступени к тихоходной
     * [PSB] - коэффициент ширины колеса по межосевому расстоянию
     */
    val NEDOptions: MutableList<Int> = mutableListOf(0, 1, 2, 3)
    val HRC: Array<Array<Float>> = arrayOf(
        arrayOf(28.5f, 24.8f), arrayOf(49f, 28.5f),
        arrayOf(59f, 59f)
    )
    val uRatio: Array<Float> = arrayOf(0.7f, 1f, 1.3f)
    val PSB: Array<Float> = arrayOf(0.25f, 0.4f)


    /**
     * Спросить про правильность работы с null и в общем в этой функции
     */
    fun calculateOptionsTemplates(
        inputData: InputData
    ): List<ReducerOptionTemplate> {
        var options: MutableList<ReducerOptionTemplate> = mutableListOf()
        //Некоторая логика перед циклами
        //val edMethods: EDMethods = EDMethods
        var pedCalculated: Float = (inputData.TT * inputData.NT / (9550f * inputData.KPD))//расчётное значение мощности редуктора
        var URED: Float
        if (pedCalculated >= 15)
            println("P is more then 15, so we wont choose ED for you")
        if (inputData.U0 != 1f)//если присутствует промежуточная передача между электродвигателем и редуктором
            pedCalculated *= 0.96f
        //Вход в циклы
        for (ned in NEDOptions) {
            var edScope = EDScope()
            //Функция подбора электродвигателя из стандартного ряда
            if (inputData.isED) {
                if (pedCalculated < 15) {
                    EDMethods.EDCalculate(
                        EDMethods.Arguments(PEDCalculated = pedCalculated, NEDFixed = ned),
                        edScope
                    )
                }
            }
            if (edScope.NED != null) {
                URED = edScope.NED!! / (inputData.NT * inputData.U0)
            } else URED = inputData.UREMA //если не подбираем редуктор или его невозможно подобрать
            if (URED > inputData.UREMA)//UREMA - должно вводиться пользователем, максимальное перед отношение
                continue
            else {
                for (hrc: Array<Float> in HRC) {
                    var PSIM: Int? = null
                    if (hrc[0] <= 35)
                        PSIM = 30//расхождения с диаграммой, спросить
                    else if (hrc[0] > 35 && hrc[0] <= 50)
                        PSIM = 25
                    else if (hrc[0] > 50)
                        PSIM = 20
                    for (uRatio in this.uRatio) {
                        var uB: Float = sqrt(URED * uRatio)
                        var uT: Float = URED / uB
                        for (psb in PSB) {
                            //NED будет null, если не будет расчёта редуктора
                            //psb1 нужен только чтобы учесть случай с NWR > 1
                            var psb1: Float
                            if (inputData.NWR > 1)
                                psb1 = 2*psb
                            else psb1 = psb
                            options.add(
                                ReducerOptionTemplate(
                                    NED = edScope.NED,
                                    HRC = hrc,
                                    uB = uB,
                                    uT = uT,
                                    u = uB*uT,
                                    uRatio = uRatio,
                                    uRED = URED,
                                    PSB = psb1,
                                    PSIM = PSIM!!,
                                    PED = pedCalculated,
                                    EDScope = edScope
                                )
                            )
                        }

                    }
                }
            }
        }
        return options
    }
}

fun calculateAllOptions(options: List<ReducerOptionTemplate>) {
    val dopnScope = DOPNScope()
    val dopnMethods: DOPN_Methods = DOPN_Methods
    for (option in options) {
        DOPN_Methods.dopn(DOPN_Methods.Arguments(option = option), dopnScope)
        // Отсылаем все к чертям
        ReducerFabric(CreationData(dopnScope.copy())).createReducer()
    }
}//эта функция по идее должна возвращать массив рассчитанных редукторов

object DOPN_Methods {
    data class Arguments(
        var N2: Float,
        var u: Float,//тк может передаваться для одной ступени => здесь могут быть uT, uB
        val KFC: Array<Float> = arrayOf(0f, 0f),//чтобы условие на KFC == 1 не срабатывало

        var ZETR: Float = 1f,
        var V: Float = 3f,
        var M: Float = 3f,
        val inputData: InputData,
        val option: ReducerOptionTemplate
    )

    fun dopn(args: Arguments, dopnScope: DOPNScope) {
        dopnScope.apply {
            //Логика для определения KHE и KFE по таблицам
            val KHE: Float = 1f
            val KFE: Float = 1f
            //
            val NF0: Float = 4_000_000f//базовое число циклов
            val SF: Float = 1.75f//при вероятности неразрушения до 99%, свыше SF>=2 тогда
            val YR: Float = 1f//для фрезерованых и шлифованых зубьев, для полир - 1.2
            //подумать над логикой для SF и YR
            //Подумать, как правильно получать N1 and N2
            var NS: Float = 60 * args.inputData.LH * args.N2 * args.inputData.NZAC[1]
            val NHE2: Float =
                NS * KHE * (args.inputData.NRR + 1)//эквивалентное число циклов при расчёте на вынссл
            var NHE1: Float = NHE2 * args.u * args.inputData.NZAC[0] / args.inputData.NZAC[1]
            //эквивалентное число циклов для шестерни
            var NHEArr: Array<Float> = arrayOf(NHE1, NHE2)
            var NFEArr: Array<Float> = Array(2) { 0f }
            var NArr: Array<Float> = arrayOf(args.N2*args.u, args.N2)
            //Объявление некоторых переменных, которые не нужны в выводе и требуются только
            //для некоторых промежуточных расчётов
            /**
             * [SH] - Коэф безопасности при расчёте на контактную прочность
             * [SGH0] - длительный предел выносливости при контактных напряжениях
             * [SGF0] - длительный предел выносливости при контактных напряжениях
             * [POKST], [POKSTF] - показатели степени
             */
            var SH: Float?
            var SGH0: Float?
            var SGF0: Float?
            var POKST: Float?
            var POKSTF: Float?
            //Начало основного цикла
                for (i in 0..1) {
                    var NH0: Float = 340 * (args.option.HRC[i].pow(3.15f)) + 8_000_000f
                    if (args.option.HRC[i] > 35) {
                        //если больше 50
                        if (args.option.HRC[i] >= 50) {
                            SH = 1.2f
                            SGH0 = 23*args.option.HRC[i]
                            SGF0 = 850f
                            wheelsSGHMD[i] = 40*args.option.HRC[i].toInt()
                            wheelsSGFMD[i] = 1450
                            POKST = 1/6f
                            POKSTF = 1/9f
                            if (args.KFC[i] != 1f)
                                wheelsKFC[i] = 0.90f
                        } else {
                            SH = 1.2f
                            SGH0 = 17*args.option.HRC[i] + 200
                            SGF0 = 550f
                            wheelsSGHMD[i] = 40*args.option.HRC[i].toInt()
                            wheelsSGFMD[i] = 1430
                            POKST = 1/6f
                            POKSTF = 1/6f
                            if (args.KFC[i] != 1f)
                                wheelsKFC[i] = 0.75f
                        }// если между 35 и 50
                    } else {
                        SH = 1.1f
                        SGH0 = 20*args.option.HRC[i] + 70
                        SGF0 = 18*args.option.HRC[i]
                        wheelsSGHMD[i] = (2.8* SGTT.getValue(args.option.HRC[i])[i]).toInt()
                        wheelsSGFMD[i] = (27.4*args.option.HRC[i]).toInt()
                        POKST = 1/6f
                        POKSTF = 1/6f
                        if (args.KFC[i] != 1f)
                            wheelsKFC[i] = 0.65f
                    }
                    //Начало немного другой логики, уже задались некоторыми параметрами
                    if (NHEArr[i] > NH0)
                        NHEArr[i] = NH0
                    var KHL: Float = (NH0 / NHEArr[i]).pow(POKST)
                    if (KHL >= 2.6f && args.option.HRC[i] <= 35)
                        KHL = 2.6f
                    else if (KHL >= 1.8f && args.option.HRC[i] > 35)
                        KHL = 1.8f
                    var ZETV: Float? //учитывает окружную скорость
                    if (args.V > 5 && args.option.HRC[i] > 35)
                        ZETV = 0.925f * (args.V.pow(0.05f))
                    else if (args.V > 5)
                        ZETV = 0.85f * (args.V.pow(0.1f))
                    else
                        ZETV = 1f
                    //Определение ещё некоторых характеристик колеса
                    wheelsSGHD[i] = ((SGH0 / SH) * KHL * args.ZETR * ZETV).toInt()
                    NS = 60 * args.inputData.LH * NArr[i] * args.inputData.NZAC[i]
                    if (args.option.HRC[i] > 35)
                        NFEArr[i] = NS * KFE * (args.inputData.NRR + 1.2f)
                    else
                        NFEArr[i] = NS * KFE * (args.inputData.NRR + 1.1f)
                    if (NFEArr[i] > NF0)
                        NFEArr[i] = NF0
                    var KFL: Float = (NF0 / NFEArr[i]).pow(POKSTF)
                    if (KFL >= 2.08 && args.option.HRC[i] <= 35)
                        KFL = 2.08f
                    else if (KFL > 1.63 && args.option.HRC[i] > 35)
                        KFL = 1.63f
                    var YSG: Float
                    if (args.M == 3f)
                        YSG = 1f
                    else
                        YSG = 1.18f - 0.1f * sqrt(args.M) + 0.006f * args.M
                    wheelsSGFD[i] = ((SGF0/SF)*wheelsKFC[i]*KFL*YSG*YR).toInt()
                }
                //Вышли из цикла
                //NP - это индекс типа передачи (см спецификацию)
                //Следующая строчка - САМЫЙ НЕПОНЯТНЫЙ МОМЕНТ!!! Нужно правильно выбрать SGHMD
            SGHMD = min(wheelsSGHMD[0], wheelsSGHMD[1])//Здесь max или min?
                if (args.inputData.NP < 3) {
                    if (args.option.HRC[0] > args.option.HRC[1] + 7) {
                        if (args.option.HRC[1] < 35) {
                            SGHD =
                                (0.45 * (args.option.HRC[0] + args.option.HRC[1])).toInt()
                            when (args.inputData.wheelType) {
                                Specifications.WheelType.CYLINDRICAL -> if (SGHD >
                                    1.23 * args.option.HRC[1]
                                ) {
                                    SGHD = (1.23 * args.option.HRC[1]).toInt()
                                }
                                Specifications.WheelType.CONE -> if (SGHD >
                                    1.15 * args.option.HRC[1]
                                ) {
                                    SGHD = (1.15 * args.option.HRC[1]).toInt()
                                }
                            }
                        }
                    }
                }
            SGHD = min(wheelsSGHD[0], wheelsSGHD[1])//Здесь max или min?

            return
        }
    }
}


object EDMethods {
    data class Arguments(var PEDCalculated: Float, var NEDFixed: Int)

    fun EDCalculate(args: Arguments, edScope: EDScope) {
        for (key in PEDS.keys) {
            if (key > args.PEDCalculated) {
                edScope.apply {
                    PED = key
                    NED = PEDS.getValue(key)[args.NEDFixed]
                    TTED = TTEDS.getValue(key)[args.NEDFixed]
                    D1ED = D1EDS.getValue(key)[args.NEDFixed]
                    L1ED = L1ES.getValue(key)[args.NEDFixed]
                    H1ED = H1EDS.getValue(key)[args.NEDFixed]
                    MAED = MAEDS.getValue(key)[args.NEDFixed]
                }
                return
            }
        }
        throw Exception("We didnt find ED with P more then PEDCalculated (we have not more then P=15)")
    }//Выбор электродвигателя из стандартного ряда

}

//Все входные данные для ZC2RED
//Ещё нужно будет дополнять
data class inputDataScope(
    var TT: Float? = null,
    var NT: Int? = null,
    var KPD: Float? = null,
    var U0: Float? = null
)

data class EDScope(
    var PED: Float? = null,
    var NED: Int? = null,
    var TTED: Float? = null,
    var D1ED: Int? = null,
    var L1ED: Int? = null,
    var H1ED: Int? = null,
    var MAED: Float? = null
)

//это класс для функции DOPN, здесь будут храниться все её инициализируемые переменные
data class DOPNScope(
    var SGHD: Int = null,
    var wheelsSGHD: Array<Int> = null,
    var SGHMD: Int = null,
    var wheelsSGHMD: Array<Int> = null,
    var wheelsSGFD: Array<Int> = null,
    var wheelsSGFMD: Array<Int> = null,
    var wheelsKFC: Array<Float> = null


)

// здесь будут храниться все дата скоупы, чтобы потом передать только 1 объект в конструктор редуктора
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
    fun createReducer(): Reducer {
        return object : Reducer() {
            override val stage: Stage = object : Stage() {
                override val type: Int
                    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            }
        }
    }
}