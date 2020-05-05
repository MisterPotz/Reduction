package com.reducetechnologies.calculations

import kotlin.math.*

class ZUCEPMethodsClass() {
    data class Arguments(
        val SIGN: Int,
        val N2: Float,
        val dopnScope: DOPNScope,
        val zuc1HScope: ZUC1HScope,
        val inputData: InputData,
        var HK1: Float = inputData.HG,//подумать, возможно не лучший вариант хранить их здесь
        var HK2: Float = inputData.HG,//подумать, возможно не лучший вариант хранить их здесь
        val EPMI: Float = 1.2f//принимаем по умолчанию минимальное значение коэффициента перекрытия
        /*var Z0: Int = 0,//задаётся потом
        var X0: Float = 0f,//принимается стандартно, но может быть другим
        val D0: Float = 1f,//ЭТО ЗАГЛУШКА!!! Но почему-то эта переменная не применяется в методе
        var DA0: Float = 1f//Здесь вроде 0, если не задано*/
    )

    fun enterZUCEP(args: Arguments, zucepScope: ZUCEPScope) {
        args.zuc1HScope.apply {
            //Они здесь сначала пересчитывают AW, но я не понимаю, зачем это нужно, возможно потом
            //пригодится, не забудь этот момент, пока ничего не буду писать
            var A: Float = AW* cos(ALFTW!!) / cos(ALFT!!)//чтобы потом найти DELY
            zucepScope.D[0] = args.dopnScope.M*Z1!!/ cos(BET)
            zucepScope.D[1] = args.dopnScope.M*Z2!!/ cos(BET)
            zucepScope.DB[0] = zucepScope.D[0]* cos(ALFT!!)
            zucepScope.DB[1] = zucepScope.D[1]* cos(ALFT!!)
            if (!args.inputData.PAR)
                zucepScope.DELY = XSUM - (AW - A)/args.dopnScope.M
            zucepScope.DA[0] = zucepScope.D[0] + 2*(args.inputData.HA + X1 - zucepScope.DELY)*args.dopnScope.M
            zucepScope.DA[1] = zucepScope.D[1] + 2*(args.inputData.HA + X2 - zucepScope.DELY)*args.dopnScope.M
            zucepScope.DF[0] = zucepScope.D[0] - 2*(args.inputData.HA + args.inputData.C - X1)*args.dopnScope.M
            zucepScope.DF[1] = zucepScope.D[1] - 2*(args.inputData.HA + args.inputData.C - X2)*args.dopnScope.M
            if (args.SIGN < 0) {
                //Уход в diamInternalTeeth
                diamInternalTeeth(args, zucepScope)
                return
            }
            else {
                //Уход в hollow
                hollow(args, zucepScope)
                return
            }
        }
    }

    private fun diamInternalTeeth(args: Arguments, zucepScope: ZUCEPScope) {
        args.zuc1HScope.apply {
            var K2: Float = 0.25f - 0.125f*X2
            if (X2 >= 2f)
                K2 = 0f
            zucepScope.DA[1] = zucepScope.D[1] - 2*(args.inputData.HA - X2 + zucepScope.DELY - K2)*args.dopnScope.M
            if (zucepScope.Z0 == 0) {
                zucepScope.Z0 = (0.3f*Z2!!).roundToInt()
                zucepScope.DA0 = args.dopnScope.M*(zucepScope.Z0 + 2*(args.inputData.HA + args.inputData.C + zucepScope.X0))
            }
            INVW = INVAT!! + 2*(X2 - zucepScope.X0)* tan(args.inputData.ALF) /(Z2!! - zucepScope.Z0)
            var ALFR: Float = 1.316111f*(INVW!!).pow(0.290865f) - 0.03806f
            var INVAR: Float = tan(ALFR) - ALFR
            while (INVW!! < INVAR) {
                ALFR -= 0.000002f
                INVAR = tan(ALFR) - ALFR
            }
            zucepScope.ALFW02 = ALFR
            zucepScope.AW02 = args.dopnScope.M*(Z2!! - zucepScope.Z0)* cos(ALFT!!) /(2* cos(
                BET
            ) * cos(zucepScope.ALFW02!!))
            zucepScope.DF[1] = 2*zucepScope.AW02!! + zucepScope.DA0
            var Y: Float = args.dopnScope.M*zucepScope.Z0* cos(ALFT!!) /zucepScope.DA0
            zucepScope.ALFA0 = atan(sqrt((1f - Y.pow(2)) / Y))
            //Уход в hollow
            hollow(args, zucepScope)
            return
        }
    }

