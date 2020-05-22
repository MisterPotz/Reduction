package com.reducetechnologies.command_infrastructure

import com.reducetechnologies.calculation_util.NumRound
import com.reducetechnologies.calculations.InputData
import com.reducetechnologies.calculations_entity.CalculationsEntity
import com.reducetechnologies.calculations_entity.ReducerData
import com.reducetechnologies.command_infrastructure.p_screens.InputPScreen
import com.reducetechnologies.command_infrastructure.p_screens.OutputPScreen
import com.reducetechnologies.command_infrastructure.p_screens.StandbyPScreen
import com.reducetechnologies.di.CalculationsComponent
import com.reducetechnologies.specificationsAndRequests.Specifications
import kotlin.math.PI

// contains calculation classes
internal class PScreenSourceDelegate(private val calculationsComponent: CalculationsComponent) : PScreenSource() {
    // simple stack that is consumed. Being build with some other classes (or with calculation on flow)
    override val preparedStack: MutableList<PScreen> = mutableListOf(
        InputPScreen().getPScreen(),
        StandbyPScreen.getPScreen()
    )

    //Нз зачем тебе здесь это
    private fun generateInputPicturesList(): List<String> {
        return List<String>(14) {
            "input_pictures/${it + 1}.jpg"
        }
    }

    override fun validate(pScreen: PScreen): PScreen? {
        /**
         * Сначала проверка, что содержит поля с вводом
         */
        if (pScreen.fields.any { it.pFieldType.needsInput }) {
            //Проверяем, если это InputPScreen
            if (pScreen.title.toLowerCase() == "inputdata") {
                val nextInputPScreen = InputPScreen(pScreen)
                var isBad: Boolean = false
                //Проверяем соответствие передаточных отношений областям определений
                //Одноступенчатый
                if (
                    checkOneStepU(pScreen)
                        ) {
                    isBad = true
                    changeOneStepU(pScreen = pScreen, nextInputPScreen = nextInputPScreen)
                }
                //Двухступенчатый
                else if (
                    checkTwoStepU(pScreen)
                        ) {
                    isBad = true
                    changeTwoStepU(pScreen = pScreen, nextInputPScreen = nextInputPScreen)
                }
                //Планетарный
                else if (
                    checkPlanetarTwoStepU(pScreen)
                        ) {
                    isBad = true
                    changePlanetarTwoStepU(pScreen = pScreen, nextInputPScreen = nextInputPScreen)
                }
                //новый PScreen
                if (isBad) {
                    // preparedStack.add(nextInputPScreen.getPScreen())
                    // Хорошо бы еще возникшие ошибки указывать
                    return nextInputPScreen.getPScreen()
                } else {
                    //Создаём инпут из полученных данных
                    val inputData: InputData = prepareInputData(pScreen)
                    //Получаем outputData
                    val reducerData: ArrayList<ReducerData> =
                        CalculationsEntity(
                            inputData = inputData,
                            calculationsComponent = calculationsComponent
                        ).reducerDataList
                    //Создаём OutputPScreen и встраиваем его в очередь
                    val outputPScreen: OutputPScreen =
                        OutputPScreen(reducerData = reducerData)
                    preparedStack.add(outputPScreen.getPScreen())
                }
            }
        }
        // если все норм возвратит нулл
        return null
    }

