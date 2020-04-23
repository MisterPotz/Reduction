package com.reducetechnologies.calculations
import com.reducetechnologies.tables_utils.table_contracts.EDDataTable
import com.reducetechnologies.tables_utils.table_contracts.HRCTable
import java.lang.Exception
import kotlin.math.sqrt
/**
 * [uRED] - то значение, на которое нацеливаемся (не конечное)
 */

data class ReducerOptionTemplate(
    var NED: Int? = null, val HRC: Array<Float>, val uB: Float, val uT: Float, val u: Float,
    val uRatio: Float, val uRED: Float, val PSB: Float, val PSIM: Int, val PED: Float,
    val EDScope: EDScope? = null
)

class AllReducersOptionsClass(val HRCTable: HRCTable,
                              val edDataTable: EDDataTable) {
    /**
     * [NEDOptions] - частота вращения электродвигателя
     * 0 -> 3000, 1 -> 1500, 2 -> 1000, 3 -> 750
     * [HRC] - пары твёрдости для колёс одной ступени
     * [uRatio] - отношение передаточного числа быстроходной ступени к тихоходной
     * [PSB] - коэффициент ширины колеса по межосевому расстоянию
     */
    val NEDOptions: MutableList<Int> = mutableListOf(0, 1, 2, 3)
    //val HRC = Tables.Table().tableMaster!!.getHRCTable().HRC
    /*val HRC: Array<Array<Float>> = arrayOf(
        arrayOf(28.5f, 24.8f), arrayOf(49f, 28.5f),
        arrayOf(59f, 59f)
    )*/
    val HRC = HRCTable.HRC
    /**
     * Нижние табличке по необходимости можно тоже будет в бд залить
     */
    val uRatio: Array<Float> = arrayOf(0.7f, 1f, 1.3f)
    val PSB: Array<Float> = arrayOf(0.25f, 0.4f)
    fun tryToCalculateOptions(inputData: InputData): List<ReducerOptionTemplate> {
        /**
         * Если рассчитывать ЭД не нужно, просто идём сразу в расчёт опций с ped = 0
         */
        if (!inputData.isED)
            return enterOptions(inputData, pedCalculated = 0f)
        var pedCalculated: Float = (inputData.TT * inputData.NT / (9550f * inputData.KPD))//расчётное значение мощности редуктора
        if (inputData.U0 != 1f)//если присутствует промежуточная передача между электродвигателем и редуктором
            pedCalculated *= 0.96f
        if (pedCalculated < 15f)
            return enterOptions(inputData, pedCalculated)
        else
            throw Exception("P is more than 15, so we wont choose ED for you")
    }
    private fun enterOptions(inputData: InputData, pedCalculated: Float): List<ReducerOptionTemplate> {
        var options: MutableList<ReducerOptionTemplate> = mutableListOf()
        var URED: Float
        //Вход в циклы
        for (ned in NEDOptions) {
            var edScope = EDScope()
            //Функция подбора электродвигателя из стандартного ряда
            if (inputData.isED) {
                EDMethodsClass(edDataTable).EDCalculate(
                    EDMethodsClass.Arguments(
                        PEDCalculated = pedCalculated,
                        NEDFixed = ned
                    ),
                    edScope
                )
            }
            URED = if (edScope.NED != null) {
                edScope.NED!! / (inputData.NT * inputData.U0)
            } else inputData.UREMA //если не подбираем редуктор или его невозможно подобрать
            if (URED > inputData.UREMA)//UREMA - должно вводиться пользователем, максимальное перед отношение
                continue
            else {
                hrcEnum(inputData = inputData,
                    pedCalculated = pedCalculated,
                    URED = URED,
                    options = options,
                    edScope = edScope)
            }
        }
        return options
    }
    private fun hrcEnum(inputData: InputData,
                        pedCalculated: Float,
                        URED: Float,
                        options: MutableList<ReducerOptionTemplate>,
                        edScope: EDScope) {
        for (hrc: Array<Float> in HRC) {
            var PSIM: Int? = null
            if (hrc[0] <= 35)
                PSIM = 30//расхождения с диаграммой, спросить
            else if (hrc[0] > 35 && hrc[0] <= 50)
                PSIM = 25
            else if (hrc[0] > 50)
                PSIM = 20
            if (inputData.ISTCol == 1) {
                //Возвращаем psbEnum
                psbEnum(inputData = inputData,
                    pedCalculated = pedCalculated,
                    PSIM = PSIM,
                    URED = URED,
                    uB = 0f,//тк здесь не будет перебора для uRatio
                    uT = 0f,//тк здесь не будет перебора для uRatio
                    uRatio = 1f,//тк здесь не будет перебора для uRatio
                    hrc = hrc,
                    options = options,
                    edScope = edScope)
            }
            else {
                //Возвращаем uRatioEnum
                uRatioEnum(inputData = inputData,
                    pedCalculated = pedCalculated,
                    PSIM = PSIM,
                    URED = URED,
                    hrc = hrc,
                    options = options,
                    edScope = edScope)
            }
        }
    }
    private fun uRatioEnum(inputData: InputData,
                           pedCalculated: Float,
                           PSIM: Int?,
                           URED: Float,
                           hrc: Array<Float>,
                           options: MutableList<ReducerOptionTemplate>,
                           edScope: EDScope) {
        for (uRatio in uRatio) {
            var uB: Float = sqrt(URED * uRatio)
            var uT: Float = URED / uB
            //Возвращаем psbEnum
            psbEnum(inputData = inputData,
                pedCalculated = pedCalculated,
                PSIM = PSIM,
                URED = URED,
                uB = uB,
                uT = uT,
                uRatio = uRatio,
                hrc = hrc,
                options = options,
                edScope = edScope)
        }
    }
    private fun psbEnum(inputData: InputData, pedCalculated: Float,
                        PSIM: Int?,
                        URED: Float,
                        uB: Float,
                        uT: Float,
                        uRatio: Float,
                        hrc: Array<Float>,
                        options: MutableList<ReducerOptionTemplate>,
                        edScope: EDScope) {
        for (psb in PSB) {
            //NED будет null, если не будет расчёта редуктора
            //psb1 нужен только чтобы учесть случай с NWR > 1
            var psb1: Float = if (inputData.NWR > 1)
                2*psb
            else psb
            options.add(
                ReducerOptionTemplate(
                    NED = edScope.NED,
                    HRC = hrc,
                    uB = uB,
                    uT = uT,
                    u = if (uB != 0f) (uB * uT) else URED,
                    uRatio = uRatio,
                    uRED = URED,
                    PSB = psb1,
                    PSIM = PSIM!!,
                    PED = pedCalculated,
                    EDScope = edScope
                )
            )
        }
    }
}
class EDMethodsClass(val edDataTable: EDDataTable) {
    data class Arguments(var PEDCalculated: Float, var NEDFixed: Int)
    fun EDCalculate(args: Arguments, edScope: EDScope) {
        for (edDataRow in edDataTable.map){
            if (edDataRow.key > args.PEDCalculated) {
                edScope.apply {
                    PED = edDataRow.key
                    NED = edDataRow.peds[args.NEDFixed]
                    TTED = edDataRow.tteds[args.NEDFixed]
                    D1ED = edDataRow.d1eds[args.NEDFixed]
                    L1ED = edDataRow.l1eds[args.NEDFixed]
                    H1ED = edDataRow.h1eds[args.NEDFixed]
                    MAED = edDataRow.maes[args.NEDFixed]
                }
                return
            }
        }
        throw Exception("We didnt find ED with P more then PEDCalculated (we have not more than P=15)")
    }//Выбор электродвигателя из стандартного ряда
}

data class EDScope(
    var PED: Float? = null,
    var NED: Int? = null,
    var TTED: Float? = null,
    var D1ED: Int? = null,
    var L1ED: Int? = null,
    var H1ED: Int? = null,
    var MAED: Float? = null
)



