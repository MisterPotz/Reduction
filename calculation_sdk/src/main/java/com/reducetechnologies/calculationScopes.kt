package com.reducetechnologies

import com.reducetechnologies.calculation_util.SelectionTree
import com.reducetechnologies.calculation_util.TreeBuilder
import com.reducetechnologies.specificationsAndRequests.Specifications
import com.reducetechnologies.tables.*
import com.sun.org.apache.bcel.internal.generic.FLOAD
import org.omg.CORBA.ARG_IN
import java.lang.Exception
import kotlin.math.*

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
 * [CONSOL] - признак шестерни на консоли (false - нет, true - да)
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
    val CONSOL: Array<Boolean>,
    val KPD: Float,
    val IST: Int,
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
            var SH: Float? = null
            var SGH0: Float? = null
            var SGF0: Float? = null
            var POKST: Float? = null
            var POKSTF: Float? = null
            //Начало основного цикла
            SelectionTree.rootSelection {
                for (i in 0..1) {
                    var NH0: Float = 340 * (args.option.HRC[i].pow(3.15f)) + 8_000_000f
                    select("Свойства материала", TreeBuilder.build
                        //если больше 50
                        .c { args.option.HRC[i] > 35 }
                        .a {
                            select("HRC >= 50", TreeBuilder.build
                                .c { args.option.HRC[i] >= 50 }
                                .a {
                                    SH = 1.2f
                                    SGH0 = 23 * args.option.HRC[i]
                                    SGF0 = 850f
                                    wheelsSGHMD[i] = 40 * args.option.HRC[i].toInt()
                                    wheelsSGFMD[i] = 1450
                                    POKST = 1 / 6f
                                    POKSTF = 1 / 9f
                                    select("KFC != 1", TreeBuilder.build
                                        .c { args.KFC[i] != 1f }
                                        .a { wheelsKFC[i] = 0.90f }
                                    )
                                }
                                .a {
                                    SH = 1.2f
                                    SGH0 = 17 * args.option.HRC[i] + 200
                                    SGF0 = 550f
                                    wheelsSGHMD[i] = 40 * args.option.HRC[i].toInt()
                                    wheelsSGFMD[i] = 1430
                                    POKST = 1 / 6f
                                    POKSTF = 1 / 6f
                                    select("KFC != 1 при HRC <= 35", TreeBuilder.build
                                        .c { args.KFC[i] != 1f }
                                        .a { wheelsKFC[i] = 0.75f }
                                    )
                                }
                            )
                        }
                        // если между 35 и 50
                        .a {
                            SH = 1.1f
                            SGH0 = 20 * args.option.HRC[i] + 70
                            SGF0 = 18 * args.option.HRC[i]
                            wheelsSGHMD[i] = (2.8 * SGTT.getValue(args.option.HRC[i])[i]).toInt()
                            wheelsSGFMD[i] = (27.4 * args.option.HRC[i]).toInt()
                            POKST = 1 / 6f
                            POKSTF = 1 / 6f
                            select("KFC != 1 при 35 < HRC < 50", TreeBuilder.build
                                .c { args.KFC[i] != 1f }
                                .a { wheelsKFC[i] = 0.65f }
                            )
                        }
                    )
                    select("Уже задались некоторыми параметрами", TreeBuilder.build
                        .c { NHEArr[i] > NH0 }
                        .a { NHEArr[i] = NH0 }
                    )
                    var KHL: Float = (NH0 / NHEArr[i]).pow(POKST!!)
                    select("Задание KHL", TreeBuilder.build
                        .c { KHL >= 2.6f && args.option.HRC[i] <= 35 }
                        .c { KHL >= 1.8f && args.option.HRC[i] > 35 }
                        .a { KHL = 2.6f }
                        .a { KHL = 1.8f }
                    )
                    //учитывает окружную скорость
                    var ZETV: Float? = null
                    select("Задание ZETV", TreeBuilder.build
                        .c { args.V > 5 && args.option.HRC[i] > 35 }
                        .c { args.V > 5 }
                        .a { ZETV = 0.925f * (args.V.pow(0.05f)) }
                        .a { ZETV = 0.85f * (args.V.pow(0.1f)) }
                        .a { ZETV = 1f }
                    )
                    //Определение ещё некоторых характеристик колеса
                    wheelsSGHD[i] = ((SGH0!! / SH!!) * KHL * args.ZETR * ZETV!!).toInt()
                    NS = 60 * args.inputData.LH * NArr[i] * args.inputData.NZAC[i]
                    select("Определение NFE", TreeBuilder.build
                        .c { args.option.HRC[i] > 35 }
                        .a { NFEArr[i] = NS * KFE * (args.inputData.NRR + 1.2f) }
                        .a { NFEArr[i] = NS * KFE * (args.inputData.NRR + 1.1f) }
                    )
                    select("NFE при невыполнении 1ого условия", TreeBuilder.build
                        .c { NFEArr[i] > NF0 }
                        .a { NFEArr[i] = NF0 }
                    )
                    var KFL: Float = (NF0 / NFEArr[i]).pow(POKSTF!!)
                    select("Задание KFL", TreeBuilder.build
                        .c { KFL >= 2.08 && args.option.HRC[i] <= 35 }
                        .c { KFL > 1.63 && args.option.HRC[i] > 35 }
                        .a { KFL = 2.08f }
                        .a { KFL = 1.63f }
                    )
                    var YSG: Float? = null
                    select("Задание YSG", TreeBuilder.build
                        .c { args.M == 3f }
                        .a { YSG = 1f }
                        .a { YSG = 1.18f - 0.1f * sqrt(args.M) + 0.006f * args.M }
                    )
                    wheelsSGFD[i] = ((SGF0!! / SF) * wheelsKFC[i] * KFL * YSG!! * YR).toInt()
                }
                //Вышли из цикла
                //NP - это индекс типа передачи (см спецификацию)
                //Следующая строчка - САМЫЙ НЕПОНЯТНЫЙ МОМЕНТ!!! Нужно правильно выбрать SGHMD
                SGHMD = min(wheelsSGHMD[0], wheelsSGHMD[1])//Здесь max или min?
                select("Логика в зависимости от NP", TreeBuilder.build
                    .c { args.inputData.NP < 3 }
                    .a {
                        select("Сравнение HRC", TreeBuilder.build
                            .c { args.option.HRC[0] > args.option.HRC[1] + 7 }
                            .a {
                                select("Задача SGHD в зав от HRC", TreeBuilder.build
                                    .c { args.option.HRC[1] < 35 }
                                    .a {
                                        SGHD =
                                            (0.45 * (args.option.HRC[0] + args.option.HRC[1])).toInt()
                                        when (args.inputData.wheelType) {
                                            Specifications.WheelType.CYLINDRICAL -> if (SGHD!! >
                                                1.23 * args.option.HRC[1]
                                            ) {
                                                SGHD = (1.23 * args.option.HRC[1]).toInt()
                                            }
                                            Specifications.WheelType.CONE -> if (SGHD!! >
                                                1.15 * args.option.HRC[1]
                                            ) {
                                                SGHD = (1.15 * args.option.HRC[1]).toInt()
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    }
                )
                SGHD = min(wheelsSGHD[0], wheelsSGHD[1])//Здесь max или min?
            }
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

object ZUC1HMethods {
    data class Arguments(
        var u: Float,
        var T2: Float,//похоже тоже только для соосных
        val PSI1: Float,
        val DSGH: Int = 0,
        val DSGF: Int = 0,
        val AW: Float = 0f,//этот AW используется исключительно для TIPRE = 4, в него должен
        //класться AW из IST = 1
        val dopnScope: DOPNScope,
        val option: ReducerOptionTemplate,
        val inputData: InputData,
        val IST: Int
    )
    private var SCHET: Int = 0
    private var SCHET1: Int = 0
    private var SCHET2: Int = 0

    private fun AWChoose(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.AW = RA40.first { it > zuc1HScope.AW }//Ура мать твою, я ляМБДА программист!!1!
        //Идём в BWChoose
        BWChoose(args, zuc1HScope)
    }

    private fun MChoose(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            if (args.DSGF == 0)
                MR = 1.039f*BW!!/(args.option.PSIM*args.inputData.NWR)
            M = mStand.first { it > MR!! }
            //Возвращаемся в BWChoose
            return
        }
    }

    private fun BWChoose(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            if (PSIBA != null)
                BW = PSIBA!!*AW + 0.5f //0.5f точно нужен?
            else
                BW = args.option.PSB*AW + 0.5f
            BW1 = (BW!!*args.PSI1 + 1f).toInt()//1f нужно для правильного округления в больш сторону
            BW2 = (BW!! + 1f).toInt()

        }
        //Идём в MChoose
        MChoose(args, zuc1HScope)
        //Идём в следующую программу
        BETChoose(args, zuc1HScope)
    }

    private fun BETChoose(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            if (BET > 0 || args.inputData.BETMA == 0f) {//Там почему то BETMI, но по логике это вернее
                //Идём в подбор Z
                ZCalculate(args, zuc1HScope)
                return
            }
            if (4f*M!!/BW!! < 1) {
                BET = atan(4f*M!!/(BW!!* sqrt(1 - (4f*M!!/BW!!).pow(2))))
            }
            else if (4f*M!!/BW!! >= 1 || BET < args.inputData.BETMI) {
                BET = args.inputData.BETMI
                //Идём в подбор Z
                ZCalculate(args, zuc1HScope)
                return
            }
            if (BET!! > args.inputData.BETMA) {
                BW = 4f*M!!/ sin(args.inputData.BETMA)
                BW1 = (BW!!*args.PSI1 + 1f).toInt()//1f нужно для правильного округления в больш сторону
                BW2 = (BW!! + 1f).toInt()
                BET = args.inputData.BETMA
                //Идём в подбор Z
                ZCalculate(args, zuc1HScope)
                return
            }
            else {
                //Идём в подбор Z
                ZCalculate(args, zuc1HScope)
                return
            }
        }
    }

    private fun Z1tipre4(args: Arguments, zuc1HScope: ZUC1HScope) {
        //some logic
        zuc1HScope.apply {
            var CEL1: Float = abs(Z2R!! - args.inputData.SIGN*Z1!!).toFloat()/args.inputData.NW.toFloat()
            var CEL2: Int = CEL1.roundToInt()
            SCHET2++
            while (CEL1 > CEL2.toFloat()) {
                if (SCHET2 <= 1)
                    Z1 = Z1!! - 1
                if (SCHET2 > 1)
                    Z1 = Z1!! + 1
                CEL1 = abs(Z2R!! - args.inputData.SIGN*Z1!!).toFloat()/args.inputData.NW.toFloat()
                CEL2 = CEL1.roundToInt()
            }
            return
        }
    }

    private fun invW(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            INVW = 2*XSUM* tan(args.inputData.ALF)/ZSUM + INVAT!!
            var ALFR: Float = 1.316111f*(INVW!!).pow(0.290865f) - 0.03808f
            var INVAR: Float = tan(ALFR) - ALFR
            while (INVW!! < INVAR) {
                ALFR -= 0.0000002f
                INVAR = tan(ALFR) - ALFR
            }
            ALFTW = ALFR
            if ((args.inputData.TIPRE == 4 && args.IST == 0) || AWFS) {
                whereToGo(args, zuc1HScope)
                return
            }
            AW = M!!*ZSUM* cos(ALFT!!)/(2* cos(BET)* cos(ALFTW!!))
            if (BETFS && (X1FS || X2FS)) {
                //Уход в расчёт U
                uCalculate(args, zuc1HScope)
                return
            }
            if (args.inputData.BETMI == 0f && (X1FS || X2FS)) {
                //Уход в расчёт U
                uCalculate(args, zuc1HScope)
                return
            }
            SCHET1++
            if (SCHET1 < 2) {
                AWChoose(args, zuc1HScope)
                return
            }
            whereToGo(args, zuc1HScope)
            return
        }
    }

    private fun whereToGo(args: Arguments, zuc1HScope: ZUC1HScope) {
        if (args.inputData.BETMI > 0f) {
            //Уход в cosBET
            cosBET(args, zuc1HScope)
            return
        }
        else {
            //Уход в cosALF
            cosALF(args, zuc1HScope)
            return
        }
    }

    private fun cosALF(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            var Y2: Float = M!!*ZSUM* cos(ALFT!!)/(2* cos(BET)*AW)
            while (Y2 > cos(ALFT!!)) {
                Z2 = Z2!! - 1
                ZSUM = Z2!! + args.inputData.SIGN*Z1!!
                Y2 = M!!*ZSUM* cos(ALFT!!)/(2* cos(BET)*AW)
            }
            ALFTW = atan(sqrt(1f - Y2.pow(2))/Y2)
            INVAT = tan(ALFT!!) - ALFT!!
            var INVATW: Float = tan(ALFTW!!) - ALFTW!!
            XSUM = (INVATW - INVAT!!)*(Z2!! + args.inputData.SIGN*Z1!!)/(2*tan(args.inputData.ALF))
            if (abs(XSUM) <= 0.001) {
                //Уход в расчёт U
                uCalculate(args, zuc1HScope)
                return
            }
            var X1FSLocal: Float = X1//нужна как вспомогательная переменная для обмена значениями
            //из-за дебильного непонятного кода на фортране
            X1 = 0.5f
            if (Z1!! >= Z1R!!)
                X1 = 0.3f
            if (X1FS && X1FSLocal > 0f)
                X1 = X1FSLocal
            X2 = XSUM - args.inputData.SIGN*X1
            if (args.inputData.SIGN > 0f || SCHET > 2) {
                //Уход в расчёт U
                uCalculate(args, zuc1HScope)
                return
            }
            if (X2 <= 1.5f) {
                //Уход в расчёт U
                uCalculate(args, zuc1HScope)
                return
            }
            else {
                Z2 = Z2!! + 1
                SCHET++
                ZSUM = Z2!! + args.inputData.SIGN*Z1!!
                //Уход в cosALF
                cosALF(args, zuc1HScope)
                return
            }
        }
    }

    private fun cosBET(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            var Y3: Float = ZSUM!!*M!!* cos(ALFT!!)/(2*AW!!* cos(ALFTW!!))
            while (Y3 >= 1) {
                Z2 = Z2!! - 1
                ZSUM = Z2!! + args.inputData.SIGN*Z1!!
                Y3 = ZSUM!!*M!!* cos(ALFT!!)/(2*AW!!* cos(ALFTW!!))
            }
            BET = atan(sqrt(1f - Y3.pow(2))/Y3)
            ALFT = atan(tan(args.inputData.ALF)/ cos(BET))
            if (XSUM == 0f)
                ALFTW = ALFT
            //Уход в расчёт U
            uCalculate(args, zuc1HScope)
            return
        }
    }

    private fun uCalculate(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            UCalculated = (Z2!!.toFloat()/Z1!!.toFloat())
            if (args.inputData.NW != 1 && args.IST == 1)
                Z2R = Z2
            //Задание всех FS, счётчики обнуляются в начале функции enter
            FSTrue(zuc1HScope)
            return
        }
    }

    private fun FSTrue(zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            X1FS = true
            X2FS = true
            AWFS = true
            BETFS = true
            return
        }
    }

    private fun ZCalculate(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            ZSUM = (2*AW* cos(BET)/M!! + 1f).toInt()//1f для правильного округления
            Z1 = (ZSUM/(args.u + args.inputData.SIGN) + 1f).toInt()
            if (Z1!! <= 10) {
                Z1 = 10
                if (!(args.inputData.TIPRE == 4 || args.IST == 0))
                    ZSUM = (Z1!!*(args.u + args.inputData.SIGN) + 1f).toInt()//1f для правильного округления
            }
            if (!(args.inputData.NW == 1 || args.IST == 1)) {
                //Идём в числа зубьев многопоточных редукторов
                Z1tipre4(args, zuc1HScope)
            }
            Z2 = ZSUM - args.inputData.SIGN*Z1!!
            if (!((args.inputData.TIPRE == 4 && args.IST == 0) || !AWFS)) {
                var AWR: Float = ZSUM*M!!/(2*cos(BET))
                if (AWR <= AW) {
                    //Идём в 105
                    anglePrecise(args, zuc1HScope)
                    return
                }
                SCHET++
                if (SCHET > 3) {
                    //Идём в 105
                    anglePrecise(args, zuc1HScope)
                    return
                }
                //AW определяется по напряжениям изгиба
                if (AWR > AW) {
                    AW = AWR
                    AWChoose(args, zuc1HScope)
                    return
                }
                //Здесь непонятный момент, зачем пересчитывать AW, если мы его не перезадаём? Пока
                //оставил, как думаю, что правильно
                anglePrecise(args, zuc1HScope)
                return
            }
        }
    }

    private fun anglePrecise(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            if (!(BET > 0f || args.inputData.BETMI == 0f)) {
                //уточнение угла наклона
                var Y1 = ZSUM*M!!/(2*AW)
                while (Y1 >= 1) {
                    Z2 = Z2!! - 1
                    ZSUM = Z2!! + args.inputData.SIGN*Z1!!
                    Y1 = ZSUM*M!!/(2*AW)
                }
                BET = atan(sqrt((1 - Y1.pow(2))/Y1))
            }
            ALFT = atan(tan(args.inputData.ALF)/ cos(BET))
            INVAT = tan(ALFT!!) - ALFT!!
            ALFTW = ALFT!!
            SCHET = 0
            ZMI = (2*(args.inputData.HL - args.inputData.HA - X1)*cos(BET)/(sin(ALFTW!!)).pow(2) +
                    1f).toInt()//1f только для правильного укругления в большую сторону здесь
            Z1R = (2*(args.inputData.HL - args.inputData.HA - X1)*cos(BET)/(sin(ALFTW!!)).pow(2) +
                    3f).toInt()
            if (X1 != 0f || X2 != 0f) {
                XSUM = X2 + args.inputData.SIGN*X1
                //Идём в invALF
                invW(args, zuc1HScope)
                return
            }
            else {
                SelectionTree.rootSelection {
                    select("Подбор смещений в зависимотси от условий", TreeBuilder.build
                        .c { args.inputData.SIGN < 0 && (Z2!! < 80 || Z1!! < 18) }
                        .a { X1 = 0.3f }
                        .c { Z1!! < ZMI!! && Z1!! >= Z1R!! }
                        .a { X1 = 0.3f }
                        .c { Z1!! < ZMI!! && Z1!! < Z1R!! }
                        .a { X1 = 0.5f }
                        .c { Z2!! < 28f }
                        .a { X2 = -X1*args.inputData.SIGN }
                        .c { Z2!! < ZMI!! && Z2!! >= Z1R!! }//почему здесь тоже Z1R, а не Z2R?
                        .a { X2 = 0.3f }
                        .c { Z2!! < ZMI!! && Z2!! < Z1R!! }
                        .a { X2 = 0.5f }
                    )
                    XSUM = X2 + args.inputData.SIGN*X1
                }
                if (XSUM == 0f && BETFS) {
                    //Уход в cosALF
                    cosALF(args, zuc1HScope)
                    return
                }
                if (XSUM == 0f && args.inputData.BETMI > 0f) {
                    //Уход в расчёт U
                    uCalculate(args, zuc1HScope)
                    return
                }
                if (XSUM == 0f && args.inputData.BETMI == 0f) {
                    //Уход в cosALF
                    cosALF(args, zuc1HScope)
                    return
                }
                //Идём в invALF
                invW(args, zuc1HScope)
                return
            }
        }
    }

    fun enterZUC2H(args: Arguments, zuc1HScope: ZUC1HScope) {
        SCHET = 1
        SCHET1 = 0
        SCHET2 = 0
        //пока выпущу логику для NW > 1 и TIPRE = 4, потому что не особо понимаю
        zuc1HScope.apply {
            if (args.DSGH == 0) {
                if (args.inputData.TIPRE == 4 && args.inputData.NW > 1)
                    Z2R = Z2//Не понимаю надобность ZR
                if (BET + args.inputData.BETMI > 0)
                    AKA = 430f
                else
                    AKA = 495f
                KHB = 1.2f//это в фортране, в схеме по другому, нужно поискать
                KHV = 1f
            }
            if (args.inputData.TIPRE == 4 && args.IST == 0) {
                AW = args.AW//Для соосных
                PSIBA = (1.1f*args.T2*KHB!!*KHV!!/(args.u*args.dopnScope.SGHD!!).pow(2))*
                        (AKA!!*(args.u*args.inputData.SIGN)/AW).pow(3)
                if (PSIBA!! < 0)
                    PSIBA = 0.2f
                if (args.DSGF > 0)
                    PSIBA = PSIBA!!*(1f + args.DSGF/args.dopnScope.wheelsSGFD[1])
                //Идём в BWChoose
                BWChoose(args, zuc1HScope)
            }
            else {
                if (AWFS) {
                    T2 = ((AW/(1.1f*AKA!!*(args.u + args.inputData.SIGN))).pow(3))*
                            (args.u*args.dopnScope.SGHD!!).pow(2)*args.option.PSB/(KHB!!*KHV!!)
                    //Идём в BWChoose
                    BWChoose(args, zuc1HScope)
                }
                else {
                    AW = 1.05f*AKA!!*(args.u + args.inputData.SIGN)*(args.T2*KHB!!*KHV!!/(args.u *
                            args.dopnScope.SGHD!!).pow(2)*args.option.PSB).pow(1/3f)
                    //Идём в AWChoose
                    AWChoose(args, zuc1HScope)
                }
            }
        }
    }
}

