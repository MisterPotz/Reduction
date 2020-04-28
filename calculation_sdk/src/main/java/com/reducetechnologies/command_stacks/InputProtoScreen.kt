package com.reducetechnologies.command_stacks

object InputProtoScreen : ProtoScreen(
    titlePane = "Input Screen",
    protoScreenId = 0,//Maybe
    fields = InputProtoScreenFields.getFields()
) {
    override fun fillTags() {
        //По идее он уже не нужен
    }



}

object InputProtoScreenFields {
    private lateinit var fields: MutableList<Field>


    private val mainText : SimpleTextField = SimpleTextField(
        text = "Введите все необходимые данные о подбираемом редукторе: ",
        fieldId = 0,
        tags = listOf()//Если это plain text, то без тегов?
    )

    private val gearSchemes : InputPicturesField = InputPicturesField(
        encodedImages = listOf(),//Сюда бы передавать из файла закодированные в base64 картинки
        title = "Схемы редукторов:",
        hintText = "Выберете нужную Вам схему",
        fieldId = 1,
        tags = listOf(RequiredProcessing.HAS_INPUT)
    )


    private val isEd : InputListField = InputListField(
        options = listOf("Да", "Нет"),
        title = "Будет ли подбираться редуктор?",
        hintText = "При расчёте редуктора для курсовой работы выберите \"Нет\"",
        fieldId = 2,
        tags = listOf(RequiredProcessing.HAS_INPUT)
    )
    private val TT : InputTextField = InputTextField(
        title = "Вращающий момент на тихоходном валу",
        hintText = "",//Или лучше просто поменять на nullable type?
        fieldId = 3,
        tags = listOf(RequiredProcessing.HAS_INPUT)
    )
    private val NT : InputTextField = InputTextField(
        title = "Частота вращения тихоходного вала",
        hintText = "",
        fieldId = 4,
        tags = listOf(RequiredProcessing.HAS_INPUT)
    )
    private val LH : InputTextField = InputTextField(
        title = "Суммарное время работы (ресурс)",
        hintText = "Значение по умолчанию: 10000 часов",
        fieldId = 5,
        tags = listOf(RequiredProcessing.HAS_INPUT)
    )
    private val NRR : InputTextField = InputTextField(
        title = "Номер типового режима нагружения передачи",
        hintText = "Введите значение от 1 до 5",
        fieldId = 6,
        tags = listOf(RequiredProcessing.HAS_INPUT)
    )
    private val KOL : InputTextField = InputTextField(
        title = "Серийность (в год)",
        hintText = "Значение по умолчанию: 10000 ед.",
        fieldId = 7,
        tags = listOf(RequiredProcessing.HAS_INPUT)
    )
    private val U0 : InputTextField = InputTextField(
        title = "Передаточное отношение промежуточной передачи между" +
                " входным валом редуктора и электродвигателем (если есть)",
        hintText = "Значение по умолчанию: 1 (нет промежуточной передачи)",
        fieldId = 8,
        tags = listOf(RequiredProcessing.HAS_INPUT)
    )
    private val UREMA : InputTextField = InputTextField(
        title = "Желаемое максимальное передаточное отношение редуктора",
        hintText = "",
        fieldId = 9,
        tags = listOf(RequiredProcessing.HAS_INPUT)
    )


    private val optionalParameters : SimpleTextField = SimpleTextField(
        text = "Параметры, которые имеют значение по умолчанию и могут не задаваться:",
        fieldId = 10,
        tags = listOf()//Что здесь?
    )
    private val ALF : InputTextField = InputTextField(
        title = "Угол профиля исходного контура (в градусах)",
        hintText = "Значение по умолчанию: 20 градусов",
        fieldId = 11,
        tags = listOf()//Что нужно в случае необязательного заполнения? Тоже has input?
    )
    private val KPD : InputTextField = InputTextField(
        title = "Для каждой схемы есть значение по умолчанию",
        hintText = "У каждой схемы есть соответствующее ей значение по умолчанию",
        fieldId = 12,
        tags = listOf()//?
    )
    private val HL : InputTextField = InputTextField(
        title = "Коэффициент граничной высоты зуба",
        hintText = "Значение по умолчанию: 2",
        fieldId = 13,
        tags = listOf()//?
    )
    private val HA : InputTextField = InputTextField(
        title = "Коэффициент высоты головки зуба",
        hintText = "Значение по умолчанию: 1",
        fieldId = 14,
        tags = listOf()//?
    )
    private val HG : InputTextField = InputTextField(
        title = "Коэффициент высоты модификации головки зуба",
        hintText = "Значение по умолчанию: 0.4",
        fieldId = 15,
        tags = listOf()//?
    )
    private val C : InputTextField = InputTextField(
        title = "Коэффициент радиального зазора",
        hintText = "Значение по умолчанию: 0.25",
        fieldId = 16,
        tags = listOf()//?
    )

    private fun setFields(){
        fields = mutableListOf(
            mainText, gearSchemes, isEd, TT, NT, LH, NRR, KOL, U0, UREMA, optionalParameters, ALF,
            KPD, HL, HA, HG, C
        )
    }

    internal fun getFields() : List<Field> {
        setFields()
        return fields
    }

}