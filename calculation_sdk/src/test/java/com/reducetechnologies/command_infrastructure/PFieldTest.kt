package com.reducetechnologies.command_infrastructure

import com.reduction_technologies.database.tables_utils.OneSidedDomain
import com.reduction_technologies.database.tables_utils.TwoSidedDomain
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PFieldTest {
    val textPField = PField(PFieldType.TEXT, TextSpec(text = "Test", additional = AdditionalText(TextType.BODY)), 0)
    val expectedText = "{\"pFieldType\":\"TEXT\",\"typeSpecificData\":{\"text\":\"Test\",\"additional\":{\"type\":\"BODY\"}},\"fieldId\":0}"

    val pictureField = PField(PFieldType.PICTURE, PictureSpec(PictureSourceType.PATH, PictureStringPath("some_path")), 1)
    val pictureString = "{\"pFieldType\":\"PICTURE\",\"typeSpecificData\":{\"pictureSourceType\":\"PATH\",\"source\":{\"string\":\"some_path\"}},\"fieldId\":1}"

    val inputText =  PField(
        pFieldType = PFieldType.INPUT_TEXT,
        typeSpecificData = InputTextSpec(
            title = "Тест",
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
    val inputTextString = "{\"pFieldType\":\"INPUT_TEXT\",\"typeSpecificData\":{\"title\":\"Тест\",\"type\":\"FLOAT\",\"additional\":{\"domain\":{\"leftSide\":{\"conditionSign\":\"\\u003e\",\"num\":0.0},\"rightSide\":{\"conditionSign\":\"\\u003c\",\"num\":1000000.0}}}},\"fieldId\":4}"

    val pScreen = PScreen(
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

    val pScreenString = "{\"title\":\"Тестовая вторая страничка\",\"fields\":[{\"pFieldType\":\"INPUT_TEXT\",\"typeSpecificData\":{\"title\":\"Коэффициент поляризации\",\"type\":\"FLOAT\",\"default\":\"\",\"additional\":{\"hint\":\"че нить такое\"}},\"fieldId\":1},{\"pFieldType\":\"TEXT\",\"typeSpecificData\":{\"text\":\"Ищщо дичи\",\"additional\":{\"type\":\"BODY\"}},\"fieldId\":5},{\"pFieldType\":\"PICTURE\",\"typeSpecificData\":{\"pictureSourceType\":\"PATH\",\"source\":{\"string\":\"input_pictures/1.jpg\"}},\"fieldId\":6},{\"pFieldType\":\"PICTURE\",\"typeSpecificData\":{\"pictureSourceType\":\"PATH\",\"source\":{\"string\":\"input_pictures/10.jpg\"}},\"fieldId\":6},{\"pFieldType\":\"INPUT_LIST\",\"typeSpecificData\":{\"title\":\"Будет ли закалка\",\"additional\":{\"options\":[\"Да\",\"Нет\",\"Не знаю что такое, сам разберись\"]}},\"fieldId\":7},{\"pFieldType\":\"MATH_TEXT\",\"typeSpecificData\":{\"text\":\"some normal text with formula -\\u003e  \$\$ \\\\newline S_{fb} \\u003d \\\\bigg(\\\\cfrac{K_L} { K_T K_R } \\\\bigg) \\\\cdot S_{fb}^{\\u0027}  \$\$\",\"mathTextField\":{\"stub\":0}},\"fieldId\":8}]}"

    @Test
    fun parsingTextToJson() {
        val gson = PField.makeGson()
        val string = gson.toJson(textPField)
        assertEquals(expectedText, string)
    }

    @Test
    fun unparsingJsonToText() {
        val gson = PField.makeGson()
        val string = expectedText
        assertEquals(textPField, gson.fromJson(string, PField::class.java))
    }

    @Test
    fun parsingPictueToJson() {
        val gson = PField.makeGson()
        val string = gson.toJson(pictureField)
        assertEquals(pictureString, string)
    }

    @Test
    fun unparsingStringToPicture() {
        val gson = PField.makeGson()
        val pfield = gson.fromJson(pictureString, PField::class.java)
        assertEquals(pictureField, pfield)
    }

    @Test
    fun parsingInputTextToJson() {
        val gson = PField.makeGson()
        val string = gson.toJson(inputText)
        assertEquals(inputTextString, string)
    }

    @Test
    fun unparsingStringToInputText() {
        val gson = PField.makeGson()
        val pfield = gson.fromJson(inputTextString, PField::class.java)
        assertEquals(inputText, pfield)
    }

    @Test
    fun parsingPScreenToJson() {
        val gson = PField.makeGson()
        val string = gson.toJson(pScreen)
        assertEquals(pScreenString, string)
    }

    @Test
    fun unparsingStringToPScreen() {
        val gson = PField.makeGson()
        val pscreen = gson.fromJson(pScreenString, PScreen::class.java)
        assertEquals(this.pScreen, pscreen)
    }
}