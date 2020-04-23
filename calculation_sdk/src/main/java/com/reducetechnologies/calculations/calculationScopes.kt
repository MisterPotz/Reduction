package com.reducetechnologies.calculations

import kotlin.math.sqrt



object ZCREDMethods {
    fun enterZCRED(input: InputData, zcredScope: ZCREDScope): List<CreationData> {
        val options: List<ReducerOptionTemplate> =
            allReducersOptions.tryToCalculateOptions(inputData = input)
        var creationDataList: MutableList<CreationData> = arrayListOf()
        if (input.ISTCol == 1)
            return ZC1RED(
                input = input,
                zcredScope = zcredScope,
                options = options,
                creationDataList = creationDataList
            )
        else
            return ZC2RED(
                input = input,
                zcredScope = zcredScope,
                options = options,
                creationDataList = creationDataList
            )
        /*options.forEach {option ->
            option.apply {
                //Тихоходная ступень
                zc2redScope.TVL3 = 9550f*option.EDScope!!.PED!!*input.U0*u*input.KPD/option.EDScope.NED!!
                var NV3: Float = option.EDScope.NED!!/(u*input.U0)
                var firstStep = OneGearWheelStep()//Здесь сохраняем скоупы тихоходной ступени
                ZUCMethods.enterZUC(ZUCMethods.Arguments(SIGN = input.SIGN[1],
                    IST = 1,
                    N2 = NV3,
                    T2 = zc2redScope.TVL3!!*input.OMEG/input.NW,
                    uStup = uT,
                    inputData = input,
                    dopnScope = firstStep.dopnScope,
                    zuc1hScope = firstStep.zuc1hScope,
                    zucepScope = firstStep.zucepScope,
                    zuc2hScope = firstStep.zuc2hScope,
                    zucfScope = firstStep.zucfScope,
                    option = option,
                    edScope = option.EDScope))
                //zc2redScope.TVL1 = 0f вроде она у нас бесполезна, мы не печатаем внутри ничего
                zc2redScope.TVL2 = zc2redScope.TVL3!!*input.OMEG/(option.uT* sqrt(input.KPD)*input.NW)
                if (input.TIPRE == 4) {
                    input.apply {
                        NP = 1
                        if (NZAC[1] == 1) {
                            BETMI = 0.142f
                            BETMA = 0.28f
                        }
                        else {
                            NZAC[0] = NZAC[1].also { NZAC[1] = NZAC[0] }//ого, супер своп
                            BETMI = 0.142f
                            BETMA = 0.28f
                        }
                    }
                }
                var secondStep = OneGearWheelStep()//Здесь сохраняем скоупы бысроходной ступени
                ZUCMethods.enterZUC(ZUCMethods.Arguments(SIGN = input.SIGN[0],
                    IST = 0,
                    N2 = NV3*uT,
                    T2 = zc2redScope.TVL2!!,
                    uStup = uB,
                    inputData = input,
                    dopnScope = secondStep.dopnScope,
                    zuc1hScope = secondStep.zuc1hScope,
                    zucepScope = secondStep.zucepScope,
                    zuc2hScope = secondStep.zuc2hScope,
                    zucfScope = secondStep.zucfScope,
                    option = option,
                    edScope = option.EDScope))
                zc2redScope.TVL1 = zc2redScope.TVL2!!*input.NW/(uB* sqrt(input.KPD)*input.OMEG)//здесь возможно формула перепутана, NW and OMEG местами
                val creationData = CreationData(edScope = option.EDScope,
                    option = option,
                    gearWheelStepsArray = arrayOf(firstStep, secondStep))
                creationDataList.add(creationData)
            }
        }
        return creationDataList*/
    }

