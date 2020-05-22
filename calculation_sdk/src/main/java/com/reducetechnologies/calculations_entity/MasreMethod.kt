package com.reducetechnologies.calculations_entity

import com.reducetechnologies.calculations.CreationData
import com.reducetechnologies.calculations.InputData
import kotlin.math.PI
import kotlin.math.pow

class MasreMethod(private val inputData: InputData,
                           private val creationDataList: List<CreationData>) {
    fun enterLoopMasre(): ArrayList<MasreScope> {
        var masreScopeList: ArrayList<MasreScope> = arrayListOf()
        creationDataList.forEach {
            masreScopeList.add(enterMasre(it))
        }
        masreScopeList.trimToSize()
        return masreScopeList
    }

    private fun enterMasre(creationData: CreationData): MasreScope {
        var masreScope: MasreScope = MasreScope()
        masreScope.apply {
            if (inputData.BKAN == 1) {
                //Судя по всему, BET должна быть в радианах, иначе очень много получится
                BKAN = (9.28f*creationData.gearWheelStepsArray[0].dopnScope.M + 0.974f)*
                        creationData.gearWheelStepsArray[0].zuc1hScope.BET
            }
            STEN = 2f*(0.1f*creationData.zcredScope.TVL2!!).pow(1/4f)
            if (STEN!! < 6f) {
                STEN = 6f
            }
            DVAL1 = 10f*creationData.zcredScope.TVL1!!.pow(1/3f)
            DVAL2 = 8f*creationData.zcredScope.TVL2!!.pow(1/3f)
            if (inputData.TIPRE < 3) {
                //Уход в OneStepCylindical
                OneStepCylindrical(creationData, masreScope)
            }
            if (inputData.TIPRE > 3) {
                //Уход в TwoStepCylindrical
                TwoStepCylindrical(creationData, masreScope)
            }
            //Уход в 13
            goto13(creationData, masreScope)
        }
        return masreScope
    }

    private fun OneStepCylindrical(creationData: CreationData, masreScope: MasreScope) {
        //Если будет червячный, то здесь сделать уход в него
        masreScope.apply {
            LRE = 1.5f*DVAL1!! + creationData.gearWheelStepsArray[0].zuc1hScope.AW +
                    creationData.gearWheelStepsArray[0].zuc2hScope.DW[1]/2 + 4f*STEN!!
            BRE = creationData.gearWheelStepsArray[0].zuc1hScope.BW1!! + 4f*STEN!! + BKAN
            HRE = creationData.gearWheelStepsArray[0].zuc2hScope.DW[1] + 6f*STEN!!
        }
        //Уход в расчёт объёма
        vCalculation(creationData, masreScope)
        return
    }

    private fun TwoStepCylindrical(creationData: CreationData, masreScope: MasreScope) {
        //Двухступенчатые соосные + если будут, добавить сюда коническо-цилиндрические
        masreScope.apply {
            DVAL3 = 7f*creationData.zcredScope.TVL3!!.pow(1/3f)
            STEN = 2f*(0.1f*creationData.zcredScope.TVL3!!).pow(1/4f)
            if (STEN!! < 6f) {
                STEN = 6f
            }
            if (inputData.TIPRE == 4 || inputData.TIPRE == 6) {
                LRE = creationData.gearWheelStepsArray[1].zuc1hScope.AW +
                        creationData.gearWheelStepsArray[1].zuc2hScope.DW[1]/2 +
                        4f*STEN!!
                if (inputData.NW >= 2) {
                    LRE = 2f*creationData.gearWheelStepsArray[1].zuc1hScope.AW +
                            creationData.gearWheelStepsArray[0].zuc2hScope.DW[1] +
                            4f*STEN!!
                }
                BRE = creationData.gearWheelStepsArray[0].zuc1hScope.BW1!! +
                        creationData.gearWheelStepsArray[1].zuc1hScope.BW1!! + 10f*STEN!!
                //Идём в 9
                goTo9(creationData,masreScope)
            }
            if (inputData.TIPRE == 5 || inputData.TIPRE == 7){
                //Здесь 8
                LRE = 1.5f*DVAL1!! + creationData.gearWheelStepsArray[0].zuc1hScope.AW +
                        creationData.gearWheelStepsArray[1].zuc1hScope.AW +
                        creationData.gearWheelStepsArray[1].zuc2hScope.DW[1]/2 + 4f*STEN!!
                BRE = creationData.gearWheelStepsArray[0].zuc1hScope.BW1!! +
                        creationData.gearWheelStepsArray[1].zuc1hScope.BW1!! + BKAN + 4f*STEN!!
                //Уход в 9
                goTo9(creationData,masreScope)
            }
        }
    }

    private fun vCalculation(creationData: CreationData, masreScope: MasreScope) {
        masreScope.apply {
            DVAL = DVAL1!!.pow(2) + DVAL2!!.pow(2)
            VKLS = PI.toFloat()/4f * (creationData.gearWheelStepsArray[0].zuc2hScope.DW[0].pow(2) +
                    (creationData.gearWheelStepsArray[0].zuc2hScope.DW[1].pow(2) +
                            DVAL2!!.pow(2))*0.54f)*
                    creationData.gearWheelStepsArray[0].zuc1hScope.BW1!!
            VKLSM = PI.toFloat()/4f *(creationData.gearWheelStepsArray[0].zuc2hScope.DW[0].pow(2) +
                    creationData.gearWheelStepsArray[0].zuc2hScope.DW[1].pow(2) -
                    DVAL2!!.pow(2))*
                    creationData.gearWheelStepsArray[0].zuc1hScope.BW1!!
        }
        return
    }

    private fun goTo9(creationData: CreationData, masreScope: MasreScope) {
        masreScope.apply {
            HRE = creationData.gearWheelStepsArray[1].zuc2hScope.DW[1] + 6*STEN!!
            DVAL = DVAL1!!.pow(2) + inputData.NW*DVAL2!!.pow(2) + DVAL3!!.pow(2)
            VKLS = PI.toFloat()/4f * ((creationData.gearWheelStepsArray[0].zuc2hScope.DW[0].pow(2) +
                    inputData.NW*(creationData.gearWheelStepsArray[0].zuc2hScope.DW[1].pow(2) +
                    DVAL2!!.pow(2))*0.54f)*creationData.gearWheelStepsArray[0].zuc1hScope.BW1!! +
                    (inputData.NW*creationData.gearWheelStepsArray[1].zuc2hScope.DW[0].pow(2) +
                            (creationData.gearWheelStepsArray[1].zuc2hScope.DW[1].pow(2) +
                                    DVAL3!!.pow(2))*0.54f)*
                    creationData.gearWheelStepsArray[1].zuc1hScope.BW1!!)
            VKLSM = PI.toFloat()/4f * ((creationData.gearWheelStepsArray[0].zuc2hScope.DW[0].pow(2) +
                    inputData.NW*creationData.gearWheelStepsArray[0].zuc2hScope.DW[1].pow(2) -
                    inputData.NW*DVAL2!!)*creationData.gearWheelStepsArray[0].zuc1hScope.BW1!! +
                    (inputData.NW*creationData.gearWheelStepsArray[1].zuc2hScope.DW[0].pow(2) +
                            creationData.gearWheelStepsArray[1].zuc2hScope.DW[1].pow(2) -
                            DVAL3!!.pow(2))*creationData.gearWheelStepsArray[1].zuc1hScope.BW1!!)
        }
        return
    }

    private fun goto13(creationData: CreationData, masreScope: MasreScope) {
        masreScope.apply {
            if (inputData.NW > 2) {
                VSTEN = 2* PI.toFloat()*STEN!!*((creationData.gearWheelStepsArray[1].zuc1hScope.AW +
                        creationData.gearWheelStepsArray[0].zuc2hScope.DW[1]/2 + 2f*STEN!!)*
                        BRE!! + (creationData.gearWheelStepsArray[1].zuc1hScope.AW +
                        creationData.gearWheelStepsArray[0].zuc2hScope.DW[1]/2 + 2f*STEN!!).pow(2))
            }
            else {
                VSTEN = (LRE!!*HRE!! + HRE!!*BRE!! + BRE!!*LRE!!)*2f*STEN!!
            }
            VBOB = 2f*4.9f*DVAL!!*8f*STEN!!
            VVAL = PI.toFloat()/4f * (BRE!! + 16f*STEN!!)*DVAL!!
            VPOES = (LRE!! + BRE!!)*2f*3.5f*STEN!!*8f*STEN!!
            if (inputData.NW > 2) {
                VPOES = VPOES!!/3
            }
            if (VKLS!! > VKLSM!!) {
                VKLS = VKLSM!!
            }
            VRE = (VSTEN!! + VBOB!! + VVAL!! + VPOES!! + VKLS!!)/1_000_000_000
            MARE = VRE!!*7.8f*1_000
            MAKLS = VKLS!!*7.8f/1_000_000
        }
        return
    }
}

