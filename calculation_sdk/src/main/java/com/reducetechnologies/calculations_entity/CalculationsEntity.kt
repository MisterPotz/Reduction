package com.reducetechnologies.calculations_entity

import com.reducetechnologies.calculation_util.NumRound
import com.reducetechnologies.calculation_util.SortingUtils
import com.reducetechnologies.calculations.CreationData
import com.reducetechnologies.calculations.InputData
import com.reducetechnologies.di.CalculationsComponent
import com.reducetechnologies.specificationsAndRequests.Specifications
import kotlin.math.PI

//Мы не подрубаем его в CalculationComponent, потому что всё можно передать ему просто в конструкторе
class CalculationsEntity constructor(private val inputData: InputData,
                                     private val calculationsComponent: CalculationsComponent) {
    private val creationDataList: List<CreationData>
    private var masreScopeList: ArrayList<MasreScope>
    private val properCreationData: ArrayList<CreationData>
    private val reducerDataList: ArrayList<ReducerData>
    private val differentSortionsOfReducerData: HashMap<String, ArrayList<ReducerData>>
    val outputData: OutputData

    private val sortingUtils: SortingUtils
    private val numRound: NumRound

    init {
        //Инициализируем numRound
        numRound = NumRound
        //Забираем creationDataList
        creationDataList = getCreationDataList()
        //Забираем masreScopeList
        masreScopeList = getMasreScopeList()
        //Просеиваем данные по самым важным критериям и одновременно просеиваем masreScope's
        properCreationData = weedOutCreationDataList()
        //Инициализация инстанта sortingUtils
        sortingUtils = SortingUtils(
            inputData = inputData,
            creationDataList = properCreationData,
            masreScopeList = masreScopeList
        )
        //Заполняем сортировочные поля для последующей сортировки
        sortingUtils.fillingSortingFields()
        //Инициализируем outputDataList
        reducerDataList = initializeOutputDataList()
        //Инициализируем списки отсортированных данных
        differentSortionsOfReducerData = initializeSortedLists()
        //Инициализация outputData
        outputData = OutputData(
            reducerDataList = reducerDataList,
            SortingLists = differentSortionsOfReducerData
        )
    }

    /**
     * Для инициализации [creationDataList]
     */
    private fun getCreationDataList(): List<CreationData> {
        val reducersOptions = calculationsComponent
            .getAllReducersOptions()
            .tryToCalculateOptions(inputData)
        return calculationsComponent
            .getZCREDMethods()
            .enterZCRED(
                input = inputData,
                options = reducersOptions
            )
    }

    /**
     * Для инициализации [masreScopeList]
     */
    private fun getMasreScopeList(): ArrayList<MasreScope> {
        val masreMethod: MasreMethod = MasreMethod(inputData, creationDataList)
        return masreMethod.enterLoopMasre()
    }

    /**
     * Для отсеивания вариантов по критериям несоответствия [inputData]
     */
    private fun weedOutCreationDataList(): ArrayList<CreationData> {
        val properCreationDataArrList: ArrayList<CreationData> = arrayListOf()
        val newMasreScopeList: ArrayList<MasreScope> = arrayListOf()
        creationDataList.forEachIndexed loop@{ i, creationData ->
            creationData.gearWheelStepsArray.forEachIndexed { j, oneGearWheelStep ->
                // Отсеиваем по несоответствию по рассчитанным контактным напряжениям
                if (oneGearWheelStep.dopnScope.wheelsSGHD.min()!! <= oneGearWheelStep.zuc2hScope.SGH!!) {
                    return@loop
                }
                // Отсеиваем по несоответствию по рассчитанным максимальным контактным напряжениям
                if (oneGearWheelStep.dopnScope.wheelsSGHMD.min()!! <= oneGearWheelStep.zuc2hScope.SGHM!!) {
                    return@loop
                }
                // Отсеиваем по несоответствию по рассчитанным изгибным напряжениям
                if (oneGearWheelStep.dopnScope.wheelsSGFD.min()!! <= oneGearWheelStep.zucfScope.SGF.max()!!) {
                    return@loop
                }
                // Отсеиваем по несоответствию по рассчитанным максимальным изгибным напряжениям
                if (oneGearWheelStep.dopnScope.wheelsSGFMD.min()!! <= oneGearWheelStep.zucfScope.SGFM.max()!!) {
                    return@loop
                }
                // Отсеиваем по интерференции для передач с внешним зацеплением
                if (inputData.SIGN[j] > 0) {
                    if (oneGearWheelStep.zucepScope.interference) {
                        return@loop
                    }
                }
                // Отсеиваем по коэффициенту перекрытия (только для колёс с прямыми зубьями)
                if (inputData.wheelSubtype[j] == Specifications.WheelSubtype.SPUR) {
                    if (oneGearWheelStep.zucepScope.isEpalfLess) {
                        return@loop
                    }
                }
            }
            // Отсеиваем по несоответствию передаточного отношения
            if (creationData.gearWheelStepsArray.size == 1) {
                if (creationData.gearWheelStepsArray[0].zuc1hScope.UCalculated <= 0.9f*inputData.UREMA) {
                    return@loop
                }
                //Отсеиваем по максимальному углу наклона зубьев
                if (creationData.gearWheelStepsArray[0].zuc1hScope.BET > inputData.BETMA) {
                    return@loop
                }
            }
            else {
                if (inputData.UREMA <= 40f) {
                    if (creationData.gearWheelStepsArray[0].zuc1hScope.UCalculated*
                        creationData.gearWheelStepsArray[1].zuc1hScope.UCalculated <= 0.9f*inputData.UREMA) {
                        return@loop
                    }
                }
                else {
                    if (creationData.gearWheelStepsArray[0].zuc1hScope.UCalculated*
                        creationData.gearWheelStepsArray[1].zuc1hScope.UCalculated <= 0.95f*inputData.UREMA) {
                        return@loop
                    }
                }
                //Отсеиваем по максимальному углу наклона зубьев
                if (creationData.gearWheelStepsArray[0].zuc1hScope.BET > inputData.BETMA ||
                    creationData.gearWheelStepsArray[1].zuc1hScope.BET > inputData.BETMA) {
                    return@loop
                }
            }
            //Внесение в ArrayList подходящего варианта, прошедшего все проверки в этой функции
            properCreationDataArrList.add(creationData)
            newMasreScopeList.add(masreScopeList[i])
        }
        properCreationDataArrList.trimToSize()
        newMasreScopeList.trimToSize()
        //Обновляем masreScopeList, чтобы он теперь соответствовал отсеянным данным
        masreScopeList = newMasreScopeList
        return properCreationDataArrList
    }

    /**
     * Для инициализации [CommonData]
     */
    private fun initializeCommonData(it: CreationData, masreScope: MasreScope): CommonData {
        return CommonData(
            U = numRound.roundThree(it.gearWheelStepsArray.asList().fold(1f) {
                    total, next -> total*next.zuc1hScope.UCalculated
            }),
            THighSpeedStep = numRound.roundThree(it.zcredScope.TVL1!!),
            TLowSpeedStep = if (inputData.ISTCol == 1) {
                numRound.roundThree(it.zcredScope.TVL2!!)
            } else {
                numRound.roundThree(it.zcredScope.TVL3!!)
            },
            NHighSpeedStep = numRound.roundThree(it.zcredScope.NV1!!),
            NLowSpeedStep = if (inputData.ISTCol == 1) {
                numRound.roundThree(it.zcredScope.NV2!!)
            } else {
                numRound.roundThree(it.zcredScope.NV3!!)
            },
            mechanismsMass = numRound.roundThree(masreScope.MARE!!),
            wheelsMass = numRound.roundThree(masreScope.MAKLS!!),
            degreeOfAccuracy = numRound.roundThree(it.gearWheelStepsArray.asList().minBy {
                it.zuc2hScope.ST
            }!!.zuc2hScope.ST.toFloat()).toInt()
        )
    }

    /**
     * Для инициализации [OneStepData]
     */
    private fun initializeOneStepData(it: CreationData, i: Int): OneStepData {
        return OneStepData(
            uStep = numRound.roundThree(it.gearWheelStepsArray[i].zuc1hScope.UCalculated),
            PSIBA = numRound.roundThree(it.option.PSB),
            AW = it.gearWheelStepsArray[i].zuc1hScope.AW.toInt(),
            ALF = numRound.roundThree((inputData.ALF*180)/ PI.toFloat()),
            BET = numRound.roundThree((it.gearWheelStepsArray[i].zuc1hScope.BET*180)/ PI.toFloat()),
            M = it.gearWheelStepsArray[i].dopnScope.M,
            FT = numRound.roundThree(it.gearWheelStepsArray[i].zuc2hScope.FT!!),
            FR = numRound.roundThree(it.gearWheelStepsArray[i].zuc2hScope.FR!!),
            FA = numRound.roundThree(it.gearWheelStepsArray[i].zuc2hScope.FA!!),
            SGH = it.gearWheelStepsArray[i].zuc2hScope.SGH!!.toInt(),
            SGHD = it.gearWheelStepsArray[i].dopnScope.SGHD!!,
            SGHM = it.gearWheelStepsArray[i].zuc2hScope.SGHM!!.toInt(),
            SGHMD = it.gearWheelStepsArray[i].dopnScope.SGHMD!!,
            Z = arrayListOf(it.gearWheelStepsArray[i].zuc1hScope.Z1!!,
                it.gearWheelStepsArray[i].zuc1hScope.Z2!!),
            X = arrayListOf(numRound.roundThree(it.gearWheelStepsArray[i].zuc1hScope.X1),
                numRound.roundThree(it.gearWheelStepsArray[i].zuc1hScope.X2)),
            D = arrayListOf(numRound.roundThree(it.gearWheelStepsArray[i].zucepScope.D[0]),
                numRound.roundThree(it.gearWheelStepsArray[i].zucepScope.D[1])),
            DW = arrayListOf(numRound.roundThree(it.gearWheelStepsArray[i].zuc2hScope.DW[0]),
                numRound.roundThree(it.gearWheelStepsArray[i].zuc2hScope.DW[1])),
            DA = arrayListOf(numRound.roundThree(it.gearWheelStepsArray[i].zucepScope.DA[0]),
                numRound.roundThree(it.gearWheelStepsArray[i].zucepScope.DA[1])),
            DF = arrayListOf(numRound.roundThree(it.gearWheelStepsArray[i].zucepScope.DF[0]),
                numRound.roundThree(it.gearWheelStepsArray[i].zucepScope.DF[1])),
            BW = arrayListOf(it.gearWheelStepsArray[i].zuc1hScope.BW1!!,
                it.gearWheelStepsArray[i].zuc1hScope.BW2!!),
            HRC = arrayListOf(it.option.HRC[0],
                it.option.HRC[1]),
            SGF = numRound.roundThree(it.gearWheelStepsArray[i].zucfScope.SGF.max()!!),
            SGFD = it.gearWheelStepsArray[i].dopnScope.wheelsSGFD.min()!!,
            SGFM = numRound.roundThree(it.gearWheelStepsArray[i].zucfScope.SGFM.max()!!),
            SGFMD = it.gearWheelStepsArray[i].dopnScope.wheelsSGFMD.min()!!
        )
    }

    /**
     * Для инициализации [SortingData]
     */
    private fun initializeSortingData(it: CreationData, masreScope: MasreScope): SortingData {
        return SortingData(
            reducerVolume = numRound.roundThree(sortingUtils.volume(masreScope)),
            sumAW = sortingUtils.sumAW(it).toInt(),
            minSumHRC = sortingUtils.minHRC(it),
            minDiffSG = (sortingUtils.diffSGH(it) + sortingUtils.diffSGF(it)),
            sortingIndex = it.sorting
        )
    }

    /**
     * Для инициализации [reducerDataList]
     */
    private fun initializeOutputDataList(): ArrayList<ReducerData> {
        val reducerDataList: ArrayList<ReducerData> = arrayListOf()
        properCreationData.forEachIndexed {index, it ->
            val commonData: CommonData = initializeCommonData(
                it = it,
                masreScope = masreScopeList[index]
                )
            val lowSpeedStepData: OneStepData = initializeOneStepData(
                it = it,
                i = 0
            )
            val sortingData: SortingData = initializeSortingData(
                it = it,
                masreScope = masreScopeList[index]
            )
            if (inputData.ISTCol == 1) {
                val reducerData: ReducerData = ReducerData(
                    commonData = commonData,
                    lowSpeedStepData = lowSpeedStepData,
                    sortingData = sortingData
                )
                reducerDataList.add(reducerData)
            }
            else {
                val highSpeedStepData: OneStepData = initializeOneStepData(
                    it = it,
                    i = 1
                )
                val reducerData: ReducerData = ReducerData(
                    commonData = commonData,
                    lowSpeedStepData = lowSpeedStepData,
                    highSpeedStepData = highSpeedStepData,
                    sortingData = sortingData
                )
                reducerDataList.add(reducerData)
            }
        }
        return reducerDataList
    }

    /**
     * Для инициализации [differentSortionsOfReducerData]
     */
    private fun initializeSortedLists(): HashMap<String, ArrayList<ReducerData>> {
        val differentSortionsOfReducerData: HashMap<String, ArrayList<ReducerData>> = hashMapOf()
        //Отсортированный по весу
        val byWeight: ArrayList<ReducerData> = ArrayList(reducerDataList.sortedBy {
            it.commonData.mechanismsMass
        })
        differentSortionsOfReducerData["weight"] = byWeight
        //Отсортированный по объёму редуктора
        val byVolume: ArrayList<ReducerData> = ArrayList(reducerDataList.sortedBy {
            it.sortingData.reducerVolume
        })
        differentSortionsOfReducerData["volume"] = byVolume
        //Отсортированный по сумме межосевых расстояний
        val bySumAW: ArrayList<ReducerData> = ArrayList(reducerDataList.sortedBy {
            it.sortingData.sumAW
        })
        differentSortionsOfReducerData["AW"] = bySumAW
        //Отсортированный по HRC
        val byMinSumHRC: ArrayList<ReducerData> = ArrayList(reducerDataList.sortedBy {
            it.sortingData.minSumHRC
        })
        differentSortionsOfReducerData["HRC"] = byMinSumHRC
        //Отсортированный по разнице (SGD - SG)
        val byMinDiffSG: ArrayList<ReducerData> = ArrayList(reducerDataList.sortedBy {
            it.sortingData.minDiffSG
        })
        differentSortionsOfReducerData["DifferenceSG"] = byMinDiffSG
        val byUDescending: ArrayList<ReducerData> = ArrayList(reducerDataList.sortedByDescending {
            it.commonData.U
        })
        differentSortionsOfReducerData["U"] = byUDescending
        return differentSortionsOfReducerData
    }
}