    private fun ZC2RED(
        input: InputData,
        zcredScope: ZCREDScope,
        options: List<ReducerOptionTemplate>,
        creationDataList: MutableList<CreationData>
    ): List<CreationData> {
        options.forEach { option ->
            option.apply {
                //Тихоходная ступень
                zcredScope.TVL3 =
                    9550f * option.EDScope!!.PED!! * input.U0 * u * input.KPD / option.EDScope.NED!!
                var NV3: Float = option.EDScope.NED!! / (u * input.U0)
                var firstStep = OneGearWheelStep()//Здесь сохраняем скоупы тихоходной ступени
                ZUCMethods.enterZUC(
                    ZUCMethods.Arguments(
                        SIGN = input.SIGN[1],
                        IST = 1,
                        N2 = NV3,
                        T2 = zcredScope.TVL3!! * input.OMEG / input.NW,
                        uStup = uT,
                        inputData = input,
                        dopnScope = firstStep.dopnScope,
                        zuc1hScope = firstStep.zuc1hScope,
                        zucepScope = firstStep.zucepScope,
                        zuc2hScope = firstStep.zuc2hScope,
                        zucfScope = firstStep.zucfScope,
                        option = option,
                        edScope = option.EDScope
                    )
                )
                //zc2redScope.TVL1 = 0f вроде она у нас бесполезна, мы не печатаем внутри ничего
                zcredScope.TVL2 =
                    zcredScope.TVL3!! * input.OMEG / (option.uT * sqrt(input.KPD) * input.NW)
                if (input.TIPRE == 4) {
                    input.apply {
                        NP = 1
                        if (NZAC[1] == 1) {
                            BETMI = 0.142f
                            BETMA = 0.28f
                        } else {
                            NZAC[0] = NZAC[1].also { NZAC[1] = NZAC[0] }//ого, супер своп
                            BETMI = 0.142f
                            BETMA = 0.28f
                        }
                    }
                }
                var secondStep = OneGearWheelStep()//Здесь сохраняем скоупы бысроходной ступени
                ZUCMethods.enterZUC(
                    ZUCMethods.Arguments(
                        SIGN = input.SIGN[0],
                        IST = 0,
                        N2 = NV3 * uT,
                        T2 = zcredScope.TVL2!!,
                        uStup = uB,
                        inputData = input,
                        dopnScope = secondStep.dopnScope,
                        zuc1hScope = secondStep.zuc1hScope,
                        zucepScope = secondStep.zucepScope,
                        zuc2hScope = secondStep.zuc2hScope,
                        zucfScope = secondStep.zucfScope,
                        option = option,
                        edScope = option.EDScope
                    )
                )
                zcredScope.TVL1 =
                    zcredScope.TVL2!! * input.NW / (uB * sqrt(input.KPD) * input.OMEG)//здесь возможно формула перепутана, NW and OMEG местами
                val creationData = CreationData(
                    edScope = option.EDScope,
                    option = option,
                    gearWheelStepsArray = arrayOf(firstStep, secondStep)
                )
                creationDataList.add(creationData)
            }
        }
        return creationDataList
    }

    private fun ZC1RED(
        input: InputData,
        zcredScope: ZCREDScope,
        options: List<ReducerOptionTemplate>,
        creationDataList: MutableList<CreationData>
    ): List<CreationData> {
        options.forEach { option ->
            option.apply {
                //Единственная ступень
                zcredScope.TVL2 =
                    9550f * option.EDScope!!.PED!! * input.U0 * u * input.KPD / option.EDScope.NED!!
                var NV2: Float = option.EDScope.NED!! / (u * input.U0)
                var onlyStep = OneGearWheelStep()//Здесь сохраняем скоупы тихоходной ступени
                ZUCMethods.enterZUC(
                    ZUCMethods.Arguments(
                        SIGN = input.SIGN[0],
                        IST = 0,
                        N2 = NV2,
                        T2 = zcredScope.TVL2!! * input.OMEG / input.NW,
                        uStup = u,
                        inputData = input,
                        dopnScope = onlyStep.dopnScope,
                        zuc1hScope = onlyStep.zuc1hScope,
                        zucepScope = onlyStep.zucepScope,
                        zuc2hScope = onlyStep.zuc2hScope,
                        zucfScope = onlyStep.zucfScope,
                        option = option,
                        edScope = option.EDScope
                    )
                )
                //zc2redScope.TVL1 = 0f вроде она у нас бесполезна, мы не печатаем внутри ничего
                zcredScope.TVL3 = 0f
                zcredScope.TVL1 =
                    zcredScope.TVL2!! * input.NW / (uB * sqrt(input.KPD) * input.OMEG)//здесь возможно формула перепутана, NW and OMEG местами
                val creationData = CreationData(
                    edScope = option.EDScope,
                    option = option,
                    gearWheelStepsArray = arrayOf(onlyStep)
                )//Возвращаем только 1 ступень
                creationDataList.add(creationData)
            }
        }
        return creationDataList
    }
}