//Все входные данные для ZC2RED
//Ещё нужно будет дополнять

object ZUCEPMethods {
    data class Arguments(
        val N2: Float,
        val zuc1HScope: ZUC1HScope,
        val inputData: InputData,
        val HA: Float,//пока буду брать из inputData, потом продумать
        val HL: Float,//пока буду брать из inputData, потом продумать
        val HG: Float,//высота притупления кромки зуба,
        var HK1: Float = inputData.HG,//подумать, возможно не лучший вариант хранить их здесь
        var HK2: Float = inputData.HG,//подумать, возможно не лучший вариант хранить их здесь
        val C: Float,//пока буду брать из inputData, потом продумать
        val ROF: Float,
        val EPMI: Float = 1.2f,//принимаем по умолчанию
        var Z0: Int = 0,//задаётся потом
        var X0: Float = 0f,//принимается стандартно, но может быть другим
        val D0: Float,
        var DA0: Float
    )

    fun enterZUCEP(args: Arguments, zucepScope: ZUCEPScope) {
        args.zuc1HScope.apply {
            //Они здесь сначала пересчитывают AW, но я не понимаю, зачем это нужно, возможно потом
            //пригодится, не забудь этот момент, пока ничего не буду писать
            var A: Float = AW* cos(ALFTW!!)/ cos(ALFT!!)//чтобы потом найти DELY
            zucepScope.D[0] = M!!*Z1!!/ cos(BET)
            zucepScope.D[1] = M!!*Z2!!/ cos(BET)
            zucepScope.DB[0] = zucepScope.D[0]* cos(ALFT!!)
            zucepScope.DB[1] = zucepScope.D[1]* cos(ALFT!!)
            if (!args.inputData.PAR)
                zucepScope.DELY = XSUM - (AW - A)/M!!
            zucepScope.DA[0] = zucepScope.D[0] + 2*(args.inputData.HA + X1 - zucepScope.DELY)*M!!
            zucepScope.DA[1] = zucepScope.D[1] + 2*(args.inputData.HA + X2 - zucepScope.DELY)*M!!
            zucepScope.DF[0] = zucepScope.D[0] - 2*(args.inputData.HA + args.inputData.C - X1)*M!!
            zucepScope.DF[1] = zucepScope.D[1] - 2*(args.inputData.HA + args.inputData.C - X2)*M!!
            if (args.inputData.SIGN < 0) {
                //Уход в diamInternalTeeth
                return
            }
            else {
                //Уход в hollow
                return
            }
        }
    }

