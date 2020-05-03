package com.reducetechnologies.command_infrastructure.p_screens

import com.reducetechnologies.command_infrastructure.*

internal object StandbyPScreen {
    private val pScreen : PScreen = PScreen(
        "Standby Screen",
        fields = StandbyPScreenFields.getFields()
    )

    fun getPScreen() : PScreen {
        return pScreen
    }
}

internal object StandbyPScreenFields {
    private lateinit var fields: MutableList<PField>

    private val firstParagraph = PField(
        pFieldType = PFieldType.TEXT,
        typeSpecificData = TextSpec(
            text = "1. На первом этапе происходит перебор и создание всех возможных шаблонов\n" +
                    "редукторов по следующим критериям:\n" +
                    "a. Если isED == true: частотам вращения валов электродвигателя.\n" +
                    "b. Твёрдости материала колёс HRC.\n" +
                    "c. Если ISTCol == 2: отношению передаточного числа быстроходной ступени к\n" +
                    "тихоходной .\n" +
                    "d. Непревышению получившегося передаточного значения\n" +
                    "максимального (введённого пользователем) значения.\n" +
                    "e. Коэффициентам ширины колёс по межосевому расстоянию ψ ba .",
            additional = AdditionalText(
                type = TextType.BODY
            //не забыть заполнить по возможности ссылки на энциклопедию
            )
        ),
        fieldId = 1
    )

    private val secondParagraph = PField(
        pFieldType = PFieldType.TEXT,
        typeSpecificData = TextSpec(
            text = "2. После того, как весь сет с шаблонами редукторов готов, происходит расчёт каждого\n" +
                    "варианта. Каждый расчёт является итеративным процессом и включает в себя\n" +
                    "несколько стадий. Первая из них – расчёт допускаемых напряжений –\n" +
                    "предназначена для определения допускаемых контактных напряжений и напряжений\n" +
                    "изгиба в стальных зубчатых колёсах при действии номинальной и максимальной\n" +
                    "нагрузок ([σ] H , [σ] H max , [σ] F , [σ] F max ).",
            additional = AdditionalText(
                type = TextType.BODY
                //не забыть заполнить по возможности ссылки на энциклопедию
            )
        ),
        fieldId = 2
    )

    private val thirdParagraph = PField(
        pFieldType = PFieldType.TEXT,
        typeSpecificData = TextSpec(
            text = "3. Затем следует проектировочный расчёт зубчатых передач на контактную прочность, в\n" +
                    "котором определяются наиболее важные геометрические параметры ступени, такие\n" +
                    "как межосевое расстояние a w , ширина венцов b w , числа зубьев колёс Z, значение\n" +
                    "модуля зацепления m, угол наклона зубьев β в косозубых передачах, коэффициенты\n" +
                    "смещения исходного контура из условия неподрезания зубьев X и т.д.",
            additional = AdditionalText(
                type = TextType.BODY
                //не забыть заполнить по возможности ссылки на энциклопедию
            )
        ),
        fieldId = 3
    )

    private val fourthParagraph = PField(
        pFieldType = PFieldType.TEXT,
        typeSpecificData = TextSpec(
            text = "4. Потом определяются все геометрические размеры колёс в ступени, все диаметры\n" +
                    "колёс, качественные параметры зацепления.",
            additional = AdditionalText(
                type = TextType.BODY
                //не забыть заполнить по возможности ссылки на энциклопедию
            )
        ),
        fieldId = 4
    )

    private val fifthParagraph = PField(
        pFieldType = PFieldType.TEXT,
        typeSpecificData = TextSpec(
            text = "5. После этого рассчитываются контактные напряжения и напряжения при изгибе\n" +
                    "зубьев.",
            additional = AdditionalText(
                type = TextType.BODY
                //не забыть заполнить по возможности ссылки на энциклопедию
            )
        ),
        fieldId = 5
    )

    private val sixthParagraph = PField(
        pFieldType = PFieldType.TEXT,
        typeSpecificData = TextSpec(
            text = "6. Процесс итерируется несколько раз, пока не произойдёт подбор оптимальных\n" +
                    "значений передачи для её безопасной работы или вариант будет признан\n" +
                    "неподходящим.",
            additional = AdditionalText(
                type = TextType.BODY
                //не забыть заполнить по возможности ссылки на энциклопедию
            )
        ),
        fieldId = 6
    )

    //Каких картинок накидать?

    private fun setFields(){
        fields = mutableListOf(
            firstParagraph, secondParagraph, thirdParagraph,
            fourthParagraph, fifthParagraph, sixthParagraph
        )
    }
    fun getFields() : List<PField> {
        setFields()
        return fields
    }
}