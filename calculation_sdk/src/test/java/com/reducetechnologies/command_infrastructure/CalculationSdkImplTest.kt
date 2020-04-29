package com.reducetechnologies.command_infrastructure

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalStateException

internal class CalculationSdkImplTest {

    // contains calculation classes
    internal class TestDelegate() : PScreenSource() {
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

    @Test
    fun nonInitFailt() {
        val calculationSdkImpl = CalculationSdkImpl(TestDelegate())
        var caught = false
        try {
            calculationSdkImpl.getNextPScreen()
        }catch (e : IllegalStateException) {
            caught = true
        }
        // checking that non standard scenarios will be faild instantly
        assertTrue(caught)
    }

    @Test
    fun validateCurrent() {
    }

    @Test
    fun getNextPScreen() {
    }

    @Test
    fun hasNextPScreen() {
    }
}