    private fun diamInternalTeeth(args: Arguments, zucepScope: ZUCEPScope) {
        args.zuc1HScope.apply {
            var K2: Float = 0.25f - 0.125f*X2
            if (X2 >= 2f)
                K2 = 0f
            zucepScope.DA[1] = zucepScope.D[1] - 2*(args.inputData.HA - X2 + zucepScope.DELY - K2)*M!!
            if (args.Z0 == 0) {
                args.Z0 = (0.3f*Z2!!).roundToInt()
                args.DA0 = M!!*(args.Z0 + 2*(args.inputData.HA + args.inputData.C + args.X0))
            }
            zucepScope.Z0 = args.Z0
            INVW = INVAT!! + 2*(X2 - args.X0)* tan(args.inputData.ALF)/(Z2!! - args.Z0)
            var ALFR: Float = 1.316111f*(INVW!!).pow(0.290865f) - 0.03806f
            var INVAR: Float = tan(ALFR) - ALFR
            while (INVW!! < INVAR) {
                ALFR -= 0.000002f
                INVAR = tan(ALFR) - ALFR
            }
            zucepScope.ALFW02 = ALFR
            zucepScope.AW02 = M!!*(Z2!! - args.Z0)* cos(ALFT!!)/(2* cos(BET)* cos(zucepScope.ALFW02!!))
            zucepScope.DF[1] = 2*zucepScope.AW02!! + args.DA0
            var Y: Float = M!!*args.Z0* cos(ALFT!!)/args.DA0
            zucepScope.ALFA0 = atan(sqrt((1f - Y.pow(2))/Y))
            //Уход в hollow
            hollow(args, zucepScope)
            return
        }
    }

