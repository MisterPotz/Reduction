package com.reducetechnologies.calculations

import kotlin.math.pow

object ZUCMethods {
    data class Arguments(
        val SIGN: Int,
        val IST: Int,
        val N2: Float,
        val T2: Float,
        val AWTipre4: Float? = null,//Это для соосных, сюда должно передаваться значение AW предыдущей ступени
        val uStup: Float,//чтобы передать задание этой величины в след функцию
        val inputData: InputData,
        var dopnScope: DOPNScope,
        var zuc1hScope: ZUC1HScope,
        var zucepScope: ZUCEPScope,
        var zuc2hScope: ZUC2HScope,
        var zucfScope: ZUCFScope,
        val option: ReducerOptionTemplate,
        val edScope: EDScope
    )

    private val dopnMethods: DOPN_Methods =
        DOPN_Methods
    private val zuc1hMethods: ZUC1HMethods =
        ZUC1HMethods
    private val zucepMethods: ZUCEPMethods =
        ZUCEPMethods
    private val zuc2hMethods: ZUC2HMethods =
        ZUC2HMethods
    private val zucfMethods: ZUCFMethods =
        ZUCFMethods

    private var SCHET: Int = 0

    fun enterZUC(args: Arguments) {
        args.apply {
            //zuc1hScope.Z2 = 0 зачем?
            //SCHET = 0 обнуление поставить в uCalc
            //какая-то логика задания Z0, X0, DA0, но я думаю она должна быть реализована ещё раньше
            DOPN_Methods.dopn(
                DOPN_Methods.Arguments(
                    N2 = N2,//Это можно выкинуть и взять из input
                    u = uStup,
                    inputData = inputData,
                    option = option
                ), dopnScope
            )
            ZUC1HMethods.enterZUC1H(
                ZUC1HMethods.Arguments(
                    SIGN = SIGN,
                    u = uStup,
                    T2 = T2,//Это можно выкинуть и взять из input
                    AW = AWTipre4!!,
                    dopnScope = dopnScope,
                    option = option,
                    inputData = inputData,
                    IST = IST
                ), zuc1hScope
            )
            ZUCEPMethods.enterZUCEP(
                ZUCEPMethods.Arguments(
                    SIGN = SIGN,
                    N2 = N2,//Это можно выкинуть и взять из input
                    dopnScope = dopnScope,
                    zuc1HScope = zuc1hScope,
                    inputData = inputData
                ), zucepScope
            )
            //Логика по задания KHB and KFB and ST, но она должна быть у меня по другому реализована,
            //а вообще можно (на 99 процентов она мне не понадобится) и без неё
            ZUC2HMethods.enterZUC2H(
                ZUC2HMethods.Arguments(
                    SIGN = SIGN,
                    CONSOL = inputData.CONSOL[IST],
                    N2 = N2,//Это можно выкинуть и взять из input
                    T2 = if (zuc1hScope.T2 != null) zuc1hScope.T2!! else T2,//СЮДА СКОРЕЕ ВСЕГО НУЖНО КИДАТЬ T2 ИЗ zuc1hscope!!
                    edScope = edScope,
                    option = option,
                    zuc1hScope = zuc1hScope,
                    zucepScope = zucepScope,
                    dopnScope = dopnScope,
                    inputData = inputData
                ), zuc2hScope
            )
            ZUCFMethods.enterZUCF(
                ZUCFMethods.Arguments(
                    SIGN = SIGN,
                    CONSOL = inputData.CONSOL[IST],
                    inputData = inputData,
                    edScope = edScope,
                    dopnScope = dopnScope,
                    option = option,
                    zuc1hScope = zuc1hScope,
                    zuc2hScope = zuc2hScope,
                    zucepScope = zucepScope
                ), zucfScope
            )
            zuc1hScope.DSGH = zuc2hScope.SGH!! - dopnScope.SGHD!!
            var DSGF1: Float = zucfScope.SGF[0] - dopnScope.wheelsSGFD[0]
            var DSGF2: Float = zucfScope.SGF[1] - dopnScope.wheelsSGFD[1]
            SCHET++
            if (SCHET > 3) {
                //Уход в uCalc
                uCalc(args)
                return
            }
            if (DSGF1 <= 0f && DSGF2 <= 0f) {
                //Уход в beforeUCalc
                beforeUCalc(args)
                return
            }
            if (DSGF2 >= DSGF1) {
                //Уход в mReCalc
                mReCalc(args, DSGF2)
                return
            }
            if (zuc1hScope.X1 > 0.3f)
                zuc1hScope.MR = dopnScope.M*(zucfScope.SGF[0]/dopnScope.wheelsSGFD[0]).pow(0.35f)
            else
                zuc1hScope.MR = 1.04f*dopnScope.M*(zucfScope.SGF[0]/dopnScope.wheelsSGFD[0]).pow(1/3f)
            zuc1hScope.DSGF = DSGF1
            dopnScope.M = zuc1hScope.MR!!
            //Уход в начало
            enterZUC(args)
            return
        }
    }

    private fun mReCalc(args: Arguments, DSGF2: Float) {
        args.apply {
            if (zuc1hScope.X1 > 0.3f)
                zuc1hScope.MR = dopnScope.M*(zucfScope.SGF[1]/dopnScope.wheelsSGFD[1]).pow(0.35f)
            else
                zuc1hScope.MR = 1.04f*dopnScope.M*(zucfScope.SGF[1]/dopnScope.wheelsSGFD[1]).pow(1/3f)
            zuc1hScope.DSGF = DSGF2
            dopnScope.M = zuc1hScope.MR!!
            //Уход в начало
            enterZUC(args)
            return
        }
    }

    private fun beforeUCalc(args: Arguments) {
        args.apply {
            if (SCHET > 2 && zuc1hScope.DSGH <= 0f && zuc1hScope.DSGF <= 0f) {
                //Уход в uCalc
                uCalc(args)
                return
            }
            if (zuc1hScope.DSGH > 0 && SCHET > 1)
                zuc1hScope.AKA = zuc1hScope.AKA!!*(zuc2hScope.SGH!!/dopnScope.SGHD!!).pow(0.7f)
            dopnScope.M = zuc1hScope.MR!!//Я НЕ УВЕРЕН, ЧТО ЗДЕСЬ НУЖНА ЭТА СТРОЧКА
            //Уход в начало
            enterZUC(args)
            return
        }
    }

    private fun uCalc(args: Arguments) {
        args.zuc1hScope.apply {
            UCalculated = Z2!!.toFloat()/Z1!!.toFloat()
            SCHET = 0//Обнулили для других расчётов
            return
        }
    }
}