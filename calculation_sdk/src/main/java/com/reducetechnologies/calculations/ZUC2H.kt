package com.reducetechnologies.calculations

import kotlin.math.*

object ZUC2HMethods {
    data class Arguments(
        val SIGN: Int,
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
            DW[0] = 2*args.zuc1hScope.AW/(args.zuc1hScope.UCalculated + args.SIGN)
            DW[1] = DW[0]*args.zuc1hScope.UCalculated
            args.dopnScope.V = PI.toFloat()*DW[1]*args.N2/60000
            if (args.ST > 0 && args.zuc1hScope.BET > 0f) {
                KHALF = (0.0026f*args.ST - 0.013f)*args.dopnScope.V + 0.027f*args.ST + 0.84f
                //Уход в calculatePSIBD
                calculatePSIBD(args, zuc2hScope)
                return
            }
            else if (args.zuc1hScope.BET > 0f) {
                args.ST = (10.1f - 0.12f*args.dopnScope.V).toInt()
                KHALF = (0.0026f*args.ST - 0.013f)*args.dopnScope.V + 0.027f*args.ST + 0.84f
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
                args.ST = (10.1f - 0.2f*args.dopnScope.V).toInt()
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
            G0 = 10f*args.ST + args.dopnScope.M*((0.16f*args.ST - 0.78f)*(args.ST).toFloat().pow(0.4f) +
                    0.58f) - 22f
            WHV = args.DELH!!*G0!!*args.dopnScope.V* sqrt(args.zuc1hScope.AW / args.zuc1hScope.UCalculated)
            WV = args.dopnScope.M*(4f*args.ST - 15f) - 0.1f*args.ST.toFloat().pow(4)
            if (WHV!! > WV!!)
                WHV = WV!!
            FT = 2000f*args.T2/DW[1]
            FR = FT!!* tan(args.zuc1hScope.ALFTW!!)
            FA = FT!!* tan(args.zuc1hScope.BET)
            args.zuc1hScope.KHV = 1f + WHV!!*args.zuc1hScope.BW!!/FT!!
            EPBET = args.zuc1hScope.BW!!* sin(args.zuc1hScope.BET) /(PI.toFloat()*args.dopnScope.M)
            if (EPBET!! >= 0.9f)
                ZEP = sqrt(1f / args.zucepScope.EPALF!!)
            else {
                ZEP = sqrt((4f - args.zucepScope.EPALF!!) / 3f)
            }
            var ZM: Float = sqrt(
                2f * args.E1 * args.E2 / ((args.E1 + args.E2) * PI.toFloat() * (1f - args.PUAS.pow(
                    2
                )))
            )
            var sinBTB: Float = sin(args.zuc1hScope.BET) * cos(args.inputData.ALF)
            var BETB: Float = atan(sinBTB / sqrt(1f - sinBTB.pow(2)))
            var ZH: Float =
                sqrt(2f * cos(BETB) / sin(2f * args.zuc1hScope.ALFTW!!))
            WHT = FT!!*args.zuc1hScope.KHB!!*args.zuc1hScope.KHV!!*KHALF!!/args.zuc1hScope.BW!!
            SGH = ZH*ZM*ZEP!!* sqrt(
                WHT!! * (args.zuc1hScope.UCalculated + args.SIGN) /
                        (DW[0] * args.zuc1hScope.UCalculated)
            )
            if (args.edScope.TTED != null) {
                SGHM = SGH!! * sqrt(args.edScope.TTED!!)//нужно брать из
            }//Потом это нужно получше обработать, потому что электродвигатель будет не всегда считаться
            args.zuc1hScope.AKA = (500f*(ZH*ZM*ZEP!!).pow(2)*KHALF!!).pow(1/3f)
            ST = args.ST//просто чтобы сохранить в scope
        }
    }
}

data class ZUC2HScope(
    var DW: Array<Float> = Array(2){-1f},
    //var V: Float? = null,переселил в dopnScope
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