    private fun hollow(args: Arguments, zucepScope: ZUCEPScope) {
        zucepScope.apply {
            DK[0] = DA[0] - 2*args.HK1*args.zuc1HScope.M!!
            DK[1] = DA[1] - args.inputData.SIGN*2*args.HK2*args.zuc1HScope.M!!
            ALFK[0] = acos(DB[0]/DK[0])
            ALFK[1] = acos(DB[1]/DK[1])
            ROP[0] = args.inputData.SIGN*(args.zuc1HScope.AW*sin(args.zuc1HScope.ALFTW!!) -
                    DB[1]*tan(ALFK[1])/2)
            ROP[1] = (args.zuc1HScope.AW*sin(args.zuc1HScope.ALFTW!!) -
                    args.inputData.SIGN*DB[0]*tan(ALFK[0])/2)
            ROL[0] = D[0]*sin(args.zuc1HScope.ALFT!!)/2 - (args.inputData.HL -
                    args.inputData.HA - args.zuc1HScope.X1)*args.zuc1HScope.M!!/sin(args.zuc1HScope.ALFT!!)
            if (args.inputData.SIGN < 0)
                ROL[1] = AW02!!*sin(ALFW02!!) + args.DA0*sin(args.zuc1HScope.ALFT!!)/2
            else
                ROL[1] = D[1]* sin(args.zuc1HScope.ALFT!!) /2 - args.inputData.SIGN*(args.inputData.HL
                        - args.inputData.HA - args.inputData.SIGN*args.zuc1HScope.X2)*
                        args.zuc1HScope.M!!/sin(args.zuc1HScope.ALFT!!)
            CF[0] = args.inputData.SIGN*(args.zuc1HScope.AW - DK[1]/2) - DF[0]/2
            CF[1] = args.inputData.SIGN*(args.zuc1HScope.AW - DF[1]/2) - DK[0]/2
            if (ROL[0] < ROP[0] && CF[0] > 0.15f*args.zuc1HScope.M!!) {
                //Уход в case 1
                case1(args, zucepScope)
                return
            }
            else {
                //Уход в case 2
                case2(args, zucepScope)
                return
            }
        }
    }

