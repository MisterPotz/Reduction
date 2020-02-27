package com.reducetechnologies.calculations

import kotlin.math.*

object ZUCFMethods {
    data class Arguments(
        val SIGN: Int,
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
            zucfScope.WFV = zucfScope.DELF!!*zuc2hScope.G0!!*dopnScope.V* sqrt(
                zuc1hScope.AW /
                        zuc1hScope.UCalculated
            )
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
            oneWheelCalc(
                args = args,
                zucfScope = zucfScope,
                Z = zuc1hScope.Z1!!,
                X = zuc1hScope.X1,
                BW = zuc1hScope.BW1!!,
                wheelNumber = 0
            )
            //Колесо
            //Там очень странная логика относительно Z0, пока что буду использовать просто уже
            //рассчитанное в zucep значение
            if (SIGN < 0)
                oneWheelCalc(
                    args = args,
                    zucfScope = zucfScope,
                    Z = -1 * zuc1hScope.Z2!!,
                    X = zuc1hScope.X2,
                    BW = zuc1hScope.BW2!!,
                    wheelNumber = 1
                )
            else
                oneWheelCalc(
                    args = args,
                    zucfScope = zucfScope,
                    Z = zuc1hScope.Z2!!,
                    X = zuc1hScope.X2,
                    BW = zuc1hScope.BW2!!,
                    wheelNumber = 1
                )
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
            var ZV: Float = abs(Z / (cos(zuc1hScope.BET)).pow(3))
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
            zucfScope.SGF[wheelNumber] = YEP*YBET*YF*WFT/args.dopnScope.M
            zucfScope.SGFM[wheelNumber] = zucfScope.SGF[wheelNumber]*edScope.TTED!!
        }
    }

}

data class ZUCFScope(
    var SGF: Array<Float> = Array(2){-1f},
    var SGFM: Array<Float> = Array(2){-1f},
    var DELF: Float? = null,
    var WFV: Float? = null,
    var KFV: Float? = null
)