    /**
     * Для получения [InputData] из PScreen
     */
    private fun prepareInputData(pScreen: PScreen): InputData {
        //Предполагает, что проверку, что pScreen = InputPScreen, уже прошёл в более высокой инстаниции
        val indexOfReducer = (pScreen.fields.find { it.fieldId == 2 }!!.typeSpecificData as InputPictureSpec)
            .additional.answer!!.toInt()
        //Простая, но необходимая проверка ещё разок
        if (indexOfReducer !in 0..13) {
            throw IllegalArgumentException("Number of reducer is out of bounds")
        }
        //isED - как задаётся через answer, 0 и 1?
        val isED =
            when ((pScreen.fields.find { it.fieldId == 3 }!!.typeSpecificData as InputListSpec)
                .additional.answer) {
                0 -> false
                else//Для 1
                    -> true
            }
        //ТТ
        val TT = (pScreen.fields.find { it.fieldId == 4 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toFloat()
        //NT
        val NT = (pScreen.fields.find { it.fieldId == 5 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toFloat()
        //LH
        val LH = (pScreen.fields.find { it.fieldId == 6 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toInt()
        val NRR = (pScreen.fields.find { it.fieldId == 7 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toInt()
        val KOL = (pScreen.fields.find { it.fieldId == 8 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toInt()
        val U0 = (pScreen.fields.find { it.fieldId == 9 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toFloat()
        val UREMA = (pScreen.fields.find { it.fieldId == 10 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toFloat()
        val ALF = NumRound.angleToRad(
            (pScreen.fields.find { it.fieldId == 12 }!!.typeSpecificData as InputTextSpec)
                .additional.answer!!.toFloat()
        )
        val KPD = (pScreen.fields.find { it.fieldId == 13 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toFloat()
        val HL = (pScreen.fields.find { it.fieldId == 14 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toFloat()
        val HA = (pScreen.fields.find { it.fieldId == 15 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toFloat()
        val HG = (pScreen.fields.find { it.fieldId == 16 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toFloat()
        val C = (pScreen.fields.find { it.fieldId == 17 }!!.typeSpecificData as InputTextSpec)
            .additional.answer!!.toFloat()
        val isChevrone: Boolean =
            indexOfReducer in arrayOf(2, 3, 7, 8)
        //Пока оставим внутреннее зацепление только в 12 и 13 схемах, что учтём в setInput
        val isInner: Boolean = false
        val ISTCol =
            when (indexOfReducer) {
                in 0..3 -> 1
                else
                    -> 2
            }
        val PAR: Boolean =
            indexOfReducer == 11
        return setInput(
            isED = isED,
            TT = TT,
            NT = NT,
            LH = LH,
            NRR = NRR,
            KOL = KOL,
            U0 = U0,
            UREMA = UREMA,
            ind = indexOfReducer,
            isChevrone = isChevrone,
            isInner = isInner,
            KPD = KPD,
            ISTCol = ISTCol,
            PAR = PAR,
            ALF = ALF,
            HL = HL,
            HG = HG,
            HA = HA,
            C = C
        )
    }

    /**
     * Передаём сюда уже значения, полученные из InputPScreen
     */
    private fun setInput(
        isED: Boolean,
        TT: Float,
        NT: Float,
        LH: Int,
        NRR: Int,
        KOL: Int,
        U0: Float,
        UREMA: Float,
        ind: Int,
        isChevrone: Boolean,
        isInner: Boolean,
        KPD: Float = 0.97f,
        ISTCol: Int,
        PAR: Boolean,
        ALF: Float,
        HL: Float,
        HA: Float,
        HG: Float,
        C: Float
    ) : InputData {
        /*if (ind > 13 || ind < 0) {
            IllegalArgumentException("Index $ind was less than 0 or more than 13")
        }
        if (ISTCol > 2 || ISTCol < 1) {
            IllegalArgumentException("IstCol $ISTCol was less than 1 or more than 2")
        }*/
        //Достаём sourceDataTable
        val sourceTable = calculationsComponent.getSourceTable()
        //Логика для спецификаций
        val wheelSubtype: Array<Specifications.WheelSubtype> =
            if (ind == 0 || ind == 4 || ind == 13) {
                arrayOf(Specifications.WheelSubtype.SPUR, Specifications.WheelSubtype.SPUR)
            } else if (ind == 1 || ind == 5 || ind == 6 || ind == 9 || ind == 10 || ind == 11 || ind == 12) {
                arrayOf(
                    Specifications.WheelSubtype.HELICAL,
                    Specifications.WheelSubtype.HELICAL)
            } else {
                arrayOf(
                    Specifications.WheelSubtype.CHEVRON,
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
            U0 = U0,
            UREMA = UREMA,
            TIPRE = sourceTable.tipreRow.list[ind],
            NP = sourceTable.npRow.list[ind],
            BETMI = (sourceTable.betMiRow.list[ind]* PI.toFloat()/180f),
            BETMA = (sourceTable.betMaRow.list[ind]* PI.toFloat()/180f),
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
            PAR = PAR,
            ALF = ALF,
            HA = HA,
            HG = HG,
            HL = HL,
            C = C
        )
    }

    /**
     * Проверяем одноступенчатые на непревышение максимального передаточного отношения
     * Здесь опасное место, [as] приводит к типу небезопасно и может выскочить ошибка,
     * но скорее всего нет, потому что всё завязано на fieldID, которые жёстко закреплены
     * Ещё может быть ошибка, если будет введён какой-то текст и он не сможет
     * преобразоваться к Float, но это маловероятно, у фронта стоит для этого
     * ограничение, которое по идее текст ввести не даст
     */
    private fun checkOneStepU(pScreen: PScreen): Boolean {
        return (((pScreen.fields.find { it.fieldId == 2 }!!.typeSpecificData as InputPictureSpec)
            .additional.answer in 0..3) && (pScreen.fields.find { it.fieldId == 10 }!!
            .typeSpecificData as InputTextSpec).additional.answer!!.toFloat() > 8)
    }

    /**
     * Проверяем двуступенчатые на попадание в диапазон
     */
    private fun checkTwoStepU(pScreen: PScreen): Boolean {
        return (((pScreen.fields.find { it.fieldId == 2 }!!.typeSpecificData as InputPictureSpec)
            .additional.answer in arrayOf(4, 5, 6, 7, 8, 9,
            10, 12, 13)) && ((pScreen.fields.find { it.fieldId == 10 }!!
            .typeSpecificData as InputTextSpec).additional.answer!!.toFloat() !in 12.5f..31.5f))
    }

    /**
     * Проверяем планетарную на попадание в диапазон
     */
    private fun checkPlanetarTwoStepU(pScreen: PScreen): Boolean {
        return (((pScreen.fields.find { it.fieldId == 2 }!!.typeSpecificData as InputPictureSpec)
            .additional.answer == 11) && ((pScreen.fields.find { it.fieldId == 10 }!!
            .typeSpecificData as InputTextSpec).additional.answer!!.toFloat() !in 16f..63f))
    }

    /**
     * Для изменения nextInputScreen
     */
    private fun changeOneStepU(pScreen: PScreen, nextInputPScreen: InputPScreen) {
            nextInputPScreen.changeField(ID = 10, min = 1.6f, max = 8f,
                newHint = "Передаточное отношение одноступенчатого редуктора не может " +
                        "быть больше 8.")
            nextInputPScreen.changeField(ID = 2, newDefault =
            (pScreen.fields.find { it.fieldId == 2 }!!.typeSpecificData as InputPictureSpec)
                .additional.answer)
    }

    private fun changeTwoStepU(pScreen: PScreen, nextInputPScreen: InputPScreen) {
            nextInputPScreen.changeField(ID = 10, min = 12.5f, max = 31.5f,
                newHint = "Передаточное отношение двуступенчатого редуктора не может " +
                        "быть меньше 12.5 и больше 31.5")
            nextInputPScreen.changeField(ID = 2, newDefault =
            (pScreen.fields.find { it.fieldId == 2 }!!.typeSpecificData as InputPictureSpec)
                .additional.answer)
    }


    private fun changePlanetarTwoStepU(pScreen: PScreen, nextInputPScreen: InputPScreen) {
            nextInputPScreen.changeField(ID = 10, min = 16f, max = 63f,
                newHint = "Передаточное отношение двуступенчатого планетарного редуктора не может " +
                        "быть меньше 16 и больше 63")
            nextInputPScreen.changeField(ID = 2, newDefault =
            (pScreen.fields.find { it.fieldId == 2 }!!.typeSpecificData as InputPictureSpec)
                .additional.answer)
    }
}