    private fun case1(args: Arguments, zucepScope: ZUCEPScope) {
        zucepScope.apply {
            if (args.inputData.SIGN*(ROL[1] - ROP[1]) <= 0f && CF[1] > 0.15f*args.zuc1HScope.M!!) {
                //Уход в calculationOfQualityIndicators
                return
            }
            if (args.inputData.PAR && args.inputData.SIGN < 0) {
                //Уход в fullIndicatorsCalculation
                return
            }
            args.HK1 += 0.05f*args.zuc1HScope.M!!
            if (args.HK1 > 0.2f*args.zuc1HScope.M!!){
                //Уход в fullIndicatorsCalculation
                return
            }
            else {
                //Уход в hollow
                hollow(args, zucepScope)
                return
            }
        }
    }

    private fun case2(args: Arguments, zucepScope: ZUCEPScope) {
        args.apply {
            HK2 += 0.05f*zuc1HScope.M!!
            if (HK2 > 0.2f*zuc1HScope.M!!) {
                //Уход в fullIndicatorsCalculation
                return
            }
            else {
                //Уход в hollow
                hollow(args, zucepScope)
                return
            }
        }
    }

    private fun fullIndicatorsCalculation(args: Arguments, zucepScope: ZUCEPScope) {
        //Здесь наверное нужно придумать логику для отброса такого варианта
        println("Произошла интерференция во впадинах или слишком малый радиальный зазор\n" +
                "(либо отбросить вариант, либо дать рекомендации по применению неизношенного\n" +
                "долбяка) \n")
        if (args.HK1 > 0 || args.inputData.PAR) {
            //Уход в calculationOfQualityIndicators
            return
        }
        else {
            //Уход в case 1
            case1(args, zucepScope)
            return
        }
    }

