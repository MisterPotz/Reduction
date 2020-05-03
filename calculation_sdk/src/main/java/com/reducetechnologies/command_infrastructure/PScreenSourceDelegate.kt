package com.reducetechnologies.command_infrastructure

// contains calculation classes
internal class PScreenSourceDelegate : PScreenSource() {
    // simple stack that is consumed. Being build with some other classes (or with calculation on flow)
    override protected val preparedStack: MutableList<PScreen> = mutableListOf(
        PScreen(
            "Введите данные",
            mutableListOf(
                PField(
                    PFieldType.TEXT, TextSpec(
                        "Коэффициент высоты модификации головки зуба нужен. Просто нужен. Для чего - хз.",
                        AdditionalText(TextType.HEADLINE)
                    ), 1
                ),
                PField(
                    PFieldType.TEXT, TextSpec(
                        "Расчет редукторов почему-то представлен только фортраном"
                    ), 2
                )
            )
        ),
        PScreen(
            "Тестовая вторая страничка",
            mutableListOf(
                PField(
                    PFieldType.INPUT_TEXT, InputTextSpec(
                        "Коэффициент поляризации",
                        InputTextType.FLOAT,
                        "",
                        AdditionalInputText(hint = "че нить такое")
                    ), 1
                ),
                PField(
                    PFieldType.TEXT, TextSpec(
                        "Просто дичч"
                    ), 2
                )
            )
        )
    )

    override fun validate(pScreen: PScreen) : WrappedPScreen? {
        // логика проверки
        // если все норм возвратит нулл
        return null
    }
}