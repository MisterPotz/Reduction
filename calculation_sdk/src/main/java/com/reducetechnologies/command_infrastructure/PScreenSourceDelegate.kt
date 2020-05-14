package com.reducetechnologies.command_infrastructure

import com.reducetechnologies.command_infrastructure.p_screens.InputPScreen
import com.reducetechnologies.command_infrastructure.p_screens.StandbyPScreen
import com.reducetechnologies.di.CalculationsComponent

// contains calculation classes
internal class PScreenSourceDelegate() : PScreenSource() {
    // simple stack that is consumed. Being build with some other classes (or with calculation on flow)
    override protected val preparedStack: MutableList<PScreen> = mutableListOf(
        InputPScreen().getPScreen(),
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
        /**
         * Сначала проверка, что содержит поля с вводом
         */
        if (pScreen.fields.any { it.pFieldType.needsInput }) {
            //Проверяем, если это InputPScreen
            if (pScreen.title.toLowerCase() == "inputdata") {
                val nextInputPScreen = InputPScreen(pScreen)
                //Проверяем одноступенчатые на непревышение максимального передаточного отношения
                checkOneStepU(pScreen = pScreen, nextInputPScreen = nextInputPScreen)

            }
        }
        // логика проверки
        // если все норм возвратит нулл
        return null
    }

    /**
     * Проверяем одноступенчатые на непревышение максимального передаточного отношения
     * Здесь опасное место, [as] приводит к типу небезопасно и может выскочить ошибка,
     * но скорее всего нет, потому что всё завязано на fieldID, которые жёстко закреплены
     * Ещё может быть ошибка, если будет введён какой-то текст и он не сможет
     * преобразоваться к Float, но это маловероятно, у фронта стоит для этого
     * ограничение, которое по идее текст ввести не даст
     */
    private fun checkOneStepU(pScreen: PScreen, nextInputPScreen: InputPScreen) {
        if (((pScreen.fields.find { it.fieldId == 2 }!!.typeSpecificData as InputPictureSpec)
                .additional.answer in 0..3) && (pScreen.fields.find { it.fieldId == 10 }!!
                .typeSpecificData as InputTextSpec).additional.answer!!.toFloat() > 8) {
            nextInputPScreen.changeField(ID = 10, min = 1.6f, max = 8f,
                newHint = "Передаточное отношение одноступенчатого редуктора не может " +
                        "быть больше 8.")
            nextInputPScreen.changeField(ID = 2, newDefault =
            (pScreen.fields.find { it.fieldId == 2 }!!.typeSpecificData as InputPictureSpec)
                .additional.answer)
        }
    }
}