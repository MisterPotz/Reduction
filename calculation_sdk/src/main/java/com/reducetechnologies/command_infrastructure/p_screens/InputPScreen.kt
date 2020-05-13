package com.reducetechnologies.command_infrastructure.p_screens

import com.reducetechnologies.command_infrastructure.*
import com.reduction_technologies.database.tables_utils.OneSidedDomain
import com.reduction_technologies.database.tables_utils.TwoSidedDomain

internal object InputPScreen {
    private val pScreen : PScreen = PScreen(
        "InputData",
        fields = InputPScreenFields.getFields()
    )

    fun getPScreen() : PScreen {
        return pScreen
    }
}
internal object InputPScreenFields {
    private lateinit var fields: MutableList<PField>

    private val mainText = PField(
        pFieldType = PFieldType.TEXT,
        typeSpecificData = TextSpec(
            text = "Введите все необходимые данные о подбираемом редукторе"
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
                //задать потом encyclopediaId; answer как я понимаю не нужен
            )
        ),
        fieldId = 2
    )
    private val isED = PField(
        pFieldType = PFieldType.INPUT_LIST,
        typeSpecificData = InputListSpec(
            title = "Будет ли подбираться редуктор?",
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
            title = "Вращающий момент на тихоходном валу",
            type = InputTextType.FLOAT,
            default = null,
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">", 0F),
                    OneSidedDomain("<", 1000000F)
                )
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 4
    )
    private val NT = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Частота вращения тихоходного вала",
            type = InputTextType.FLOAT,
            default = null,
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">", 0F),
                    OneSidedDomain("<", 1000000F)
                )
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 5
    )
    private val LH = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Суммарное время работы (ресурс)",
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
                options = List(5) { (it + 1).toString()}
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 7
    )
    private val KOL = PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Серийность (в год)",
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
                )
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
                )
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
                    OneSidedDomain("<", 1000000F)
                )
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
                    OneSidedDomain(">=", 0F),
                    OneSidedDomain("<", 1000000F)
                )
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
            default = "0.4",
            additional = AdditionalInputText(
                domain = TwoSidedDomain(
                    OneSidedDomain(">=", 0F),
                    OneSidedDomain("<", 1000000F)
                )
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
                )
            )
            //задать потом encyclopediaId; answer как я понимаю не нужен
        ),
        fieldId = 17
    )

    private fun setFields(){
        fields = mutableListOf(
            mainText, gearSchemes, isED, TT, NT, LH, NRR, KOL, U0, UREMA, optionalParameters, ALF,
            KPD, HL, HA, HG, C
        )
    }
    fun getFields() : List<PField> {
        setFields()
        return fields
    }
}