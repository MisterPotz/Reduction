package com.reducetechnologies.calculations

import kotlin.math.*

class ZUC2HMethodsClass() {
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
        val CONSOL: Int,//просто инициализировать значениями из массива из inputData
        val E1: Long = 215000,//взял значение из книжки
        val E2: Long = 215000,//взял значение из книжки
        val PUAS: Float = 0.3f//пока заглушка, потом нужно найти, какие данные вбивать
    )

    fun enterZUC2H(args: Arguments, zuc2hScope: ZUC2HScope) {
        zuc2hScope.apply {
            DW[0] = 2*args.zuc1hScope.AW/(args.zuc1hScope.UCalculated + args.SIGN)
            DW[1] = DW[0]*args.zuc1hScope.UCalculated
            args.dopnScope.V = PI.toFloat()*DW[1]*args.N2/60000
            if (ST > 0 && args.zuc1hScope.BET > 0f) {
                ST = (10.1f - 0.12f*args.dopnScope.V).toInt()//Это я поставил, нужно протестить, потому что в учебнике нет
                KHALF = (0.0026f*ST - 0.013f)*args.dopnScope.V + 0.027f*ST + 0.84f
                //Уход в calculatePSIBD
                calculatePSIBD(args, zuc2hScope)
                return
            }
            else if (args.zuc1hScope.BET > 0f) {
                ST = (10.1f - 0.12f*args.dopnScope.V).toInt()
                //Этого нет в фортране, но нужно ограничить ST 9ю в случае небольшой скорости
                if (ST > 9) ST = 9
                KHALF = (0.0026f*ST - 0.013f)*args.dopnScope.V + 0.027f*ST + 0.84f
                //Уход в calculatePSIBD
                calculatePSIBD(args, zuc2hScope)
                return
            }
            else if (ST > 0) {
                KHALF = 1f
                //Уход в calculatePSIBD
                calculatePSIBD(args, zuc2hScope)
                return
            }
            else {
                ST = (10.1f - 0.2f*args.dopnScope.V).toInt()//Может выдать 10, если V очень маленькая, чего быть не может
                //Поэтому возможно стоит поставить граничное условие на 9
                //Этого нет в фортране, но нужно ограничить ST 9ю в случае небольшой скорости
                if (ST > 9) ST = 9
                KHALF = 1f
                //Уход в calculatePSIBD
                calculatePSIBD(args, zuc2hScope)
                return
            }
        }
    }

    private fun calculatePSIBD(args: Arguments, zuc2hScope: ZUC2HScope) {
        zuc2hScope.apply {
            PSIBD = args.zuc1hScope.BW1!!/DW[0]//Здесь вроде бы должно быть BW1, а не BW
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
                    SHEM = 4
                else if (args.option.HRC[0] > 35f)
                    SHEM = 3
                if (args.CONSOL == 1)
                    SHEM = 2
                args.zuc1hScope.KHB = 1f + 0.51f*PSIDR/SHEM
                //Уход в HRC2Check
                HRC2Check(args, zuc2hScope)
                return
            }
        }
    }

    private fun HRC1Check(args: Arguments, zuc2hScope: ZUC2HScope, PSIDR: Float) {
        args.apply {
            if (option.HRC[0] <= 45f)
                zuc2hScope.SHEM = 4
            else if (args.option.HRC[0] > 45f)
                zuc2hScope.SHEM = 3
            if (CONSOL == 1)
                zuc2hScope.SHEM = 2
            zuc1hScope.KHB = 1f + 1.1f*PSIDR/zuc2hScope.SHEM
            //Уход в HRC2Check
            HRC2Check(args, zuc2hScope)
            return
        }
    }

    private fun HRC2Check(args: Arguments, zuc2hScope: ZUC2HScope) {
        args.apply {
            if (option.HRC[1] <= 35f) {
                if (zuc1hScope.BET <= 0f)
                    zuc2hScope.DELH = 0.006f
                if (abs(inputData.HG) > 0)
                    zuc2hScope.DELH = 0.004f
                if (zuc1hScope.BET > 0f)
                    zuc2hScope.DELH = 0.002f
                //Уход в finalCalc
                finalCalc(args, zuc2hScope)
                return
            }
            else {
                if (zuc1hScope.BET <= 0f)
                    zuc2hScope.DELH = 0.014f
                if (abs(inputData.HG) > 0)
                    zuc2hScope.DELH = 0.01f
                if (zuc1hScope.BET > 0f)
                    zuc2hScope.DELH = 0.004f
                //Уход в finalCalc
                finalCalc(args, zuc2hScope)
                return
            }
        }
    }

    private fun finalCalc(args: Arguments, zuc2hScope: ZUC2HScope) {
        zuc2hScope.apply {
            G0 = 10f*ST + args.dopnScope.M*((0.16f*ST - 0.78f)*(ST).toFloat().pow(0.4f) +
                    0.58f) - 22f
            WHV = DELH!!*G0!!*args.dopnScope.V* sqrt(args.zuc1hScope.AW / args.zuc1hScope.UCalculated)
            WV = args.dopnScope.M*(4f*ST - 15f) + 0.1f*ST.toFloat().pow(0.4f)
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
            val ZM: Float = sqrt(
                2f * args.E1 * args.E2 / ((args.E1 + args.E2) * PI.toFloat() * (1f - args.PUAS.pow(
                    2
                )))
            )
            val sinBTB: Float = sin(args.zuc1hScope.BET) * cos(args.inputData.ALF)
            val BETB: Float = atan(sinBTB / sqrt(1f - sinBTB.pow(2)))
            val ZH: Float =
                sqrt(2f * cos(BETB) / sin(2f * args.zuc1hScope.ALFTW!!))
            WHT = FT!!*args.zuc1hScope.KHB!!*args.zuc1hScope.KHV!!*KHALF!!/args.zuc1hScope.BW!!
            SGH = ZH*ZM*ZEP!!* sqrt(
                WHT!! * (args.zuc1hScope.UCalculated + args.SIGN) /
                        (DW[0] * args.zuc1hScope.UCalculated)
            )
            if (args.edScope.TTED != null) {
                SGHM = SGH!! * sqrt(args.edScope.TTED!!)//нужно брать из
            }//Потом это нужно получше обработать, потому что электродвигатель будет не всегда считаться
            //Если не подбираем электродвигатель, то просто берем для TTED значение 2
            else {
                SGHM = SGH!! * sqrt(2f)
            }
            args.zuc1hScope.AKA = (500f*(ZH*ZM*ZEP!!).pow(2)*KHALF!!).pow(1/3f)
        }
    }
}

data class ZUC2HScope(
    var DW: Array<Float> = Array(2){-1f},
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
    var ST: Int = 0,
    var SHEM: Int = 0,//инициализируется в программе
    var DELH: Float? = null//инициализируется в программе
)