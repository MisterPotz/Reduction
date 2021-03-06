package com.reducetechnologies.calculations

import javax.inject.Inject
import kotlin.math.sqrt


data class CreationData(
    // здесь будут храниться все дата скоупы, чтобы потом передать только 1 объект в конструктор редуктора
    //val inputData: InputData, скорее всего будем передавать input напрямую
    val edScope: EDScope,
    val option: ReducerOptionTemplate,
    val zcredScope: ZCREDScope,
    val gearWheelStepsArray: Array<OneGearWheelStep> = arrayOf(),
    var sorting: Float = 0f,
    var sortingMass: Float = 0f
)

//Для одной ступени
data class OneGearWheelStep(
    val dopnScope: DOPNScope = DOPNScope(),
    val zuc1hScope: ZUC1HScope = ZUC1HScope(),
    val zuc2hScope: ZUC2HScope = ZUC2HScope(),
    val zucepScope: ZUCEPScope = ZUCEPScope(),
    val zucfScope: ZUCFScope = ZUCFScope()
)

class ZCREDMethodsClass @Inject constructor(private val zucMethod: ZUCMethodsClass) {
    fun enterZCRED(input: InputData, options: List<ReducerOptionTemplate>): List<CreationData> {
        var creationDataList: MutableList<CreationData> = arrayListOf()
        if (input.ISTCol == 1)
            return ZC1RED(
                input = input,
                options = options,
                creationDataList = creationDataList
            )
        else
            return ZC2RED(
                input = input,
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
        options: List<ReducerOptionTemplate>,
        creationDataList: MutableList<CreationData>
    ): List<CreationData> {
        options.forEach { option ->
            option.apply {
                val zcredScope = ZCREDScope()
                //Тихоходная ступень
                if (option.EDScope!!.PED != null){
                    zcredScope.TVL3 =
                        9550f * this.EDScope!!.PED!! * input.U0 * u * input.KPD / EDScope.NED!!
                }
                else {
                    zcredScope.TVL3 = input.TT*input.OMEG/(input.NW)
                }
                if (EDScope!!.NED != null){
                    zcredScope.NV3 = EDScope.NED!! / (u * input.U0)
                }
                else {
                    zcredScope.NV3 = input.NT
                }
                val firstStep = OneGearWheelStep()//Здесь сохраняем скоупы тихоходной ступени
                zucMethod.enterZUC(
                    ZUCMethodsClass.Arguments(
                        SIGN = input.SIGN[1],
                        IST = 1,
                        N2 = zcredScope.NV3!!,
                        T2 = zcredScope.TVL3!! * input.OMEG / input.NW,
                        uStup = uT,
                        inputData = input,
                        dopnScope = firstStep.dopnScope,
                        zuc1hScope = firstStep.zuc1hScope,
                        zucepScope = firstStep.zucepScope,
                        zuc2hScope = firstStep.zuc2hScope,
                        zucfScope = firstStep.zucfScope,
                        option = this,
                        edScope = EDScope
                    )
                )
                //zc2redScope.TVL1 = 0f вроде она у нас бесполезна, мы не печатаем внутри ничего
                zcredScope.TVL2 =
                    zcredScope.TVL3!! * input.OMEG / (firstStep.zuc1hScope.UCalculated * sqrt(input.KPD) * input.NW)
                if (input.TIPRE == 4 && input.SIGN[0] == 1 && input.SIGN[1] == 1) {
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
                val secondStep = OneGearWheelStep()//Здесь сохраняем скоупы бысроходной ступени
                zucMethod.enterZUC(
                    ZUCMethodsClass.Arguments(
                        SIGN = input.SIGN[0],
                        IST = 0,
                        N2 = zcredScope.NV3!! * uT,
                        T2 = zcredScope.TVL2!!,
                        AWTipre4 = firstStep.zuc1hScope.AW,
                        uStup = uB,
                        inputData = input,
                        dopnScope = secondStep.dopnScope,
                        zuc1hScope = secondStep.zuc1hScope,
                        zucepScope = secondStep.zucepScope,
                        zuc2hScope = secondStep.zuc2hScope,
                        zucfScope = secondStep.zucfScope,
                        option = this,
                        edScope = EDScope
                    )
                )
                zcredScope.NV2 = zcredScope.NV3!!*uT
                zcredScope.NV1 = zcredScope.NV2!!*uB
                zcredScope.TVL1 =
                    zcredScope.TVL2!! * input.NW / (secondStep.zuc1hScope.UCalculated * sqrt(input.KPD) * input.OMEG)//здесь возможно формула перепутана, NW and OMEG местами
                val creationData = CreationData(
                    edScope = EDScope,
                    option = this,
                    zcredScope = zcredScope,
                    gearWheelStepsArray = arrayOf(firstStep, secondStep)
                )
                creationDataList.add(creationData)
            }
        }
        return creationDataList
    }

    private fun ZC1RED(
        input: InputData,
        options: List<ReducerOptionTemplate>,
        creationDataList: MutableList<CreationData>
    ): List<CreationData> {
        options.forEach { option ->
            option.apply {
                val zcredScope = ZCREDScope()
                //Единственная ступень
                if (option.EDScope!!.PED != null){
                    zcredScope.TVL2 =
                        9550f * EDScope!!.PED!! * input.U0 * u * input.KPD / EDScope.NED!!
                }
                else {
                    zcredScope.TVL2 = input.TT*input.OMEG/(input.NW)
                }
                if (EDScope!!.NED != null){
                    zcredScope.NV2 = EDScope.NED!! / (u * input.U0)
                }
                else {
                    zcredScope.NV2 = input.NT
                }
                val onlyStep = OneGearWheelStep()//Здесь сохраняем скоупы тихоходной ступени
                zucMethod.enterZUC(
                    ZUCMethodsClass.Arguments(
                        SIGN = input.SIGN[0],
                        IST = 0,
                        N2 = zcredScope.NV2!!,
                        T2 = zcredScope.TVL2!! * input.OMEG / input.NW,
                        uStup = u,
                        inputData = input,
                        dopnScope = onlyStep.dopnScope,
                        zuc1hScope = onlyStep.zuc1hScope,
                        zucepScope = onlyStep.zucepScope,
                        zuc2hScope = onlyStep.zuc2hScope,
                        zucfScope = onlyStep.zucfScope,
                        option = this,
                        edScope = EDScope
                    )
                )
                zcredScope.NV1 = zcredScope.NV2!!*u
                //zc2redScope.TVL3 = 0f вроде она у нас бесполезна, мы не печатаем внутри ничего
                zcredScope.TVL3 = 0f
                zcredScope.TVL1 =
                    zcredScope.TVL2!! * input.NW / (onlyStep.zuc1hScope.UCalculated * sqrt(input.KPD) * input.OMEG)//здесь возможно формула перепутана, NW and OMEG местами
                val creationData = CreationData(
                    edScope = EDScope,
                    option = this,
                    zcredScope = zcredScope,
                    gearWheelStepsArray = arrayOf(onlyStep)
                )//Возвращаем только 1 ступень
                creationDataList.add(creationData)
            }
        }
        return creationDataList
    }
}

/**
 * [TVL1], [TVL2], [TVL3] - вращающие моменты на валах (1 - входной, 3 - выходной (для односутпенчатых
 * выходной выл - 2))
 * [NV1], [NV2], [NV3] - частоты вращения валов 1, 2, 3
 */
data class ZCREDScope(
    var TVL1: Float? = null,
    var TVL2: Float? = null,
    var TVL3: Float? = null,
    var NV1: Float? = null,
    var NV2: Float? = null,
    var NV3: Float? = null
)
