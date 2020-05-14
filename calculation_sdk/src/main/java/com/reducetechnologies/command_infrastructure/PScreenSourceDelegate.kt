package com.reducetechnologies.command_infrastructure

import com.reducetechnologies.command_infrastructure.p_screens.InputPScreen
import com.reducetechnologies.command_infrastructure.p_screens.StandbyPScreen

// contains calculation classes
internal class PScreenSourceDelegate : PScreenSource() {
    // simple stack that is consumed. Being build with some other classes (or with calculation on flow)
    override protected val preparedStack: MutableList<PScreen> = mutableListOf(
        InputPScreen.getPScreen(),
        StandbyPScreen.getPScreen()
    )

    private val testList = mutableListOf(
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
                ),
                PField(
                    PFieldType.PICTURE,
                    PictureSpec(
                        pictureSourceType = PictureSourceType.PATH,
                        source = PictureStringPath("encyclopedia_pictures/reductor.jpg")
                    ),
                    3
                ),
                PField(
                    PFieldType.INPUT_PICTURE,
                    InputPictureSpec(
                        "Введите схему", null, AdditionalInputImage(
                            generateInputPicturesList(), null, null
                        )
                    ), 4
                ),
                PField(
                    PFieldType.TEXT, TextSpec(
                        "Ищщо дичи"
                    ), 5
                ),
                PField(
                    PFieldType.PICTURE,
                    PictureSpec(
                        pictureSourceType = PictureSourceType.PATH,
                        source = PictureStringPath("input_pictures/1.jpg")
                    ),
                    6
                ),
                PField(
                    PFieldType.TEXT, TextSpec(
                        "И еще чуть чуть"
                        , AdditionalText(TextType.HEADLINE)
                    ), 5
                ),
                PField(
                    PFieldType.PICTURE,
                    PictureSpec(
                        pictureSourceType = PictureSourceType.PATH,
                        source = PictureStringPath("input_pictures/10.jpg")
                    ),
                    6
                ),
                PField(
                    PFieldType.INPUT_LIST,
                    InputListSpec(
                        "Будет ли закалка", null, AdditionalInputList(
                            listOf(
                                "Да", "Нет", "Не знаю что такое, сам разберись"
                            )
                        )
                    ),
                    7
                ),
                PField(
                    PFieldType.MATH_TEXT,
                    MathTextSpec(
                        text = "some normal text with formula ->  $$ \\newline S_{fb} = \\bigg(\\cfrac{K_L} { K_T K_R } \\bigg) \\cdot S_{fb}^{'}  $$"
                    ),
                    8
                )
            )
        )
    )

    private fun generateInputPicturesList(): List<String> {
        return List<String>(14) {
            "input_pictures/${it + 1}.jpg"
        }
    }

    override fun validate(pScreen: PScreen): PScreen? {
        // логика проверки
        // если все норм возвратит нулл
        return null
    }
}