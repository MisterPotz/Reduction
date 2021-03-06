package com.reducetechnologies.calculations

import com.reducetechnologies.tables_utils.table_contracts.RA40Table
import com.reducetechnologies.tables_utils.table_contracts.StandartModulesTable
import kotlin.math.*

class ZUC1HMethodsClass(val RA40: RA40Table, val MStandart: StandartModulesTable) {
    data class Arguments(
        val SIGN: Int,
        var u: Float,
        var T2: Float,
        val PSI1: Float = 1.1f,//это значение оставим стандартным
        //val DSGH: Int = 0,задал их в scope
        //val DSGF: Int = 0,
        val AW: Float = 0f,//этот AW используется исключительно для TIPRE = 4, в него должен
        //класться AW из IST = 1
        val dopnScope: DOPNScope,
        val option: ReducerOptionTemplate,
        val inputData: InputData,
        val IST: Int//Здесь это ступень, тихоходная это 1, быстроходная это 0, пока примем, что
    //только при 1ой ступени мы будем считать её за быстроходную (0)
    )
    private var SCHET: Int = 0
    private var SCHET1: Int = 0
    private var SCHET2: Int = 0

    private fun AWChoose(args: Arguments, zuc1HScope: ZUC1HScope) {
        if (RA40.list.last() <= zuc1HScope.AW!!){
            zuc1HScope.AW = RA40.list.last()
        }
        else {
            zuc1HScope.AW = RA40.list.first { it >= zuc1HScope.AW }//Ура мать твою, я ляМБДА программист!!1!
        }
        //Идём в BWChoose
        BWChoose(args, zuc1HScope)
    }

    /*
    Это моя добавочная функция, которая нужна только для того, чтобы не было дробного межосевого
    расстояния. Она применяется только 1 раз
     */
    private fun AWChooseReturnHelp(args: Arguments, zuc1HScope: ZUC1HScope) {
        if (RA40.list.last() <= zuc1HScope.AW!!){
            zuc1HScope.AW = RA40.list.last()
        }
        else {
            zuc1HScope.AW = RA40.list.first { it >= zuc1HScope.AW }//Ура мать твою, я ляМБДА программист!!1!
        }
        return
    }

    private fun BETlimitationHighHelp(args: Arguments, zuc1HScope: ZUC1HScope){
        zuc1HScope.apply {
            if (BET > args.inputData.BETMA) {
                BET = args.inputData.BETMA
            }
        }
    }