    private fun calculationOfQualityIndicators(args: Arguments, zucepScope: ZUCEPScope) {
        zucepScope.apply {
            EA[0] = (args.zuc1HScope.Z1!!/2* PI.toFloat())*(tan(ALFK[0]) - tan(args.zuc1HScope.ALFTW!!))
            EA[1] = (args.inputData.SIGN*args.zuc1HScope.Z2!!/2* PI.toFloat())*
                    (tan(ALFK[1]) - tan(args.zuc1HScope.ALFTW!!))
            EPALF = EA[0] + EA[1]
            EAM = max(EA[0], EA[1])
            if (EPALF!! < args.EPMI) {
                //Нужно тоже придумать отсеивание
                println("Коэффициент перекрытия меньше нормы ${args.EPMI} и равен: $EPALF")
            }
            DA[0] = DK[0]
            DA[1] = DK[1]
            var V2: Float = PI.toFloat()*D[1]*args.N2*cos(args.zuc1HScope.ALFT!!)/(6000f*
                    cos(args.zuc1HScope.ALFTW!!))
            var VS: Float = 2*V2* sin(args.zuc1HScope.ALFTW!!)
            var FTRZ: Float = 1.25f*(0.102f - 0.02f*VS.pow(1/3f))
            var PSIZR: Float = 2* PI.toFloat()*FTRZ*(1/args.zuc1HScope.Z1!! +
                    args.inputData.SIGN/args.zuc1HScope.Z2!!)
            if (args.zuc1HScope.BET > 0f) {
                PSIZC = PSIZR*(EA[0].pow(2) + EA[1].pow(2))/(2*EPALF!!* cos(args.zuc1HScope.BET))
            }
            else
                PSIZC = PSIZR*(1f - EPALF!! + 0.5f*EPALF!!.pow(2))*EAM!!/EPALF!!
            return
        }
    }
}

object ZUC2HMethods {
    data class Arguments(
        val edScope: EDScope,
        val option: ReducerOptionTemplate,
        val zuc1hScope: ZUC1HScope,
        val zucepScope: ZUCEPScope,
        val dopnScope: DOPNScope,
        val inputData: InputData,
        val N2: Float,
        val T2: Float,
        var ST: Int = 0,
        val CONSOL: Boolean,//просто инициализировать значениями из массива из inputData
        var SHEM: Int = 0,//инициализируется в программе
        var DELH: Float? = null,//инициализируется в программе
        val E1: Long = 200000,//пока заглушка, потом нужно найти, какие данные вбивать
        val E2: Long = 200000,//пока заглушка, потом нужно найти, какие данные вбивать
        val PUAS: Float = 0.3f//пока заглушка, потом нужно найти, какие данные вбивать
    )

    fun enterZUC2H(args: Arguments, zuc2hScope: ZUC2HScope) {
        zuc2hScope.apply {
            DW[0] = 2*args.zuc1hScope.AW/(args.zuc1hScope.UCalculated + args.inputData.SIGN)
            DW[1] = DW[0]*args.zuc1hScope.UCalculated
            V = PI.toFloat()*DW[1]*args.N2/60000
            if (args.ST > 0 && args.zuc1hScope.BET > 0f) {
                KHALF = (0.0026f*args.ST - 0.013f)*V!! + 0.027f*args.ST + 0.84f
                //Уход в calculatePSIBD
                calculatePSIBD(args, zuc2hScope)
                return
            }
            else if (args.zuc1hScope.BET > 0f) {
                args.ST = (10.1f - 0.12f*V!!).toInt()
                KHALF = (0.0026f*args.ST - 0.013f)*V!! + 0.027f*args.ST + 0.84f
                //Уход в calculatePSIBD
                calculatePSIBD(args, zuc2hScope)
                return
            }
            else if (args.ST > 0) {
                KHALF = 1f
                //Уход в calculatePSIBD
                calculatePSIBD(args, zuc2hScope)
                return
            }
            else {
                args.ST = (10.1f - 0.2f*V!!).toInt()
                KHALF = 1f
                //Уход в calculatePSIBD
                calculatePSIBD(args, zuc2hScope)
                return
            }
        }
    }

    private fun calculatePSIBD(args: Arguments, zuc2hScope: ZUC2HScope) {
        zuc2hScope.apply {
            PSIBD = args.zuc1hScope.BW!!/DW[0]
            var PSIDR: Float = PSIBD!!/args.inputData.NWR
            if (args.zuc1hScope.KHB!! > 1f) {
                //Уход в HRC2Check
                HRC2Check(args, zuc2hScope)
                return
            }
            else if (args.option.HRC[1] > 35f) {
                //Уход в HRC1Check
                HRC1Check(args, zuc2hScope, PSIDR)
                return
            }
            else {
                if (args.option.HRC[0] <= 35f)
                    args.SHEM = 4
                else if (args.option.HRC[0] > 35f)
                    args.SHEM = 3
                if (args.CONSOL)
                    args.SHEM = 2
                args.zuc1hScope.KHB = 1f + 0.51f*PSIDR/args.SHEM
                //Уход в HRC2Check
                HRC2Check(args, zuc2hScope)
                return
            }
        }
    }

    private fun HRC1Check(args: Arguments, zuc2hScope: ZUC2HScope, PSIDR: Float) {
        args.apply {
            if (option.HRC[0] <= 45f)
                SHEM = 4
            else if (args.option.HRC[0] > 45f)
                SHEM = 3
            if (CONSOL)
                SHEM = 2
            zuc1hScope.KHB = 1f + 1.1f*PSIDR/SHEM
            //Уход в HRC2Check
            HRC2Check(args, zuc2hScope)
            return
        }
    }

    private fun HRC2Check(args: Arguments, zuc2hScope: ZUC2HScope) {
        args.apply {
            if (option.HRC[1] <= 35f) {
                if (zuc1hScope.BET < 0f)
                    DELH = 0.006f
                if (abs(inputData.HG) > 0)//Странное условие, мб просто !=0 поставит?
                    DELH = 0.004f
                if (zuc1hScope.BET > 0f)
                    DELH = 0.002f
                //Уход в finalCalc
                return
            }
            else {
                if (zuc1hScope.BET < 0f)
                    DELH = 0.014f
                if (abs(inputData.HG) > 0)//Странное условие, мб просто !=0 поставит?
                    DELH = 0.01f
                if (zuc1hScope.BET > 0f)
                    DELH = 0.004f
                //Уход в finalCalc
                return
            }
        }
    }