/**
 * Выдаётся как объект для фронта
 * [reducerDataList] - это лист со всеми подходящими по требованиям вариантами после отсевов и тд.
 * [SortingLists] - это набор листов с данными одного и того же листа [reducerDataList], но
 * отсортированные по разным критериям (так как проще их хранить и показывать пользователю сразу, а
 * не просить бэк каждый раз их рассчитывать, тем более памяти они будут занимать немного)
 * P.S. если ты будешь брать данные для построения, то я тебе рекомендую это делать из
 * отсортированного по массе списка ([SortingLists[weight]]), потому что он по моему мнению
 * предоставляет самый адекватный порядок предоставления результатов
 */
data class OutputData(
    val reducerDataList: ArrayList<ReducerData>,
    val SortingLists: HashMap<String, ArrayList<ReducerData>>
)

/**
 * Через [ReducerData.toString] можно отправлять данные как на показ пользователю, так
 * и в текстовый файл для последующей печати
 * [commonData] - общие характеристики редуктора, присущие ему как целому, а не отдельным ступеням
 * [lowSpeedStepData] - характеристики тихоходной ступени
 * [highSpeedStepData] - характеристики быстроходной ступени
 */
data class ReducerData(
    val commonData: CommonData,
    val lowSpeedStepData: OneStepData,
    val highSpeedStepData: OneStepData? = null,
    val sortingData: SortingData
) {
    override fun toString(): String {
        return """
                                        Результаты расчёта
                                     Характеристика механизма
            $commonData
                                Характеристика тихоходной ступени
                                
            $lowSpeedStepData
            
                                Характеристика быстроходной ступени
            $highSpeedStepData
        """.trimIndent()
    }
}