    private fun MChoose(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            if (DSGF == 0f)
                MR = 1.039f*BW!!/(args.option.PSIM*args.inputData.NWR)
            if (MStandart.list.last() <= MR!!){
                args.dopnScope.M = MStandart.list.last()
            }
            else {
                args.dopnScope.M = MStandart.list.first { it >= MR!! }
            }
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
            if (4f*args.dopnScope.M/BW!! < 1) {
                BET = atan(
                    4f * args.dopnScope.M / (BW!! * sqrt(
                        1 - (4f * args.dopnScope.M / BW!!).pow(2)
                    ))
                )
            }
            if (4f*args.dopnScope.M/BW!! >= 1 || BET < args.inputData.BETMI) {
                BET = args.inputData.BETMI
                //Идём в подбор Z
                ZCalculate(args, zuc1HScope)
                return
            }
            if (BET > args.inputData.BETMA) {
                BW = 4f*args.dopnScope.M/ sin(args.inputData.BETMA)
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
        //По сути, это критерий некратности в передачах с многопарным зацеплением, он даже здесь выполняется
        zuc1HScope.apply {
            var CEL1: Float = abs(Z2R!! - args.SIGN * Z1!!).toFloat()/args.inputData.NW.toFloat()
            var CEL2: Int = CEL1.toInt()
            while (CEL1 > CEL2.toFloat()) {
                SCHET2++
                if (SCHET2 <= 1)
                    Z1 = Z1!! - 1
                if (SCHET2 > 1)
                    Z1 = Z1!! + 1
                CEL1 = abs(Z2R!! - args.SIGN * Z1!!).toFloat()/args.inputData.NW.toFloat()
                CEL2 = CEL1.toInt()
            }
            return
        }
    }

    private fun invW(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            INVW = 2*XSUM* tan(args.inputData.ALF) /ZSUM + INVAT!!
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
            AW = args.dopnScope.M*ZSUM* cos(ALFT!!) /(2* cos(BET) * cos(
                ALFTW!!
            ))
            AWChooseReturnHelp(args, zuc1HScope)
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
        if (zuc1HScope.BETFS) {
            //Уход в cosALF
            cosALF(args, zuc1HScope)
            return
        }
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
            var Y2: Float = args.dopnScope.M*ZSUM* cos(ALFT!!) /(2* cos(BET) *AW)
            while (Y2 > cos(ALFT!!)) {
                Z2 = Z2!! - 1
                ZSUM = Z2!! + args.SIGN*Z1!!
                Y2 = args.dopnScope.M*ZSUM* cos(ALFT!!) /(2* cos(BET) *AW)
            }
            ALFTW = atan(sqrt(1f - Y2.pow(2)) / Y2)
            INVAT = tan(ALFT!!) - ALFT!!
            var INVATW: Float = tan(ALFTW!!) - ALFTW!!
            XSUM = (INVATW - INVAT!!)*(Z2!! + args.SIGN*Z1!!)/(2* tan(args.inputData.ALF))
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
            X2 = XSUM - args.SIGN*X1
            if (args.SIGN > 0f || SCHET > 2) {
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
                ZSUM = Z2!! + args.SIGN*Z1!!
                //Уход в cosALF
                cosALF(args, zuc1HScope)
                return
            }
        }
    }

    private fun cosBET(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            var Y3: Float = ZSUM*args.dopnScope.M* cos(ALFT!!) /(2*AW* cos(
                ALFTW!!
            ))
            while (Y3 >= 1) {
                Z2 = Z2!! - 1
                ZSUM = Z2!! + args.SIGN*Z1!!
                Y3 = ZSUM*args.dopnScope.M* cos(ALFT!!) /(2*AW* cos(
                    ALFTW!!
                ))
            }
            BET = atan(sqrt(1f - Y3.pow(2)) / Y3)
            if (BET < args.inputData.BETMI){
                BET = args.inputData.BETMI
            }//Этого нет в фортране, но это условие здесь определённо нужно, чтобы не получать углы меньше минимальных
            //BETlimitationHighHelp(args, zuc1HScope)//Для ограничения по максимально возможному углу
            ALFT = atan(tan(args.inputData.ALF) / cos(BET))
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
            ZSUM = (2*AW* cos(BET) /args.dopnScope.M + 1f).toInt()//1f для правильного округления
            Z1 = (ZSUM/(args.u + args.SIGN) + 1f).toInt()
            if (Z1!! <= 10) {
                Z1 = 10
                if (!(args.inputData.TIPRE == 4 || args.IST == 0))
                    ZSUM = (Z1!!*(args.u + args.SIGN)).roundToInt()
            }
            if (!(args.inputData.NW == 1 || args.IST == 1)) {
                if (DSGH == 0f) {
                    Z2R = ZSUM - args.SIGN*Z1!!//Этой строчки нет, но без неё никак не задать Z2R
                }
                //Идём в числа зубьев многопоточных редукторов
                Z1tipre4(args, zuc1HScope)
            }
            Z2 = ZSUM - args.SIGN*Z1!!
            if (!((args.inputData.TIPRE == 4 && args.IST == 0) || AWFS)) {
                var AWR: Float = ZSUM*args.dopnScope.M/(2* cos(BET))
                if (AWR <= AW) {
                    //Идём в 105
                    anglePrecise(args, zuc1HScope)
                    return
                }
                SCHET++
                if (SCHET > 15) {
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
            }
            //Здесь непонятный момент, зачем пересчитывать AW, если мы его не перезадаём? Пока
            //оставил, как думаю, что правильно
            anglePrecise(args, zuc1HScope)
            return
        }
    }