    private fun hollow(args: Arguments, zucepScope: ZUCEPScope) {
        zucepScope.apply {
            DK[0] = DA[0] - 2*args.HK1*args.dopnScope.M
            DK[1] = DA[1] - args.SIGN*2*args.HK2*args.dopnScope.M
            ALFK[0] = acos(DB[0] / DK[0])
            ALFK[1] = acos(DB[1] / DK[1])
            ROP[0] = args.SIGN*(args.zuc1HScope.AW* sin(args.zuc1HScope.ALFTW!!) -
                    DB[1]* tan(ALFK[1]) /2)
            ROP[1] = (args.zuc1HScope.AW* sin(args.zuc1HScope.ALFTW!!) -
                    args.SIGN*DB[0]* tan(ALFK[0]) /2)
            ROL[0] = D[0]* sin(args.zuc1HScope.ALFT!!) /2 - (args.inputData.HL -
                    args.inputData.HA - args.zuc1HScope.X1)*args.dopnScope.M/ sin(args.zuc1HScope.ALFT!!)
            if (args.SIGN < 0)
                ROL[1] = AW02!!* sin(ALFW02!!) + zucepScope.DA0* sin(args.zuc1HScope.ALFT!!) /2
            else
                ROL[1] = D[1]* sin(args.zuc1HScope.ALFT!!) /2 - args.SIGN*(args.inputData.HL
                        - args.inputData.HA - args.SIGN*args.zuc1HScope.X2)*
                        args.dopnScope.M/ sin(args.zuc1HScope.ALFT!!)
            CF[0] = args.SIGN*(args.zuc1HScope.AW - DK[1]/2) - DF[0]/2
            CF[1] = args.SIGN*(args.zuc1HScope.AW - DF[1]/2) - DK[0]/2
            if (ROL[0] < ROP[0] && CF[0] > 0.15f*args.dopnScope.M) {
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
            if (args.SIGN*(ROL[1] - ROP[1]) <= 0f && CF[1] > 0.15f*args.dopnScope.M) {
                //Если при пересчёте оказалось, что в этот раз интерференции больше нет, нужно
                //обнулять флаг
                interference = false
                //Уход в calculationOfQualityIndicators
                calculationOfQualityIndicators(args, zucepScope)
                return
            }
            if (args.inputData.PAR && args.SIGN < 0) {
                //Уход в fullIndicatorsCalculation
                fullIndicatorsCalculation(args, zucepScope)
                return
            }
            args.HK1 += 0.05f*args.dopnScope.M
            if (args.HK1 > 0.2f*args.dopnScope.M){
                //Уход в fullIndicatorsCalculation
                fullIndicatorsCalculation(args, zucepScope)
                return
            }
            else {
                //Если при пересчёте оказалось, что в этот раз интерференции больше нет, нужно
                //обнулять флаг
                interference = false
                //Уход в hollow
                hollow(args, zucepScope)
                return
            }
        }
    }

    private fun case2(args: Arguments, zucepScope: ZUCEPScope) {
        args.apply {
            HK2 += 0.05f*args.dopnScope.M
            if (HK2 > 0.2f*args.dopnScope.M) {
                //Уход в fullIndicatorsCalculation
                fullIndicatorsCalculation(args, zucepScope)
                return
            }
            else {
                //Если при пересчёте оказалось, что в этот раз интерференции больше нет, нужно
                //обнулять флаг
                zucepScope.interference = false
                //Уход в hollow
                hollow(args, zucepScope)
                return
            }
        }
    }

    private fun fullIndicatorsCalculation(args: Arguments, zucepScope: ZUCEPScope) {
        //Сразу по входу помечаем с помощью флага, что была интерференция
        zucepScope.interference = true
        /*println("Произошла интерференция во впадинах или слишком малый радиальный зазор\n" +
                "(либо отбросить вариант, либо дать рекомендации по применению неизношенного\n" +
                "долбяка) \n")*/
        if (args.HK1 > 0 || args.inputData.PAR) {
            //Уход в calculationOfQualityIndicators
            calculationOfQualityIndicators(args, zucepScope)
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
            EA[0] = (args.zuc1HScope.Z1!!/(2* PI.toFloat()))*(tan(ALFK[0]) - tan(
                args.zuc1HScope.ALFTW!!
            ))
            EA[1] = (args.SIGN*args.zuc1HScope.Z2!!/(2* PI.toFloat()))*
                    (tan(ALFK[1]) - tan(args.zuc1HScope.ALFTW!!))
            EPALF = EA[0] + EA[1]
            EAM = max(EA[0], EA[1])
            isEpalfLess = EPALF!! < args.EPMI // Для того, чтобы понять, что там происходит с перекрытием
            DA[0] = DK[0]
            DA[1] = DK[1]
            var V2: Float = PI.toFloat()*D[1]*args.N2* cos(args.zuc1HScope.ALFT!!) /(6000f*
                    cos(args.zuc1HScope.ALFTW!!))
            var VS: Float = 2*V2* sin(args.zuc1HScope.ALFTW!!)
            var FTRZ: Float = 1.25f*(0.102f - 0.02f*VS.pow(1/3f))
            var PSIZR: Float = 2f*PI.toFloat()*FTRZ*(1f/args.zuc1HScope.Z1!! +
                    args.SIGN*1f/args.zuc1HScope.Z2!!)
            if (args.zuc1HScope.BET > 0f) {
                PSIZC = PSIZR*(EA[0].pow(2) + EA[1].pow(2))/(2*EPALF!!* cos(args.zuc1HScope.BET))
            }
            else
                PSIZC = PSIZR*(1f - EPALF!! + 0.5f*EPALF!!.pow(2))*EAM!!/EPALF!!
            return
        }
    }
}

data class ZUCEPScope(
    var D: Array<Float> = Array(2){-1f},
    var DB: Array<Float> = Array(2){-1f},//диаметры основных окружностей
    var DA: Array<Float> = Array(2){-1f},//диаметры окружностей вершин зубьев колёс
    var DF: Array<Float> = Array(2){-1f},//диаметры окружностей впадин колёс
    var CF: Array<Float> = Array(2){-1f},//действительный зазор с зацеплении
    var DK: Array<Float> = Array(2){-1f},//диаметры окружностей притупленных кромок колёс
    var ALFK: Array<Float> = Array(2){-1f},//угол профиля зуба в точке притупленных кромок
    var ROP: Array<Float> = Array(2){-1f},
    var ROL: Array<Float> = Array(2){-1f},
    var PSIZC: Float? = null,//коэффициент потерь в зацеплении
    var DELY: Float = 0f,
    var EA: Array<Float> = Array(2){-1f},
    var EAM: Float? = null,
    var EPALF: Float? = null,
    var ALFW02: Float? = null,//угол зацепления долбяка с колесом
    var AW02: Float? = null,//межосевое расстояние в станочном зацеплении долбяка с колесом
    var ALFA0: Float? = null,//угол профиля зуба долбяка на окружности вершин
    var Z0: Int = 0,//задаётся потом
    var X0: Float = 0f,//принимается стандартно, но может быть другим
    val D0: Float = 1f,//ЭТО ЗАГЛУШКА!!! Но почему-то эта переменная не применяется в методе
    var DA0: Float = 1f,//Здесь вроде 0, если не задано
    var interference: Boolean = false,//True if the interference has been
    var isEpalfLess: Boolean = false//True if EPALF < EPMI, but it matters only in SPUR wheels
)