/**
 * [U] - передаточное отношение механизма
 * [THighSpeedStep] - вращающий момент на быстроходном валу, Н*м
 * [TLowSpeedStep] - вращающий момент на тихоходном валу, Н*м
 * [NHighSpeedStep] - частота вращения быстроходного вала, об/мин
 * [NLowSpeedStep] - частота вращения тихоходного вала, об/мин
 * [mechanismsMass] - масса механизма, кг
 * [wheelsMass] - масса колёс, кг
 * [degreeOfAccuracy] - степень точности
 */
data class CommonData(
    val U: Float,
    val THighSpeedStep: Float,
    val TLowSpeedStep: Float,
    val NHighSpeedStep: Float,
    val NLowSpeedStep: Float,
    val mechanismsMass: Float,
    val wheelsMass: Float,
    val degreeOfAccuracy: Int
) {
    override fun toString(): String {
        return """
            Передаточное отношение механизма                            $U
            Вращающий момент на быстроходном валу, Н*м                  $THighSpeedStep
                                тихоходном валу, Н*м                    $TLowSpeedStep
            Частота вращения быстроходного валу, об/мин                 $NHighSpeedStep
                                тихоходного вала, об/мин                $NLowSpeedStep
            Масса механизма, кг                                         $mechanismsMass
                  колёс, кг                                             $wheelsMass
            Степень точности                                            $degreeOfAccuracy
        """.trimIndent()
    }
}

