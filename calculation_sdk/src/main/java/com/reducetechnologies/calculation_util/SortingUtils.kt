package com.reducetechnologies.calculation_util

import com.reducetechnologies.calculations.CreationData
import com.reducetechnologies.calculations.InputData
import com.reducetechnologies.calculations_entity.MasreScope
import kotlin.math.pow

/**
 * Нужен для инкапсуляции вычисления сортировочных параметров
 */
internal class SortingUtils(
    private val inputData: InputData,
    private val creationDataList: ArrayList<CreationData>,
    private val masreScopeList: ArrayList<MasreScope>
) {
    /**
     * Вычисляет разницу между [SGHD] and [SGH]
     */
    fun diffSGH(creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHD.min()!! -
                    creationData.gearWheelStepsArray[0].zuc2hScope.SGH!!
        } else {
            return (creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHD.min()!! -
                    creationData.gearWheelStepsArray[0].zuc2hScope.SGH!!) +
                    (creationData.gearWheelStepsArray[1].dopnScope.wheelsSGHD.min()!! -
                            creationData.gearWheelStepsArray[1].zuc2hScope.SGH!!)
        }
    }

    /**
     * Вычисляет разницу между [SGHMD] and [SGHM]
     */
    private fun diffSGHM(creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHMD.min()!! -
                    creationData.gearWheelStepsArray[0].zuc2hScope.SGHM!!
        } else {
            return (creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHMD.min()!! -
                    creationData.gearWheelStepsArray[0].zuc2hScope.SGHM!!) +
                    (creationData.gearWheelStepsArray[1].dopnScope.wheelsSGHMD.min()!! -
                            creationData.gearWheelStepsArray[1].zuc2hScope.SGHM!!)
        }
    }

    /**
     * Применяется в [diffSGF] and [diffSGFM]
     */
    private fun diffArr(arrayBig: Array<Int>, arraySmall: Array<Float>): Float {
        return (arrayBig.min()!! - arraySmall.max()!!)
    }

    /**
     * Вычисляет разницу между [SGFD] and [SGF]
     */
    fun diffSGF(creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return diffArr(
                creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFD,
                creationData.gearWheelStepsArray[0].zucfScope.SGF
            )
        } else {
            return diffArr(
                creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFD,
                creationData.gearWheelStepsArray[0].zucfScope.SGF
            ) +
                    diffArr(
                        creationData.gearWheelStepsArray[1].dopnScope.wheelsSGFD,
                        creationData.gearWheelStepsArray[1].zucfScope.SGF
                    )
        }
    }

    /**
     * Вычисляет разницу между [SGFMD] and [SGFM]
     */
    private fun diffSGFM(creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return diffArr(
                creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFMD,
                creationData.gearWheelStepsArray[0].zucfScope.SGFM
            )
        } else {
            return diffArr(
                creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFMD,
                creationData.gearWheelStepsArray[0].zucfScope.SGFM
            ) +
                    diffArr(
                        creationData.gearWheelStepsArray[1].dopnScope.wheelsSGFMD,
                        creationData.gearWheelStepsArray[1].zucfScope.SGFM
                    )
        }
    }

    /**
     * Вычисляет разницу между [UCalculated] and [UREMA]
     */
    private fun diffUCalc(creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return inputData.UREMA - creationData.gearWheelStepsArray[0].zuc1hScope.UCalculated
        } else {
            return inputData.UREMA - (creationData.gearWheelStepsArray[0].zuc1hScope.UCalculated *
                    creationData.gearWheelStepsArray[1].zuc1hScope.UCalculated)
        }
    }

    /**
     * Вычисляет сумму HRC (как некоторый показатель дороговизны, чем меньше общая сумма, тем
     * дешевле, хотя конечно не всегда так, есть ещё объём материала
     */
    fun minHRC(creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return (creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHD.max()!! +
                    creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFD.max()!!).toFloat()
        } else {
            return (creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHD.max()!! +
                    creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFD.max()!! +
                    creationData.gearWheelStepsArray[1].dopnScope.wheelsSGHD.max()!! +
                    creationData.gearWheelStepsArray[1].dopnScope.wheelsSGFD.max()!!).toFloat()
        }
    }

    /**
     * Вычисляет сумму межосевых расстояний [AW] и служит тоже как показатель удобства, размеров и тд.
     */
    fun sumAW(creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return creationData.gearWheelStepsArray[0].zuc1hScope.AW
        } else {
            return creationData.gearWheelStepsArray[0].zuc1hScope.AW +
                    creationData.gearWheelStepsArray[1].zuc1hScope.AW
        }
    }

    /**
     * Вычисляет сумму [EPALF] and [EPBET] как некоторый показатель
     * Не должен использоваться с шевронными передачами, там это бессмысленно
     */
    private fun Ep(creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return creationData.gearWheelStepsArray[0].zucepScope.EPALF!! +
                    creationData.gearWheelStepsArray[0].zuc2hScope.EPBET!!
        } else {
            return creationData.gearWheelStepsArray[0].zucepScope.EPALF!! +
                    creationData.gearWheelStepsArray[0].zuc2hScope.EPBET!! +
                    creationData.gearWheelStepsArray[1].zucepScope.EPALF!! +
                    creationData.gearWheelStepsArray[1].zuc2hScope.EPBET!!
        }
    }

    /**
     * Вычисляет общую сумму всех размеров редуктора (ширина, высота, длина) и является некоторым
     * показателем оптимальности
     */
    private fun minSize(masreScope: MasreScope): Float {
        return masreScope.BRE!! + masreScope.HRE!! + masreScope.LRE!!
    }

    /**
     * Вычисляет объём редуктора (коробки, а не материала)
     */
    fun volume(masreScope: MasreScope): Float {
        return (masreScope.BRE!! * masreScope.HRE!! * masreScope.LRE!!)/1_000_000_000
    }

    /**
     * Заполняет сортировочные поля [sorting] и [MARE]
     * Нужно ещё продумать сортировочные коэффициенты, чтобы выдавало более-менее адекватные результаты
     */
    fun fillingSortingFields() {
        creationDataList.forEachIndexed { index, creationData ->
            val ind: Float = (diffSGH(creationData).pow(2) + diffSGHM(creationData).pow(2) +
                    diffSGF(creationData).pow(2) + diffSGFM(creationData).pow(2) +
                    diffUCalc(creationData).pow(2) + minHRC(creationData).pow(2) +
                    sumAW(creationData).pow(2) - Ep(creationData).pow(2) +
                    minSize(masreScope = masreScopeList[index]).pow(2) + masreScopeList[index].MARE!!.pow(2)
                    ).pow(1/2f)
            creationDataList[index].sorting = ind
            creationDataList[index].sortingMass = masreScopeList[index].MARE!!
            masreScopeList[index].sorting = ind
        }
    }
}