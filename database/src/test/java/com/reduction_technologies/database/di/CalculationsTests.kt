package com.reduction_technologies.database.di

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.reducetechnologies.calculations.*
import com.reducetechnologies.calculations_entity.MasreMethod
import com.reducetechnologies.calculations_entity.MasreScope
import com.reducetechnologies.di.CalculationsComponent
import com.reducetechnologies.specificationsAndRequests.Specifications
import com.reducetechnologies.tables_utils.table_contracts.FatigueTable
import com.reducetechnologies.tables_utils.table_contracts.SGTTTable
import com.reducetechnologies.tables_utils.table_contracts.source_datatable.SourceDataTable
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

/**
 * Собственно тесты можно пилить здесь.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest= Config.NONE)
internal class CalculationsModuleTest {
    lateinit var databaseComponent: DatabaseComponent
    lateinit var calculationComponent : CalculationsComponent
    lateinit var input: InputData
    lateinit var inputOneStaged: InputData
    //Methods
    lateinit var edMethod: EDMethodsClass
    lateinit var allReducersMethod: AllReducersOptionsClass
    lateinit var DOPNMethod: DOPN_MethodsClass
    lateinit var ZUC1HMethod: ZUC1HMethodsClass
    lateinit var ZUCEPMethod: ZUCEPMethodsClass
    lateinit var ZUC2HMethod: ZUC2HMethodsClass
    lateinit var ZUCFMethod: ZUCFMethodsClass
    lateinit var ZUCMethod: ZUCMethodsClass
    lateinit var ZCREDMethod: ZCREDMethodsClass
    lateinit var masreMethod: MasreMethod
    //Tables
    lateinit var sourceTable: SourceDataTable
    lateinit var fatigueTable: FatigueTable
    lateinit var SGTTTable: SGTTTable
    //Scopes
    val edScope = EDScope()
    val dopnScope = DOPNScope()
    val zuc1hScope = ZUC1HScope()
    val zucepScope = ZUCEPScope()
    val zuc2hScope = ZUC2HScope()
    val zucfScope = ZUCFScope()

    /**
     * Нужно для правильной работы внутренних слоев работы с бд
     */
    @get:Rule
    val rule = InstantTaskExecutorRule()//если эта дичь не видит её, нужно синхронизировать грэдл

    /**
     * [isAccurate] - просто проверяет, укладывается ли расхождение с учебником в 5%
     */
    fun isAccurate(num1: Float, num2: Float) : Boolean = (((abs(num1 - num2))/num2) < 0.05f)

    fun setInput(
        isED: Boolean,
        TT: Float,
        NT: Float,
        LH: Int,
        NRR: Int,
        KOL: Int,
        UREMA: Float,
        ind: Int,
        isChevrone: Boolean,
        isInner: Boolean,
        KPD: Float = 0.97f,
        ISTCol: Int,
        PAR: Boolean
    ) : InputData {
        if (ind > 13 || ind < 0) {
            IllegalArgumentException("Index $ind was less than 0 or more than 13")
        }
        if (ISTCol > 2 || ISTCol < 1) {
            IllegalArgumentException("IstCol $ISTCol was less than 1 or more than 2")
        }
        val wheelSubtype: Array<Specifications.WheelSubtype> =
            if (ind == 0 || ind == 4 || ind == 13) {
                arrayOf(Specifications.WheelSubtype.SPUR, Specifications.WheelSubtype.SPUR)
            } else if (ind == 1 || ind == 5 || ind == 6 || ind == 9 || ind == 10 || ind == 11 || ind == 12) {
                arrayOf(Specifications.WheelSubtype.HELICAL,
                    Specifications.WheelSubtype.HELICAL)
            } else {
                arrayOf(Specifications.WheelSubtype.CHEVRON,
                    Specifications.WheelSubtype.CHEVRON)
            }
        val isInnerLastTwo =
            ind == 12 || ind == 13
        return InputData(
            isED = isED,
            TT = TT,
            NT = NT,
            LH = LH,
            NRR = NRR,
            KOL = KOL,
            UREMA = UREMA,
            TIPRE = sourceTable.tipreRow.list[ind],
            NP = sourceTable.npRow.list[ind],
            BETMI = (sourceTable.betMiRow.list[ind]*PI.toFloat()/180f),
            BETMA = (sourceTable.betMaRow.list[ind]*PI.toFloat()/180f),
            OMEG = sourceTable.omegRow.list[ind],
            NW = sourceTable.nwRow.list[ind],
            NZAC = arrayOf(sourceTable.nzaC1Row.list[ind], sourceTable.nzaC2Row.list[ind]),
            NWR = sourceTable.nwrRow.list[ind].getNWR(isChevrone = isChevrone),
            BKAN = sourceTable.bkanRow.list[ind],
            SIGN = arrayOf(
                sourceTable.signRow.list[ind].getSign(isInnerLastTwo),
                sourceTable.signRow.list[ind].getSign(isInner)),
            CONSOL = arrayOf(sourceTable.consolRow.list[ind], sourceTable.consolRow.list[ind]),
            KPD = KPD,
            ISTCol = ISTCol,
            wheelType = Specifications.WheelType.CYLINDRICAL,
            wheelSubtype = wheelSubtype,
            PAR = PAR
        )
    }

    fun testScheme(
        isED: Boolean,
        TT: Float,
        NT: Float,
        LH: Int,
        NRR: Int,
        KOL: Int,
        UREMA: Float,
        ind: Int,
        isChevrone: Boolean,
        isInner: Boolean,
        KPD: Float = 0.97f,
        ISTCol: Int,
        PAR: Boolean
    ) {
        val inputO = setInput(
            isED = isED,
            TT = TT,
            NT = NT,
            LH = LH,
            NRR = NRR,
            KOL = KOL,
            UREMA = UREMA,
            ind = ind,
            isChevrone = isChevrone,
            isInner = isInner,
            ISTCol = ISTCol,
            PAR = PAR
        )
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputO)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputO,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
    }

    fun weedOutCreationDataList(inputData: InputData, creationDataList: List<CreationData>, masreScopeList: ArrayList<MasreScope>): ArrayList<CreationData> {
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
            }
            //Внесение в ArrayList подходящего варианта, прошедшего все проверки в этой функции
            properCreationDataArrList.add(creationData)
            newMasreScopeList.add(masreScopeList[i])
        }
        properCreationDataArrList.trimToSize()
        newMasreScopeList.trimToSize()
        newMasreScopeList.forEachIndexed { index, newMasreScope ->
            println(index)
            println(newMasreScope)
        }
        return properCreationDataArrList
    }

    fun weedOutMasreDataList(inputData: InputData, creationDataList: List<CreationData>, masreScopeList: ArrayList<MasreScope>): ArrayList<MasreScope> {
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
            }
            //Внесение в ArrayList подходящего варианта, прошедшего все проверки в этой функции
            properCreationDataArrList.add(creationData)
            newMasreScopeList.add(masreScopeList[i])
        }
        properCreationDataArrList.trimToSize()
        newMasreScopeList.trimToSize()
        /*newMasreScopeList.forEachIndexed { index, newMasreScope ->
            println(index)
            println(newMasreScope)
        }*/
        return newMasreScopeList
    }

    fun diffSGH(inputData: InputData, creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHD.min()!! -
                    creationData.gearWheelStepsArray[0].zuc2hScope.SGH!!
        }
        else {
            return (creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHD.min()!! -
                    creationData.gearWheelStepsArray[0].zuc2hScope.SGH!!) +
                    (creationData.gearWheelStepsArray[1].dopnScope.wheelsSGHD.min()!! -
                    creationData.gearWheelStepsArray[1].zuc2hScope.SGH!!)
        }
    }

    fun diffSGHM(inputData: InputData, creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHMD.min()!! -
                    creationData.gearWheelStepsArray[0].zuc2hScope.SGHM!!
        }
        else {
            return (creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHMD.min()!! -
                    creationData.gearWheelStepsArray[0].zuc2hScope.SGHM!!) +
                    (creationData.gearWheelStepsArray[1].dopnScope.wheelsSGHMD.min()!! -
                            creationData.gearWheelStepsArray[1].zuc2hScope.SGHM!!)
        }
    }

    fun diffArr(arrayBig: Array<Int>, arraySmall: Array<Float>): Float {
        return (arrayBig.min()!! - arraySmall.max()!!)
    }

    fun diffSGF(inputData: InputData, creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return diffArr(creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFD,
                creationData.gearWheelStepsArray[0].zucfScope.SGF)
        }
        else {
            return diffArr(creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFD,
                creationData.gearWheelStepsArray[0].zucfScope.SGF) +
                    diffArr(creationData.gearWheelStepsArray[1].dopnScope.wheelsSGFD,
                        creationData.gearWheelStepsArray[1].zucfScope.SGF)
        }
    }

    fun diffSGFM(inputData: InputData, creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return diffArr(creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFMD,
                creationData.gearWheelStepsArray[0].zucfScope.SGFM)
        }
        else {
            return diffArr(creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFMD,
                creationData.gearWheelStepsArray[0].zucfScope.SGFM) +
                    diffArr(creationData.gearWheelStepsArray[1].dopnScope.wheelsSGFMD,
                        creationData.gearWheelStepsArray[1].zucfScope.SGFM)
        }
    }

    fun diffUCalc(inputData: InputData, creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return inputData.UREMA - creationData.gearWheelStepsArray[0].zuc1hScope.UCalculated
        }
        else {
            return inputData.UREMA - (creationData.gearWheelStepsArray[0].zuc1hScope.UCalculated *
                    creationData.gearWheelStepsArray[1].zuc1hScope.UCalculated)
        }
    }

    fun minHRC(inputData: InputData, creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return (creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHD.max()!! +
                    creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFD.max()!!).toFloat()
        }
        else {
            return (creationData.gearWheelStepsArray[0].dopnScope.wheelsSGHD.max()!! +
                    creationData.gearWheelStepsArray[0].dopnScope.wheelsSGFD.max()!! +
                    creationData.gearWheelStepsArray[1].dopnScope.wheelsSGHD.max()!! +
                    creationData.gearWheelStepsArray[1].dopnScope.wheelsSGFD.max()!!).toFloat()
        }
    }

    fun minAW(inputData: InputData, creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return creationData.gearWheelStepsArray[0].zuc1hScope.AW
        }
        else {
            return creationData.gearWheelStepsArray[0].zuc1hScope.AW +
                    creationData.gearWheelStepsArray[1].zuc1hScope.AW
        }
    }

    fun Ep(inputData: InputData, creationData: CreationData): Float {
        if (inputData.ISTCol == 1) {
            return creationData.gearWheelStepsArray[0].zucepScope.EPALF!! +
                    creationData.gearWheelStepsArray[0].zuc2hScope.EPBET!!
        }
        else {
            return creationData.gearWheelStepsArray[0].zucepScope.EPALF!! +
                    creationData.gearWheelStepsArray[0].zuc2hScope.EPBET!! +
                    creationData.gearWheelStepsArray[1].zucepScope.EPALF!! +
                    creationData.gearWheelStepsArray[1].zuc2hScope.EPBET!!
        }
    }

    fun minSize(masreScope: MasreScope): Float {
        return masreScope.BRE!! + masreScope.HRE!! + masreScope.LRE!!
    }

    fun weedOutAndSorting(inputO: InputData, creationDataList: List<CreationData>) {
        masreMethod = MasreMethod(inputO, creationDataList)
        var masreScope: ArrayList<MasreScope> = masreMethod.enterLoopMasre()
        masreScope.forEachIndexed { index, masreScope ->
            println(index)
            println(masreScope)
        }
        //С двуступенчатыми вроде работает корректно
        //Производим отсев вариантов
        val properCreationDataArrList: ArrayList<CreationData> = weedOutCreationDataList(inputO, creationDataList, masreScope)
        properCreationDataArrList.forEachIndexed { index, creationData ->
            println(index)
            creationData.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(creationData.zcredScope.toString())
        }
        val newMasreDataList: ArrayList<MasreScope> = weedOutMasreDataList(inputO, creationDataList, masreScope)
        properCreationDataArrList.forEachIndexed { index, creationData ->
            val ind: Float = (diffSGH(inputO, creationData).pow(2) + diffSGHM(inputO, creationData).pow(2) +
                    diffSGF(inputO, creationData).pow(2) + diffSGFM(inputO, creationData).pow(2) +
                    diffUCalc(inputO, creationData).pow(2) + minHRC(inputO, creationData).pow(2) +
                    minAW(inputO, creationData).pow(2) - Ep(inputO, creationData).pow(2) +
                    minSize(masreScope = newMasreDataList[index]).pow(2) + newMasreDataList[index].MARE!!.pow(2)
                    ).pow(1/2f)
            properCreationDataArrList[index].sorting = ind
            properCreationDataArrList[index].sortingMass = newMasreDataList[index].MARE!!
            newMasreDataList[index].sorting = ind
        }
        newMasreDataList.forEachIndexed { index, masreScope ->
            println(index)
            println(masreScope.sorting)
        }
        properCreationDataArrList.sortBy { it.sorting }
        newMasreDataList.sortBy { it.sorting }
        println("Вот отсюда начинаются сортированные списки: ")
        properCreationDataArrList.forEachIndexed { index, creationData ->
            println(index)
            creationData.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(creationData.zcredScope.toString())
            println(newMasreDataList[index].toString())
        }
        //А вот здесь пробуем сортировку по массе
        println("Отсюда начинаются отсортированные по массе: ")
        properCreationDataArrList.sortBy { it.sortingMass }
        newMasreDataList.sortBy { it.MARE }
        properCreationDataArrList.forEachIndexed { index, creationData ->
            println(index)
            creationData.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(creationData.zcredScope.toString())
            println(newMasreDataList[index].toString())
        }
    }

    @Before
    fun setUp() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        // Сначала в любом случае сначала получаем databaseComponent - он имеет методы для получения calculationModule и билдера calculationComponent
        databaseComponent = DaggerDatabaseComponent.builder()
            .databaseModule(DatabaseModule(context))
            .build()
        // псведо-асинхронно получаем calculationModule
        val calculationModule = runBlocking { databaseComponent.calculationModule().await() }

        // и наконец, через билдер билдим calculationComponent, кладя в него ранее полученный calculationModule
        calculationComponent =
            databaseComponent.calculationsBuilder()
                .get()
                .calculationModule(calculationModule).build()

        //Tables late initialization
        sourceTable = calculationComponent.getSourceTable()
        fatigueTable = calculationComponent.getFatigueTable()
        SGTTTable = calculationComponent.getSGTTTable()
        //Methods late initialization
        edMethod = calculationComponent.getEDMethods()
        allReducersMethod = calculationComponent.getAllReducersOptions()
        DOPNMethod = calculationComponent.getDOPNMethods()
        ZUC1HMethod = calculationComponent.getZUC1HMethods()
        ZUCEPMethod = calculationComponent.getZUCEPMethods()
        ZUC2HMethod = calculationComponent.getZUC2HMethods()
        ZUCFMethod = calculationComponent.getZUCFMethods()
        ZUCMethod = calculationComponent.getZUCMethods()
        ZCREDMethod = calculationComponent.getZCREDMethods()
        /*ZUCMethod = ZUCMethodsClass(
            dopnMethods = DOPNMethod,
            zuc1hMethods = ZUC1HMethod,
            zucepMethods = ZUCEPMethod,
            zucfMethdos = ZUCFMethod,
            zuc2hMethods = ZUC2HMethod
        )*/
        //Подумать, сажать ли это на даггер или нет, потому что даггер по идее не должен знать о расчётах
        //Основной инпут, который отражает условия Макса
        /*input = InputData(
            isED = false,
            TT = 590f,
            NT = 36f,
            LH = 10000,
            NRR = 2,
            UREMA = 26.4f,
            TIPRE = sourceTable.tipreRow.list[4],
            NP = sourceTable.npRow.list[4],
            BETMI = sourceTable.betMiRow.list[4],
            BETMA = sourceTable.betMaRow.list[4],
            OMEG = sourceTable.omegRow.list[4],
            NW = sourceTable.nwRow.list[4],
            NZAC = arrayOf(sourceTable.nzaC1Row.list[4], sourceTable.nzaC2Row.list[4]),
            NWR = sourceTable.nwrRow.list[4].getNWR(isChevrone = false),
            BKAN = sourceTable.bkanRow.list[4],
            SIGN = arrayOf(sourceTable.signRow.list[4].getSign(false),
                sourceTable.signRow.list[4].getSign(false)),
            CONSOL = arrayOf(sourceTable.consolRow.list[4], sourceTable.consolRow.list[4]),
            KPD = 0.98f,
            ISTCol = 2,
            wheelType = Specifications.WheelType.CYLINDRICAL,
            wheelSubtype = arrayOf(Specifications.WheelSubtype.SPUR,
                Specifications.WheelSubtype.SPUR),
            PAR = false
        )*/
        //Тип 5 (нуммерация с 0)
        input = InputData(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            TIPRE = sourceTable.tipreRow.list[5],
            NP = sourceTable.npRow.list[5],
            BETMI = (sourceTable.betMiRow.list[5]*PI.toFloat()/180f),
            BETMA = (sourceTable.betMaRow.list[5]*PI.toFloat()/180f),
            OMEG = sourceTable.omegRow.list[5],
            NW = sourceTable.nwRow.list[5],
            NZAC = arrayOf(sourceTable.nzaC1Row.list[5], sourceTable.nzaC2Row.list[5]),
            NWR = sourceTable.nwrRow.list[5].getNWR(isChevrone = false),
            BKAN = sourceTable.bkanRow.list[5],
            SIGN = arrayOf(sourceTable.signRow.list[5].getSign(false),
                sourceTable.signRow.list[5].getSign(false)),
            CONSOL = arrayOf(sourceTable.consolRow.list[5], sourceTable.consolRow.list[5]),
            KPD = 0.9f,
            ISTCol = 2,
            wheelType = Specifications.WheelType.CYLINDRICAL,
            wheelSubtype = arrayOf(Specifications.WheelSubtype.HELICAL,
                Specifications.WheelSubtype.HELICAL),
            PAR = false
        )
        //тип 1
        inputOneStaged = InputData(
            isED = false,
            TT = 510f,
            NT = 303f,
            LH = 32000,
            NRR = 3,
            KOL = 10000,
            UREMA = 4.8f,
            TIPRE = sourceTable.tipreRow.list[1],
            NP = sourceTable.npRow.list[1],
            BETMI = (sourceTable.betMiRow.list[1]*PI.toFloat()/180f),
            BETMA = (sourceTable.betMaRow.list[1]*PI.toFloat()/180f),
            OMEG = sourceTable.omegRow.list[1],
            NW = sourceTable.nwRow.list[1],
            NZAC = arrayOf(sourceTable.nzaC1Row.list[1], sourceTable.nzaC2Row.list[1]),
            NWR = sourceTable.nwrRow.list[1].getNWR(isChevrone = false),
            BKAN = sourceTable.bkanRow.list[1],
            SIGN = arrayOf(sourceTable.signRow.list[1].getSign(false),
                sourceTable.signRow.list[1].getSign(false)),
            CONSOL = arrayOf(sourceTable.consolRow.list[1], sourceTable.consolRow.list[1]),
            KPD = 0.97f,
            ISTCol = 1,
            wheelType = Specifications.WheelType.CYLINDRICAL,
            wheelSubtype = arrayOf(Specifications.WheelSubtype.HELICAL,
                Specifications.WheelSubtype.HELICAL),
            PAR = false
        )
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun get_source_via_calculation_component() {
        val table = calculationComponent.getSourceTable()
        Assertions.assertNotNull(table.betMaRow.list[0])
        println(table.betMaRow.list[0])

        Assertions.assertNotNull(table)
    }

    @org.junit.Test
    fun test_edMethods(){
        val edArguments = EDMethodsClass.Arguments(PEDCalculated = 14f, NEDFixed = 0)
        edMethod.EDCalculate(args = edArguments,
            edScope = edScope)
        /*println(edScope.PED)
        println(edScope.NED)
        println(edScope.TTED)
        println(edScope.D1ED)
        println(edScope.L1ED)
        println(edScope.H1ED)
        println(edScope.MAED)*/
        Assertions.assertNotNull(edScope.PED)
        Assertions.assertNotNull(edScope.NED)
        Assertions.assertNotNull(edScope.TTED)
        Assertions.assertNotNull(edScope.D1ED)
        Assertions.assertNotNull(edScope.L1ED)
        Assertions.assertNotNull(edScope.H1ED)
        Assertions.assertNotNull(edScope.MAED)
    }

    @org.junit.Test
    fun test_all_reducers_methods(){
        val reducersOptions = allReducersMethod.tryToCalculateOptions(inputData = input)
        for ((index, option) in reducersOptions.withIndex()){
            println("$index . " + option.u)
        }
    }

    @org.junit.Test
    fun test_dopn_methods(){
        val reducersOptions = allReducersMethod.tryToCalculateOptions(inputData = input)
        val dopnArguments = DOPN_MethodsClass.Arguments(N2 = 190f,
            u = 7.04f,
            inputData = input,
            option = reducersOptions[0])
        DOPNMethod.dopn(args = dopnArguments, dopnScope = dopnScope)
        //Данные не зависят от u
        //Зависят от N
        //Ну это и понятно, там по сути только от всяких кэфов зависит и HRC, которые здесь очень
        //близко прописаны, поэтому ничего плохого быть не должно
        /*println(dopnScope.M)
        println(dopnScope.V)
        println(dopnScope.SGHD)//Близко, 488 здесь и 505 там, расхождение 3 процента, не критично
        println(dopnScope.SGHMD)//Абсолютно совпало, идеально
        println("${dopnScope.wheelsKFC[0]} and ${dopnScope.wheelsKFC[1]}")//Они просто не изменяются, их логика должна быть прописана как выбор из табл от дано
        println("${dopnScope.wheelsSGHD[0]} and ${dopnScope.wheelsSGHD[1]}")//Вроде правдоподобно
        println("${dopnScope.wheelsSGHMD[0]} and ${dopnScope.wheelsSGHMD[1]}")//Правдоподобно, вторая даже выдаёт 1652, что как в учебнике
        println("${dopnScope.wheelsSGFD[0]} and ${dopnScope.wheelsSGFD[1]}")//Здесь есть меньше 5 процентов расхождение, но это скорее данные
        println("${dopnScope.wheelsSGFMD[0]} and ${dopnScope.wheelsSGFMD[1]}")//Лол, с учебником расхождения в обоих на 1: 780 и 781 там, 679 и 680 там*/
        Assertions.assertTrue(isAccurate(dopnScope.SGHD!!.toFloat(), 505f))
        Assertions.assertTrue(isAccurate(dopnScope.SGHMD!!.toFloat(), 1652f))
        Assertions.assertTrue(isAccurate(dopnScope.wheelsSGFD[0].toFloat(), 244f))
        Assertions.assertTrue(isAccurate(dopnScope.wheelsSGFD[1].toFloat(), 213f))
        Assertions.assertTrue(isAccurate(dopnScope.wheelsSGFMD[0].toFloat(), 781f))
        Assertions.assertTrue(isAccurate(dopnScope.wheelsSGFMD[1].toFloat(), 680f))
        //Проходит
    }

    @org.junit.Test
    fun test_zuc1h_methods(){
        //Создание списка вариантов редукторов
        val reducersOptions = allReducersMethod.tryToCalculateOptions(inputData = input)
        //Для целей тестов выбрали 1 опцию
        val option = reducersOptions[0]
        println(option.u)
        //DOPN
        val dopnArguments = DOPN_MethodsClass.Arguments(N2 = input.NT,
            u = option.u,
            inputData = input,
            option = option)
        DOPNMethod.dopn(args = dopnArguments, dopnScope = dopnScope)
        //ZUC1H
        val zuc1hArguments = ZUC1HMethodsClass.Arguments(
            SIGN = input.SIGN[0],
            u = option.u,
            T2 = input.TT,
            dopnScope = dopnScope,
            option = option,
            inputData = input,
            IST = 0
        )
        ZUC1HMethod.enterZUC1H(args = zuc1hArguments, zuc1HScope = zuc1hScope)
        var toPrint = zuc1hScope.toString()
        println(toPrint)
        //ZUCEP
        val zucepArguments = ZUCEPMethodsClass.Arguments(
            SIGN = input.SIGN[0],
            N2 = input.NT,
            dopnScope = dopnScope,
            zuc1HScope = zuc1hScope,
            inputData = input
        )
        ZUCEPMethod.enterZUCEP(args = zucepArguments, zucepScope = zucepScope)
        toPrint = zucepScope.toString()
        println(toPrint)
        //ZUC2H
        val zuc2hArguments = ZUC2HMethodsClass.Arguments(
            SIGN = input.SIGN[0],
            edScope = edScope,
            option = option,
            zuc1hScope = zuc1hScope,
            zucepScope = zucepScope,
            dopnScope = dopnScope,
            inputData = input,
            N2 = input.NT,
            T2 = if (zuc1hScope.T2 != null) zuc1hScope.T2!! else input.TT,
            CONSOL = input.CONSOL[0]
        )
        ZUC2HMethod.enterZUC2H(args = zuc2hArguments, zuc2hScope = zuc2hScope)
        toPrint = zuc2hScope.toString()
        println(toPrint)
        toPrint = zuc1hScope.toString()
        println(toPrint)
        //ZUCF
        val zucfArguments = ZUCFMethodsClass.Arguments(
            SIGN = input.SIGN[0],
            inputData = input,
            dopnScope = dopnScope,
            edScope = edScope,
            option = option,
            zuc1hScope = zuc1hScope,
            zuc2hScope = zuc2hScope,
            zucepScope = zucepScope,
            CONSOL = input.CONSOL[0]
        )
        ZUCFMethod.enterZUCF(args = zucfArguments, zucfScope = zucfScope)
        toPrint = zucfScope.toString()
        println(toPrint)
        toPrint = dopnScope.toString()
        println(toPrint)
    }

    @org.junit.Test
    fun test_zuc_method() {
        //Создание списка вариантов редукторов
        val reducersOptions = allReducersMethod.tryToCalculateOptions(inputData = input)
        //Для целей тестов выбрали 1 опцию
        val option = reducersOptions[0]
        //Arguments for all methods
        val dopnArguments = DOPN_MethodsClass.Arguments(N2 = input.NT,
            u = option.u,
            inputData = input,
            option = option)
        val zuc1hArguments = ZUC1HMethodsClass.Arguments(
            SIGN = input.SIGN[0],
            u = option.u,
            T2 = input.TT,
            dopnScope = dopnScope,
            option = option,
            inputData = input,
            IST = 0
        )
        val zucepArguments = ZUCEPMethodsClass.Arguments(
            SIGN = input.SIGN[0],
            N2 = input.NT,
            dopnScope = dopnScope,
            zuc1HScope = zuc1hScope,
            inputData = input
        )
        val zuc2hArguments = ZUC2HMethodsClass.Arguments(
            SIGN = input.SIGN[0],
            edScope = edScope,
            option = option,
            zuc1hScope = zuc1hScope,
            zucepScope = zucepScope,
            dopnScope = dopnScope,
            inputData = input,
            N2 = input.NT,
            T2 = if (zuc1hScope.T2 != null) zuc1hScope.T2!! else input.TT,
            CONSOL = input.CONSOL[0]
        )
        val zucfArguments = ZUCFMethodsClass.Arguments(
            SIGN = input.SIGN[0],
            inputData = input,
            dopnScope = dopnScope,
            edScope = edScope,
            option = option,
            zuc1hScope = zuc1hScope,
            zuc2hScope = zuc2hScope,
            zucepScope = zucepScope,
            CONSOL = input.CONSOL[0]
        )
        val zucArguments = ZUCMethodsClass.Arguments(
            SIGN = input.SIGN[0],
            IST = 0,
            N2 = input.NT,
            T2 = 120.3f,
            uStup = option.u,
            inputData = input,
            dopnScope = dopnScope,
            zuc1hScope = zuc1hScope,
            zucepScope = zucepScope,
            zuc2hScope = zuc2hScope,
            zucfScope = zucfScope,
            option = option,
            edScope = edScope
        )
        ZUCMethod.enterZUC(args = zucArguments)
        println("${option.HRC[0]} and ${option.HRC[1]}")
        var toPrint = dopnScope.toString()
        println(toPrint)
        toPrint = zuc1hScope.toString()
        println(toPrint)
        toPrint = zucepScope.toString()
        println(toPrint)
        toPrint = zuc2hScope.toString()
        println(toPrint)
        toPrint = zucfScope.toString()
        println(toPrint)
    }

    @org.junit.Test
    fun test_zcred_method(){
        val reducerOptions = allReducersMethod.tryToCalculateOptions(input)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = input,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        // More or less correctly
    }

    @org.junit.Test
    fun test_zcred_method_one_staged(){
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputOneStaged)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputOneStaged,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        //More or less correctly
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_zero_scheme(){
        val inputO = setInput(
            isED = false,
            TT = 510f,
            NT = 303f,
            LH = 32000,
            NRR = 3,
            KOL = 10000,
            UREMA = 4.8f,
            ind = 0,
            isChevrone = false,
            isInner = false,
            ISTCol = 1,
            PAR = false
        )
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputO)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputO,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        //More or less correctly
        weedOutAndSorting(inputO, creationDataList)
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_second_scheme(){
        val inputO = setInput(
            isED = false,
            TT = 510f,
            NT = 303f,
            LH = 32000,
            NRR = 3,
            KOL = 10000,
            UREMA = 4.8f,
            ind = 2,
            isChevrone = true,
            isInner = false,
            ISTCol = 1,
            PAR = false
        )
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputO)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputO,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        masreMethod = MasreMethod(inputO, creationDataList)
        var masreScope: ArrayList<MasreScope> = masreMethod.enterLoopMasre()
        masreScope.forEachIndexed { index, masreScope ->
            println(index)
            println(masreScope)
        }
        //More or less correctly
        //1. Почему всегда принимает наименьший угол в шевронной передаче?
        //2. Почему такой большой коэффициент осевого перекрытия?
        val properCreationDataArrList: ArrayList<CreationData> = weedOutCreationDataList(inputO, creationDataList, masreScope)
        properCreationDataArrList.forEachIndexed { index, creationData ->
            println(index)
            creationData.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(creationData.zcredScope.toString())
        }
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_third_scheme(){
        val inputO = setInput(
            isED = false,
            TT = 510f,
            NT = 303f,
            LH = 32000,
            NRR = 3,
            KOL = 10000,
            UREMA = 4.8f,
            ind = 3,
            isChevrone = true,
            isInner = false,
            ISTCol = 1,
            PAR = false
        )
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputO)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputO,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        //Проверка masreMethod
        masreMethod = MasreMethod(inputO, creationDataList)
        var masreScope: ArrayList<MasreScope> = masreMethod.enterLoopMasre()
        masreScope.forEachIndexed { index, masreScope ->
            println(index)
            println(masreScope)
        }
        //More or less correctly
        //1. Почему всегда принимает наименьший угол в шевронной передаче?
        //2. Почему такой большой коэффициент осевого перекрытия?
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_fourth_scheme(){
        val inputO = setInput(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 4,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputO)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputO,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        //More or less correctly
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_sixth_scheme(){
        testScheme(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 6,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        //More or less correctly
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_7_scheme(){
        testScheme(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 7,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        //More or less correctly
        //Здесь уже есть углы больше минимального, это хорошо, показывает, что это работает более-менее
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_8_scheme(){
        /*testScheme(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 8,
            isChevrone = true,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )*/
        //More or less correctly
        //Здесь уже есть углы больше минимального, это хорошо, показывает, что это работает более-менее
        //Это Мишин Вариант
        val inputO = setInput(
            isED = false,
            TT = 412.286f,
            NT = 47.739f,
            LH = 10000,
            NRR = 2,
            KOL = 10000,
            UREMA = 19.9f,
            ind = 8,
            isChevrone = true,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputO)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputO,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        //More or less correctly
        weedOutAndSorting(inputO, creationDataList)
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_9_scheme(){
        testScheme(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 9,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        //More or less correctly
        //Даже соосность соблюдается
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_10_scheme(){
        testScheme(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 10,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        //More or less correctly
        //Даже соосность соблюдается
        //Даже критерий некратности зубьев вроде соблюдается
        //Чекнуть насч
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_11_scheme(){
        testScheme(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 11,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = true
        )
        //More or less correctly
        //Даже соосность соблюдается
        //Даже критерий некратности зубьев вроде соблюдается
        //Чекнуть насчёт максимальных и минимальных передаточных отношений
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_12_scheme(){
        testScheme(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 12,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        //Со знаками разобрался, не совсем понятно, что там с интерференцией
    }

    @org.junit.Test
    fun test_zcred_method_one_staged_13_scheme(){
        testScheme(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 13,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        //С интерференцией стало понятно, что если PAR == true and SIGN < 0, то он пишет, что происходит интерференция, что по идее не так
    }







    //Вот здесь в этот тест включено почти всё, на нём хорошо тестить

    @org.junit.Test
    fun test_masre_method_on_5_scheme(){
        val inputO = setInput(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 5,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputO)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputO,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        //С интерференцией стало понятно, что если PAR == true and SIGN < 0, то он пишет, что происходит интерференция, что по идее не так
        //Проверка masreMethod
        masreMethod = MasreMethod(inputO, creationDataList)
        var masreScope: ArrayList<MasreScope> = masreMethod.enterLoopMasre()
        masreScope.forEachIndexed { index, masreScope ->
            println(index)
            println(masreScope)
        }
        //С двуступенчатыми вроде работает корректно
        //Производим отсев вариантов
        val properCreationDataArrList: ArrayList<CreationData> = weedOutCreationDataList(inputO, creationDataList, masreScope)
        properCreationDataArrList.forEachIndexed { index, creationData ->
            println(index)
            creationData.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(creationData.zcredScope.toString())
        }
        val newMasreDataList: ArrayList<MasreScope> = weedOutMasreDataList(inputO, creationDataList, masreScope)
        properCreationDataArrList.forEachIndexed { index, creationData ->
            val ind: Float = (diffSGH(inputO, creationData).pow(2) + diffSGHM(inputO, creationData).pow(2) +
                    diffSGF(inputO, creationData).pow(2) + diffSGFM(inputO, creationData).pow(2) +
                    diffUCalc(inputO, creationData).pow(2) + minHRC(inputO, creationData).pow(2) +
                    minAW(inputO, creationData).pow(2) - Ep(inputO, creationData).pow(2) +
                    minSize(masreScope = newMasreDataList[index]).pow(2) + newMasreDataList[index].MARE!!.pow(2)
                    ).pow(1/2f)
            properCreationDataArrList[index].sorting = ind
            newMasreDataList[index].sorting = ind
        }
        newMasreDataList.forEachIndexed { index, masreScope ->
            println(index)
            println(masreScope.sorting)
        }
        properCreationDataArrList.sortBy { it.sorting }
        newMasreDataList.sortBy { it.sorting }
        println("Вот отсюда начинаются сортированные списки: ")
        properCreationDataArrList.forEachIndexed { index, creationData ->
            println(index)
            creationData.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(creationData.zcredScope.toString())
            println(newMasreDataList[index].toString())
        }
    }








    @org.junit.Test
    fun test_masre_method_on_9_scheme(){
        val inputO = setInput(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 9,
            isChevrone = false,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputO)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputO,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        //С интерференцией стало понятно, что если PAR == true and SIGN < 0, то он пишет, что происходит интерференция, что по идее не так
        //Проверка masreMethod
        masreMethod = MasreMethod(inputO, creationDataList)
        var masreScope: ArrayList<MasreScope> = masreMethod.enterLoopMasre()
        masreScope.forEachIndexed { index, masreScope ->
            println(index)
            println(masreScope)
        }
        //С 4 типом работает вроде бы нормально
    }

    @org.junit.Test
    fun test_masre_method_on_8_scheme(){
        val inputO = setInput(
            isED = false,
            TT = 774.3f,
            NT = 44.4f,
            LH = 10000,
            NRR = 1,
            KOL = 1000,
            UREMA = 35f,
            ind = 8,
            isChevrone = true,
            isInner = false,
            ISTCol = 2,
            PAR = false
        )
        val reducerOptions = allReducersMethod.tryToCalculateOptions(inputO)
        val someOptions: List<ReducerOptionTemplate> = listOf(reducerOptions[0], reducerOptions[1], reducerOptions[2], reducerOptions[3])
        /*reducerOptions.forEach {
            println(it.u)
        }*/
        val creationDataList = ZCREDMethod.enterZCRED(
            input = inputO,
            options = reducerOptions
        )
        creationDataList.forEachIndexed {index, element ->
            println(index)
            element.gearWheelStepsArray.forEach {
                var toPrint = it.dopnScope.toString()
                println(toPrint)
                toPrint = it.zuc1hScope.toString()
                println(toPrint)
                toPrint = it.zucepScope.toString()
                println(toPrint)
                toPrint = it.zuc2hScope.toString()
                println(toPrint)
                toPrint = it.zucfScope.toString()
                println(toPrint)
            }
            println(element.zcredScope.toString())
        }
        //С интерференцией стало понятно, что если PAR == true and SIGN < 0, то он пишет, что происходит интерференция, что по идее не так
        //Проверка masreMethod
        masreMethod = MasreMethod(inputO, creationDataList)
        var masreScope: ArrayList<MasreScope> = masreMethod.enterLoopMasre()
        masreScope.forEachIndexed { index, masreScope ->
            println(index)
            println(masreScope)
        }
        //С 7 типом вроде бы тоже работает нормально
    }

}