/**
 * [uStep] - передаточное число ступени
 * [PSIBA] - коэффициент ширины венца
 * [AW] - межосевое расстояние, мм
 * [ALF] - угол зацепления, градусы
 * [BET] - угол наклона зубьев, градусы
 * [M] - модуль зацепления (нормальный), мм
 * Силы в зацеплении, Н
 * [FT] - окружная
 * [FR] - радиальная
 * [FA] - осевая
 * Контактные напряжения, МПа
 * при номинальной нагрузке:
 * [SGH] - расчётные
 * [SGHD] - допускаемые
 * при максимальной нагрузке:
 * [SGHM] - расчётные
 * [SGHMD] - допускаемые
 * Параметры зубчатых колёс (0 - шестерня, 1 - колесо)
 * [Z] - число зубьев
 * [X] - коэффициент смещения исходного контура
 * Диаметры, мм
 * [D] - делительный
 * [DW] - начальный
 * [DA] - вершин
 * [DF] - впадин
 *
 * [BW] - ширина зубчатого венца
 * [HRC] - твёрдость поверхности зубьев, HRC
 * Напряжения изгиба, МПа
 * при номинальной нагрузке:
 * [SGF] - расчётные
 * [SGFD] - допускаемые
 * при максимальной нагрузке:
 * [SGFM] - расчётные
 * [SGFMD] - допускаемые
 */
