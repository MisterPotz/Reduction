package com.reducetechnologies.miniTests

import com.reducetechnologies.command_infrastructure.InputPictureSpec
import com.reducetechnologies.command_infrastructure.InputTextSpec
import com.reducetechnologies.command_infrastructure.PScreen
import com.reducetechnologies.command_infrastructure.p_screens.InputPScreen
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PScreenSourceDelegateTest {

    @Test
    fun validate(pScreen: PScreen) : PScreen? {
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

    @Test
    fun test_Validate() {
        val pScreen = InputPScreen()
        validate(pScreen.getPScreen())
    }

    fun checkOneStepU(pScreen: PScreen, nextInputPScreen: InputPScreen) {
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