    private fun finalCalc(args: Arguments, zuc2hScope: ZUC2HScope) {
        zuc2hScope.apply {
            G0 = 10f*args.ST + args.zuc1hScope.M!!*((0.16f*args.ST - 0.78f)*(args.ST).toFloat().pow(0.4f) +
                    0.58f) - 22f
            WHV = args.DELH!!*G0!!*V!!* sqrt(args.zuc1hScope.AW/args.zuc1hScope.UCalculated)
            WV = args.zuc1hScope.M!!*(4f*args.ST - 15f) - 0.1f*args.ST.toFloat().pow(4)
            if (WHV!! > WV!!)
                WHV = WV!!
            FT = 2000f*args.T2/DW[1]
            FR = FT!!* tan(args.zuc1hScope.ALFTW!!)
            FA = FT!!*tan(args.zuc1hScope.BET)
            args.zuc1hScope.KHV = 1f + WHV!!*args.zuc1hScope.BW!!/FT!!
            EPBET = args.zuc1hScope.BW!!* sin(args.zuc1hScope.BET)/(PI.toFloat()*args.zuc1hScope.M!!)
            if (EPBET!! >= 0.9f)
                ZEP = sqrt(1f/args.zucepScope.EPALF!!)
            else {
                ZEP = sqrt((4f - args.zucepScope.EPALF!!)/3f)
            }
            var ZM: Float = sqrt(2f*args.E1*args.E2/((args.E1 + args.E2)* PI.toFloat()*(1f - args.PUAS.pow(2))))
            var sinBTB: Float = sin(args.zuc1hScope.BET)* cos(args.inputData.ALF)
            var BETB: Float = atan(sinBTB/ sqrt(1f - sinBTB.pow(2)))
            var ZH: Float = sqrt(2f* cos(BETB)/ sin(2f*args.zuc1hScope.ALFTW!!))
            WHT = FT!!*args.zuc1hScope.KHB!!*args.zuc1hScope.KHV!!*KHALF!!/args.zuc1hScope.BW!!
            SGH = ZH*ZM*ZEP!!* sqrt(WHT!!*(args.zuc1hScope.UCalculated + args.inputData.SIGN)/
                    (DW[0]*args.zuc1hScope.UCalculated))
            if (args.edScope.TTED != null) {
                SGHM = SGH!! * sqrt(args.edScope.TTED!!)//нужно брать из
            }//Потом это нужно получше обработать, потому что электродвигатель будет не всегда считаться
            args.zuc1hScope.AKA = (500f*(ZH*ZM*ZEP!!).pow(2)*KHALF!!).pow(1/3f)
            ST = args.ST//просто чтобы сохранить в scope
        }
    }
}

object ZUCFMethods {
    data class Arguments(
        val inputData: InputData,
        val edScope: EDScope,
        val dopnScope: DOPNScope,
        val option: ReducerOptionTemplate,
        var zuc1hScope: ZUC1HScope,
        val zuc2hScope: ZUC2HScope,
        val zucepScope: ZUCEPScope,
        val YEP: Int = 1,//коэффициент, учитывающий перекрытие зубьев, задают 1
        val CONSOL: Boolean
    )

    fun enterZUCF(args: Arguments, zucfScope: ZUCFScope) {
        args.apply {
            if (zuc2hScope.EPBET!! > 1f)
                zuc2hScope.KHALF = (4f + (zucepScope.EPALF!! - 1f)*(zuc2hScope.ST!! - 5f))/(4f*
                        zucepScope.EPALF!!)
            else
                zuc2hScope.KHALF = 1f
            if (abs(inputData.HG) > 0f)
                zucfScope.DELF = 0.011f
            else if (zuc1hScope.BET > 0f)
                zucfScope.DELF = 0.006f
            else
                zucfScope.DELF = 0.016f
            zucfScope.WFV = zucfScope.DELF!!*zuc2hScope.G0!!*zuc2hScope.V!!* sqrt(zuc1hScope.AW/
            zuc1hScope.UCalculated)
            if (zucfScope.WFV!! > zuc2hScope.WV!!)
                zucfScope.WFV = zuc2hScope.WV
            zucfScope.KFV = 1f + zucfScope.WFV!!*zuc1hScope.BW1!!/zuc2hScope.FT!!
            if (zuc1hScope.KFB!! > 1f) {
                //Уход в parametersCalc
                return
            }
            var PSIDR: Float = zuc2hScope.PSIBD!!
            if (inputData.NWR > 1f)
                PSIDR /= inputData.NWR
            if (option.HRC[1] <= 35f) {
                var SHEM: Int
                if (option.HRC[0] <= 35)
                    SHEM = 4
                else if (option.HRC[0] > 35)
                    SHEM = 3
                else (CONSOL)
                    SHEM = 2
                zuc1hScope.KFB = 1f + 1.1f*PSIDR/SHEM
                //Уход в parametersCalc
                return
            }
            else {
                var SHEM: Int
                if (option.HRC[0] <= 45)
                    SHEM = 4
                else if (option.HRC[0] > 45)
                    SHEM = 3
                else (CONSOL)
                    SHEM = 2
                zuc1hScope.KFB = 1f + 1.8f*PSIDR/SHEM
                //Уход в parametersCalc
                return
            }
        }
    }

    private fun parametersCalc(args: Arguments, zucfScope: ZUCFScope) {
        args.apply {
            //Шестерня
            oneWheelCalc(args = args,
                zucfScope = zucfScope,
                Z = zuc1hScope.Z1!!,
                X = zuc1hScope.X1,
                BW = zuc1hScope.BW1!!,
                wheelNumber = 0)
            //Колесо
            //Там очень странная логика относительно Z0, пока что буду использовать просто уже
            //рассчитанное в zucep значение
            if (inputData.SIGN < 0)
                oneWheelCalc(args = args,
                    zucfScope = zucfScope,
                    Z = -1*zuc1hScope.Z2!!,
                    X = zuc1hScope.X2,
                    BW = zuc1hScope.BW2!!,
                    wheelNumber = 1)
            else
                oneWheelCalc(args = args,
                    zucfScope = zucfScope,
                    Z = zuc1hScope.Z2!!,
                    X = zuc1hScope.X2,
                    BW = zuc1hScope.BW2!!,
                    wheelNumber = 1)
            return
        }
    }