data class OneStepData(
    val uStep: Float,
    val PSIBA: Float,
    val AW: Int,
    val ALF: Float,
    val BET: Float,
    val M: Float,
    val FT: Float,
    val FR: Float,
    val FA: Float,
    val SGH: Int,
    val SGHD: Int,
    val SGHM: Int,
    val SGHMD: Int,
    val Z: ArrayList<Int>,
    val X: ArrayList<Float>,
    val D: ArrayList<Float>,
    val DW: ArrayList<Float>,
    val DA: ArrayList<Float>,
    val DF: ArrayList<Float>,
    val BW: ArrayList<Int>,
    val HRC: ArrayList<Float>,
    val SGF: Float,
    val SGFD: Int,
    val SGFM: Float,
    val SGFMD: Int
) {
    override fun toString(): String {
        return """
            Передаточное число                                          $uStep
            Коэффициент ширины венца                                    $PSIBA
            Межосевое расстояние, мм                                    $AW
            Угол зацепления, градусы                                    $ALF
            Угол наклона зубьев, градусы                                $BET
            Модуль зацепления (нормальный), мм                          $M
            Силы в зацеплении, Н:
                окружная                                                $FT
                радиальная                                              $FR
                осевая                                                  $FA
            Контактные напряжения, МПа:
                при номинальной нагрузке:
                    расчётные                                           $SGH
                    допускаемые                                         $SGHD
                при максимальной нагрузке:
                    расчётные                                           $SGHM
                    допускаемые                                         $SGHMD
                            Параметры зубчатых колёс ступени    Шестерня        Колесо
            Число зубьев                                        ${Z[0]}         ${Z[1]}
            Коэффициент смещения исходного контура              ${X[0]}         ${X[1]}
            Диаметры, мм:
                Делительный                                     ${D[0]}         ${D[1]}
                Начальный                                       ${DW[0]}        ${DW[1]}
                Вершин                                          ${DA[0]}        ${DA[1]}
                Впадин                                          ${DF[0]}        ${DF[1]}
            Ширина зубчатого венца                              ${BW[0]}        ${BW[1]}
            Твёрдость поверхности зубьев, HRC                   ${HRC[0]}       ${HRC[1]}
            Напряжения изгиба, МПа:
                при номинальной нагрузке:
                    расчётные                                           $SGF
                    допускаемые                                         $SGFD
                при максимальной нагрузке:
                    расчётные                                           $SGFM
                    допускаемые                                         $SGFMD    
        """.trimIndent()
    }
}

/**
 * [reducerVolume] - объём редуктора (коробки)
 * [sumAW] - сумма межосевых расстояний ступеней
 * [minSumHRC] - сумма HRC (на самом деле SGD, но это не играет никакой роли, потому что они
 * взаимозаменяемые в плане сортировки)
 * [minDiffSG] - сумма разниц SGHD - SGH и SGFD - SGF
 * [sortingIndex] - это индекс, который чем меньше, чем лучше итоговый результат, он учитывает
 * много параметров. Но для его корректной работы нужно ещё подобрать весовые коэффициенты
 */
data class SortingData(
    val reducerVolume: Float,
    val sumAW: Int,
    val minSumHRC: Float,
    val minDiffSG: Float,
    val sortingIndex: Float
)