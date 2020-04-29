package com.reducetechnologies.command_infrastructure

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalStateException

internal val floatErrorMessage = "filled not float"

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
                    )
                )
            ),
            PScreen(
                "Введите данные",
                mutableListOf(
                    PField(
                        PFieldType.INPUT_TEXT, InputTextSpec(
                            "Допустимая нагрузка",
                            InputTextType.FLOAT, "0.4", AdditionalInputText()
                        ), 1
                    )
                )
            )
        )

        override fun validate(pScreen: PScreen): PScreen? {
            var isGood = true
            // логика проверки
            for (i in pScreen.fields) {
                when (i.pFieldType) {
                    PFieldType.INPUT_TEXT -> (i.typeSpecificData as InputTextSpec).additional.answer!!.toFloatOrNull()
                        .run {
                            if (this == null) {
                                (i.typeSpecificData as InputTextSpec).additional.error =
                                    floatErrorMessage
                                isGood = false
                            }
                        }
                    PFieldType.INPUT_LIST -> (i.typeSpecificData as InputListSpec).additional.answer?.run {
                    }
                    else -> Unit
                }
            }
            // если все норм возвратит нулл
            return if (isGood) null else pScreen
        }
    }

    @Test
    fun nonInitFailed() {
        val calculationSdkImpl = CalculationSdkImpl(TestDelegate())
        var caught = false
        try {
            calculationSdkImpl.getNextPScreen()
        } catch (e: IllegalStateException) {
            caught = true
        }
        // checking that non standard scenarios will be faild instantly
        assertTrue(caught)
    }

    @Test
    fun validateCurrentWithReturn() {
        val calculationSdkImpl = CalculationSdkImpl(TestDelegate())
        val pScreen =  calculationSdkImpl.init()

       ( pScreen.fields[0].typeSpecificData as InputTextSpec).additional.answer = "asd"
        val revalidate = calculationSdkImpl.validateCurrent(pScreen)
        assertTrue(revalidate != null)
        assertEquals(pScreen, revalidate)
    }

    @Test
    fun validateCurrentWithNoReturn() {
        val calculationSdkImpl = CalculationSdkImpl(TestDelegate())
        val pScreen =  calculationSdkImpl.init()

        ( pScreen.fields[0].typeSpecificData as InputTextSpec).additional.answer = "0.4"
        val revalidate = calculationSdkImpl.validateCurrent(pScreen)
        assertTrue(revalidate == null)
    }

    @Test
    fun getNextPScreenWithoutValidating() {
        val calculationSdkImpl = CalculationSdkImpl(TestDelegate())
        val screen = calculationSdkImpl.init()

        var caught = false
        try {
            calculationSdkImpl.getNextPScreen()
        } catch (e: IllegalStateException) {
            caught = true
        }
        // checking that non standard scenarios will be faild instantly
        assertTrue(caught)
    }

    @Test
    fun getNextPScreenWithValidating() {
        val calculationSdkImpl = CalculationSdkImpl(TestDelegate())
        val screen = calculationSdkImpl.init()
        (screen.fields[0].typeSpecificData as InputTextSpec).additional.answer = "0.4"

        // check that currently stack is empty because waits for screen to be returned
        assertTrue(!calculationSdkImpl.hasNextPScreen())
        calculationSdkImpl.validateCurrent(screen)

        assertTrue(calculationSdkImpl.hasNextPScreen())
        val newScreen = calculationSdkImpl.getNextPScreen()
        assertNotEquals(screen, newScreen)
    }
}