    private fun anglePrecise(args: Arguments, zuc1HScope: ZUC1HScope) {
        zuc1HScope.apply {
            if (!(BETFS || args.inputData.BETMI == 0f)) {
                //уточнение угла наклона
                var Y1 = ZSUM*args.dopnScope.M/(2*AW)
                while (Y1 >= 1) {
                    Z2 = Z2!! - 1
                    ZSUM = Z2!! + args.SIGN*Z1!!
                    Y1 = ZSUM*args.dopnScope.M/(2*AW)
                }
                BET = atan(sqrt((1 - Y1.pow(2)) / Y1))
                if (BET < args.inputData.BETMI){
                    BET = args.inputData.BETMI
                }//Этого нет в фортране, но это условие здесь определённо нужно, чтобы не получать углы меньше минимальных
                //BETlimitationHighHelp(args, zuc1HScope)//Для ограничения по максимально возможному углу
            }
            ALFT = atan(tan(args.inputData.ALF) / cos(BET))
            INVAT = tan(ALFT!!) - ALFT!!
            ALFTW = ALFT!!
            SCHET = 0
            ZMI = (2*(args.inputData.HL - args.inputData.HA - X1)* cos(BET) /(sin(
                ALFTW!!
            )).pow(2) +
                    1f).toInt()//1f только для правильного укругления в большую сторону здесь
            Z1R = (2*(args.inputData.HL - args.inputData.HA - X1)* cos(BET) /(sin(
                ALFTW!!
            )).pow(2) +
                    3f).toInt()
            if (X1 != 0f || X2 != 0f) {
                XSUM = X2 + args.SIGN*X1
                //Идём в invW
                invW(args, zuc1HScope)
                return
            }
            else {
                //Подбор смещений в зависимости от условий
                if (args.SIGN < 0 && (Z2!! < 80 || Z1!! < 18)){
                    X1 = 0.3f
                }
                if (Z1!! < ZMI!! && Z1!! >= Z1R!!){
                    X1 = 0.3f
                }
                if (Z1!! < ZMI!! && Z1!! < Z1R!!){
                    X1 = 0.5f
                }
                if (Z2!! < 28f){
                    X2 = -X1 * args.SIGN
                }
                if (Z2!! < ZMI!! && Z2!! >= Z1R!!){
                    X2 = 0.3f
                }
                if (Z2!! < ZMI!! && Z2!! < Z1R!!){
                    X2 = 0.5f
                }
                XSUM = X2 + args.SIGN * X1
                /*SelectionTree.rootSelection {
                    select("Подбор смещений в зависимотси от условий",
                        TreeBuilder.build
                            .c { args.SIGN < 0 && (Z2!! < 80 || Z1!! < 18) }
                            .a { X1 = 0.3f }
                            .c { Z1!! < ZMI!! && Z1!! >= Z1R!! }
                            .a { X1 = 0.3f }
                            .c { Z1!! < ZMI!! && Z1!! < Z1R!! }
                            .a { X1 = 0.5f }
                            .c { Z2!! < 28f }
                            .a { X2 = -X1 * args.SIGN }
                            .c { Z2!! < ZMI!! && Z2!! >= Z1R!! }//почему здесь тоже Z1R, а не Z2R?
                            .a { X2 = 0.3f }
                            .c { Z2!! < ZMI!! && Z2!! < Z1R!! }
                            .a { X2 = 0.5f }
                    )
                    XSUM = X2 + args.SIGN * X1
                }*/
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

    fun enterZUC1H(args: Arguments, zuc1HScope: ZUC1HScope) {
        SCHET = 1
        SCHET1 = 0
        SCHET2 = 0
        //пока выпущу логику для NW > 1 и TIPRE = 4, потому что её нет в коде
        zuc1HScope.apply {
            if (DSGH == 0f) {
                if (args.inputData.TIPRE == 4 && args.inputData.NW > 1)
                    Z2R = Z2//Не понимаю надобность ZR
                if ((BET + args.inputData.BETMI) > 0f)
                    AKA = 430f
                else
                    AKA = 495f
                KHB = 1f//попробую сейчас как в схеме, в фортране 1.2
                KHV = 1f
            }
            if (args.inputData.TIPRE == 4 && args.IST == 0) {
                AW = args.AW//Для соосных
                PSIBA = (1.1f*args.T2*KHB!!*KHV!!/(args.u*args.dopnScope.SGHD!!).pow(2))*
                        (AKA!!*(args.u*args.SIGN)/AW).pow(3)
                if (PSIBA!! < 0.2f)
                    PSIBA = 0.2f
                if (DSGF > 0f)
                    PSIBA = PSIBA!!*(1f + DSGF/args.dopnScope.wheelsSGFD[1])
                //Идём в BWChoose
                BWChoose(args, zuc1HScope)
            }
            else {
                if (AWFS) {
                    T2 = ((AW/(1.1f*AKA!!*(args.u + args.SIGN))).pow(3))*
                            (args.u*args.dopnScope.SGHD!!).pow(2)*args.option.PSB/(KHB!!*KHV!!)
                    //Идём в BWChoose
                    BWChoose(args, zuc1HScope)
                }
                else {
                    AW = 1.05f*AKA!!*(args.u + args.SIGN)*(args.T2*KHB!!*KHV!!/(args.u *
                            args.dopnScope.SGHD!!).pow(2)*args.option.PSB).pow(1/3f)
                    //Идём в AWChoose
                    AWChoose(args, zuc1HScope)
                }
            }
        }
    }
}

/**
 * [DSGH] - разность между действительными и допускаемыми контактными напряжениями
 * [DSGF] - разность между действительными и допускаемыми изгибными напряжениями
 * [Z1], [Z2] - числа зубьев шестерни (1) и колеса (2)
 * [ZSUM] - суммарное число зубьев
 * [Z1R] - предварительное значение зубьев шестерни
 * [ZMI] - минимальное значение зубьев шестерни
 * [X1], [X2] - числа зубьев шестерни и колеса
 * [X1FS], [X2FS] - показатель, заданы они на данный момент или нет
 * [XSUM] - суммарный коэффициент смещения
 * [AW] - межосевое расстояние передачи
 * [AWFS] - показатель, задано ли межосевое расстояние на данный момент
 * [BW1], [BW2] - ширина зубчатого венца шестерни и колеса
 * [BET] - угол наклона зуба
 * [BETFS] - показатель, задан ли уже угол наклона зуба
 * [ALFT] - торцовый угол профиля зуба
 * [ALFTW] - угол зацепления передачи
 * [INVAT], [INVW] - инволюты углов профиля зуба
 * [KHV] - коэффициент, учитывающий динамическую нагрузку, возникающую в зацеплении
 * [MR] - предварительное значение модуля (не приведённое к стандартному ряду модулей)
 * [AKA] - коэффициент, используемый при проектном расчёте зубчатой передачи
 * [KHB], [KFB] - коэффициенты, учитывающие распределение нагрузки по ширине венца
 * [PSIBA] - коэффициент ширины зубчатого венца в долях межосевого расстояния
 * [UCalculated] - расчитанное передаточное значение ступени
 */
data class ZUC1HScope(
    var DSGH: Float = 0f,
    var DSGF: Float = 0f,
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
    var KFB: Float = 1f,
    var PSIBA: Float? = null,
    var T2: Float? = null,//здесь выдаётся ненастоящий Т2
    var UCalculated: Float = 0f
)