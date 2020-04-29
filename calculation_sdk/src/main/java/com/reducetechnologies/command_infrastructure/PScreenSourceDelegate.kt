package com.reducetechnologies.command_infrastructure

// contains calculation classes
internal class PScreenSourceDelegate : PScreenSource() {
    // simple stack that is consumed. Being build with some other classes (or with calculation on flow)
    override protected val preparedStack: MutableList<PScreen> = mutableListOf(
        PScreen(
            "Введите данные",
            mutableListOf(
                PField(
                    PFieldType.INPUT_TEXT, InputTextSpec(
                        "Коэффициент высоты модификации головки зуба",
                        InputTextType.FLOAT, "0.4", AdditionalInputText()
                    ), 1
                ),
                PField(
                    PFieldType.INPUT_LIST, InputListSpec(
                        "будет ли расчет этой хрени", 0,
                        AdditionalInputList(options = listOf("Да", "Нет"))
                    ), 2
                )
            )
        )
    )

    override fun validate(pScreen: PScreen) : PScreen? {
        // логика проверки
        // если все норм возвратит нулл
        return null
    }
}