    private fun oneWheelCalc(args: Arguments,
                             zucfScope: ZUCFScope,
                             Z: Int,
                             X: Float,
                             BW: Int,
                             wheelNumber: Int//0 или 1 в зависимости от шестерни ил колеса
                             ) {
        args.apply {
            var WFT: Float = zuc2hScope.FT!!*zuc2hScope.KHALF!!*zuc1hScope.KFB!!*zucfScope.KFV!!/BW
            var ZV: Float = abs(Z/ (cos(zuc1hScope.BET)).pow(3))
            var YF: Float
            if (Z < abs(Z)) {//максимально тупая проверка на отрицательность
                YF = 4.3f - (8f/zucepScope.Z0!!.toFloat().pow(0.8f))*(1f + 0.23f*X) - 0.33f*
                        zucepScope.Z0!!*0.0001f*(180f - ZV)*(1f + 56f*X/zucepScope.Z0!!.toFloat()
                    .pow(0.8f))//дибильнейшие вычисления, если что ты знаешь, где проверять
            }
            else {
                YF = 3.6f * (1f + (112f * X.pow(2) - 154f * X + 71) / ZV.pow(2) -
                        (2.8f * X + 0.93f) / ZV)
            }
            var YBET: Float = 1f - (zuc1hScope.BET/140f)*(180f/ PI.toFloat())
            if (YBET < 0.7f)
                YBET = 0.7f
            zucfScope.SGF[wheelNumber] = YEP*YBET*YF*WFT/zuc1hScope.M!!
            zucfScope.SGFM[wheelNumber] = zucfScope.SGF[wheelNumber]*edScope.TTED!!
        }
    }

}

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
    var SGHD: Int? = null,
    var wheelsSGHD: Array<Int> = Array(2){-1},
    var SGHMD: Int? = null,
    var wheelsSGHMD: Array<Int> = Array(2){-1},
    var wheelsSGFD: Array<Int> = Array(2){-1},
    var wheelsSGFMD: Array<Int> = Array(2){-1},
    var wheelsKFC: Array<Float> = Array(2){-1f}


)

data class ZUC1HScope(
    var Z1: Int? = null,
    var Z2: Int? = null,
    var ZSUM: Int = 0,
    var Z1R: Int? = null,
    var Z2R: Int? = null,//только для TIPRE = 4
    var ZMI: Int? = null,
    var X1: Float = 0f,
    var X2: Float = 0f,
    var X1FS: Boolean = false,//Для того, чтобы понять, уже входили в этот цикл и подбирали или нет
    var X2FS: Boolean = false,//Для того, чтобы понять, уже входили в этот цикл и подбирали или нет
    var XSUM: Float = 0f,
    var AW: Float = 0f,
    var AWFS: Boolean = false,//Для того, чтобы понять, уже входили в этот цикл и подбирали или нет
    var M: Float? = null,
    var BW: Float? = null,
    var BW1: Int? = null,
    var BW2: Int? = null,
    var BET: Float = 0f,
    var BETFS: Boolean = false,//Для того, чтобы понять, уже входили в этот цикл и подбирали или нет
    var ALFT: Float? = null,
    var ALFTW: Float? = null,
    var INVAT: Float? = null,
    var INVW: Float? = null,
    var KHV: Float? = null,
    var MR: Float? = null,
    var AKA: Float? = null,
    var KHB: Float? = null,
    var KFB: Float? = null,
    var PSIBA: Float? = null,
    var T2: Float? = null,
    var UCalculated: Float = 0f
)

data class ZUCEPScope(
    var D: Array<Float> = Array(2){-1f},
    var DB: Array<Float> = Array(2){-1f},//диаметры основных окружностей
    var DA: Array<Float> = Array(2){-1f},//диаметры окружностей вершин зубьев колёс
    var DF: Array<Float> = Array(2){-1f},//диаметры окружностей впадин колёс
    var CF: Array<Float> = Array(2){-1f},//действительный зазор с зацеплении
    var DK: Array<Float> = Array(2){-1f},//диаметры окружностей притупленных кромок колёс
    var ALFK: Array<Float> = Array(2){-1f},//угол профиля зуба в точке притупленных кромок
    var ROP: Array<Float> = Array(2){-1f},
    var ROL: Array<Float> = Array(2){01f},
    var PSIZC: Float? = null,//коэффициент потерь в зацеплении
    var DELY: Float = 0f,
    var EA: Array<Float> = Array(2){-1f},
    var EAM: Float? = null,
    var EPALF: Float? = null,
    var ALFW02: Float? = null,//угол зацепления долбяка с колесом
    var AW02: Float? = null,//межосевое расстояние в станочном зацеплении долбяка с колесом
    var ALFA0: Float? = null,//угол профиля зуба долбяка на окружности вершин
    var Z0: Int? = null
)

data class ZUC2HScope(
    var DW: Array<Float> = Array(2){-1f},
    var V: Float? = null,
    var KHALF: Float? = null,
    var PSIBD: Float? = null,
    var G0: Float? = null,
    var WHV: Float? = null,
    var WV: Float? = null,
    var FT: Float? = null,
    var FR: Float? = null,
    var FA: Float? = null,
    var EPBET: Float? = null,
    var ZEP: Float? = null,
    var WHT: Float? = null,
    var SGH: Float? = null,
    var SGHM: Float? = null,
    var ST: Int? = null
)

data class ZUCFScope(
    var SGF: Array<Float> = Array(2){-1f},
    var SGFM: Array<Float> = Array(2){-1f},
    var DELF: Float? = null,
    var WFV: Float? = null,
    var KFV: Float? = null
)

// здесь будут храниться все дата скоупы, чтобы потом передать только 1 объект в конструктор редуктора
data class CreationData(
    val dopnScope: DOPNScope,
    val edScope: EDScope,
    val zuc1HScope: ZUC1HScope,
    val zucepScope: ZUCEPScope
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