/**
 * [BKAN] - ширина канавки между полушевронами колеса, мм
 * [STEN] - толщина стенки корпуса редуктора, мм
 * [DVAL1], [DVAL2], [DVAL3], [DVAL] - диаметры валов редуктора, к которым приложены TVL1, TVL2,
 * TVL3 соответственно, мм
 * [LRE] - длина корпуса редуктора, мм
 * [BRE] - ширина корпуса редуктора, мм
 * [HRE] - высота корпуса редуктора, мм
 * [VKLS] - объём колёс, мм^3
 * [VKLSM] - максимальный объём колёс, мм^3
 * [VBOB] - объём бобышек, мм^3
 * [VVAL] - объём валов, мм^3
 * [VPOES] - объём поясов, мм^3
 * [VRE] - объём редуктора, мм^3
 * [MARE] - масса редуктора, кг
 * [MAKLS] - масса колёс, кг
 */
data class MasreScope(
    var BKAN: Float = 0f,
    var STEN: Float? = null,
    var DVAL1: Float? = null,
    var DVAL2: Float? = null,
    var DVAL3: Float? = null,
    var LRE: Float? = null,
    var BRE: Float? = null,
    var HRE: Float? = null,
    var DVAL: Float? = null,
    var VKLS: Float? = null,
    var VKLSM: Float? = null,
    var VSTEN: Float? = null,
    var VBOB: Float? = null,
    var VVAL: Float? = null,
    var VPOES: Float? = null,
    var VRE: Float? = null,
    var MARE: Float? = null,
    var MAKLS: Float? = null,
    var sorting: Float = 0f
)