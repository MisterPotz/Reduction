package com.reducetechnologies.command_infrastructure.p_screens

import com.reducetechnologies.command_infrastructure.*
import com.reduction_technologies.database.tables_utils.OneSidedDomain
import com.reduction_technologies.database.tables_utils.TwoSidedDomain

internal class InputPScreen {
    private val pScreen : PScreen
    private val inputPScreenFields: InputPScreenFields
    constructor() {
        inputPScreenFields = InputPScreenFields()
        this.pScreen = PScreen(
            "InputData",
            fields = inputPScreenFields.getStandartFields()
        )
    }
    constructor(inputPScreen: PScreen) {
        inputPScreenFields = InputPScreenFields(inputPScreen.fields as MutableList<PField>)
        this.pScreen = PScreen(
            title = inputPScreen.title,
            fields = inputPScreenFields.getFields()
        )
    }

    fun changeField(ID: Int, min: Float? = null, max: Float? = null, newDefault: Int? = null, newHint: String? = null) {
        inputPScreenFields.changeField(ID = ID, min = min, max = max, newDefault = newDefault, newHint = newHint)
    }

    fun getPScreen() : PScreen {
        return pScreen
    }
}
internal class InputPScreenFields() {
    private lateinit var fields: MutableList<PField>

    private val mainText = PField(
        pFieldType = PFieldType.TEXT,
        typeSpecificData = TextSpec(
            text = "Введите все необходимые данные о подбираемом редукторе",
            additional = AdditionalText(TextType.HEADLINE)
        ),
        fieldId = 1
    )
    private val gearSchemes = PField(
        pFieldType = PFieldType.INPUT_PICTURE,
        typeSpecificData = InputPictureSpec(
            title = "Схемы редукторов:",
            default = 0,//ну или 1, самый первый короче просто
            additional = AdditionalInputImage(
                imagePaths = listOf(
                    "input_schemes/1.jpg",
                    "input_schemes/2.jpg",
                    "input_schemes/3.jpg",
                    "input_schemes/4.jpg",
                    "input_schemes/5.jpg",
                    "input_schemes/6.jpg",
                    "input_schemes/7.jpg",
                    "input_schemes/8.jpg",
                    "input_schemes/9.jpg",
                    "input_schemes/10.jpg",
                    "input_schemes/11.jpg",
                    "input_schemes/12.jpg",
                    "input_schemes/13.jpg",
                    "input_schemes/14.jpg"
                )
                //answer = 3//Это только для теста, потом убрать
                //задать потом encyclopediaId; answer как я понимаю не нужен
            )
        ),
        fieldId = 2
    )
    private val isED = PField(
        pFieldType = PFieldType.INPUT_LIST,
        typeSpecificData = InputListSpec(
            title = "Будет ли подбираться электродвигатель?",
            default = null,
            additional = AdditionalInputList(
                options = listOf(
                    "Да",
                    "Нет"
                ),
                hint = "При расчёте редуктора для курсовой работы выберите \"Нет\""
                //задать потом encyclopediaId; answer как я понимаю не нужен
            )
        ),
        fieldId = 3
    )
    private val TT = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Вращающий момент на тихоходном валу, Н*м",
            type = InputTextType.FLOAT,
            default = null,
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">", 0F),
                    OneSidedDomain("<", 1000000F)
                ),
                encyclodpediaId = 20
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 4
    )
    private val NT = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Частота вращения тихоходного вала, об/мин",
            type = InputTextType.FLOAT,
            default = null,
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">", 0F),
                    OneSidedDomain("<", 1000000F)
                ),
                encyclodpediaId = 21
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 5
    )
    private val LH = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Суммарное время работы (ресурс), часы",
            type = InputTextType.INTEGER,
            default = "10000",
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 10000F),
                    OneSidedDomain("<", 1000000F)
                )
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 6
    )
    private val NRR = PField(
        pFieldType = PFieldType.INPUT_LIST,
        typeSpecificData = InputListSpec(
            title = "Номер типового режима нагружения передачи",
            default = null,
            additional = AdditionalInputList(
                options = List(5) { (it + 1).toString()},
                encyclodpediaId = 26
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 7
    )
    private val KOL = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Серийность (в год), штук",
            type = InputTextType.INTEGER,
            default = "10000",
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 1F),
                    OneSidedDomain("<", 1000000F)
                )
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 8
    )
    private val U0 = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Передаточное отношение промежуточной передачи между" +
                    " входным валом редуктора и электродвигателем (если есть)",
            type = InputTextType.FLOAT,
            default = "1.0",
            additional = AdditionalInputText(
                hint = "1 - нет промежуточной передачи",
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 1F),
                    OneSidedDomain("<", 1000F)
                )
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 9
    )
    private val UREMA = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Желаемое максимальное передаточное отношение редуктора",
            type = InputTextType.FLOAT,
            default = null,
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 1.6F),
                    OneSidedDomain("<", 64F)
                )
                //answer = "9"//Это только для теста, потом убрать
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 10
    )
    private val optionalParameters = PField(
        pFieldType = PFieldType.TEXT,
        typeSpecificData = TextSpec(
            text = "Необязательные параметры: ",additional = AdditionalText(TextType.HEADLINE)
        ),
        fieldId = 11
    )
    private val ALF = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Угол профиля исходного контура (в градусах)",
            type = InputTextType.FLOAT,
            default = "20.0",
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 14.5F),
                    OneSidedDomain("<=", 45F)
                ),
                encyclodpediaId = 1
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 12
    )
    private val KPD = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "КПД редуктора",
            type = InputTextType.FLOAT,
            default = "0.97",
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">", 0F),
                    OneSidedDomain("<", 1F)
                ),
                encyclodpediaId = 19
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 13
    )
    private val HL = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Коэффициент граничной высоты зуба",
            type = InputTextType.FLOAT,
            default = "2.0",
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 0F),
                    OneSidedDomain("<=", 2F)
                ),
                encyclodpediaId = 22
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 14
    )
    private val HA = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Коэффициент высоты головки зуба",
            type = InputTextType.FLOAT,
            default = "1.0",
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 1F),
                    OneSidedDomain("<=", 1.1F)
                ),
                encyclodpediaId = 23
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 15
    )
    private  val HG = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Коэффициент высоты модификации головки зуба",
            type = InputTextType.FLOAT,
            default = "0",
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 0F),
                    OneSidedDomain("<=", 0.45F)
                ),
                encyclodpediaId = 24
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 16
    )
    private val C = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Коэффициент радиального зазора",
            type = InputTextType.FLOAT,
            default = "0.25",
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 0.25F),
                    OneSidedDomain("<=", 0.4F)
                ),
                encyclodpediaId = 25
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 17
    )

    //Вторичный конструктор
    constructor(fields: MutableList<PField>) : this() {
        this.fields = fields
    }

    private fun setFields(){
        fields = mutableListOf(
            mainText, gearSchemes, isED, TT, NT, LH, NRR, KOL, U0, UREMA, optionalParameters, ALF,
            KPD, HL, HA, HG, C
        )
    }

    private fun changeGearSchemesField(newDefault: Int): PField {
        return PField(
            pFieldType = PFieldType.INPUT_PICTURE,
            typeSpecificData = InputPictureSpec(
                title = "Схемы редукторов:",
                default = newDefault,//ну или 1, самый первый короче просто
                additional = AdditionalInputImage(
                    imagePaths = listOf(
                        "input_schemes/1.jpg",
                        "input_schemes/2.jpg",
                        "input_schemes/3.jpg",
                        "input_schemes/4.jpg",
                        "input_schemes/5.jpg",
                        "input_schemes/6.jpg",
                        "input_schemes/7.jpg",
                        "input_schemes/8.jpg",
                        "input_schemes/9.jpg",
                        "input_schemes/10.jpg",
                        "input_schemes/11.jpg",
                        "input_schemes/12.jpg",
                        "input_schemes/13.jpg",
                        "input_schemes/14.jpg"
                    )
                    //задать потом encyclopediaId; answer как я понимаю не нужен
                )
            ),
            fieldId = 2
        )
    }

    private fun changeUField(min: Float, max: Float, newHint: String): PField {
        return PField(
            pFieldType = PFieldType.INPUT_TEXT,
            typeSpecificData = InputTextSpec(
                title = "Желаемое максимальное передаточное отношение редуктора",
                type = InputTextType.FLOAT,
                default = null,
                additional = AdditionalInputText(
                    hint = newHint,
                    domain = TwoSidedDomain(
                        OneSidedDomain(">=", min),
                        OneSidedDomain("<", max)
                    )
                )
                //задать потом encyclopediaId; answer как я понимаю не нужен
            ),
            fieldId = 10
        )
    }
    /**
     * Используется для изменения значений полей
     */
    fun changeField(ID: Int, min: Float? = null, max: Float? = null, newDefault: Int? = null, newHint: String? = null){
        val pField: PField? =
        fields.find {
            it.fieldId == ID
        }
        when (pField!!.fieldId) {
            2 -> fields[pField.fieldId-1] = changeGearSchemesField(newDefault = newDefault!!)
            10 -> fields[pField.fieldId-1] = changeUField(min = min!!, max = max!!, newHint = newHint!!)
        }
        //fields[pField.fieldId-1] = PField()
    }

    /**
     * Используется, если мы уже применяли вторичный конструктор и изменяли поля
     */
    fun getFields() : MutableList<PField>{
        return fields
    }

    /**
     * Используется только для получения самого стандартного экрана
     */
    fun getStandartFields() : MutableList<PField> {
        setFields()
        return fields
    }
}