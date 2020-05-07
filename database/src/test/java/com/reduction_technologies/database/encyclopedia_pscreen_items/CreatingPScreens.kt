package com.reduction_technologies.database.encyclopedia_pscreen_items

import com.reducetechnologies.command_infrastructure.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class CreatingPScreens {
    val gson = PField.makeGson()
    @Test
    fun alpha(){
        val pscreen = PScreen("", fields = listOf(
            PField(PFieldType.MATH_TEXT, MathTextSpec("""\alpha"""), 0),
            PField(PFieldType.TEXT, TextSpec("Угол профиля зуба исходного контура в нормальном сечении"), 1)
        ))
        val string = gson.toJson(pscreen)
        assertTrue(string.isNotEmpty())
    }

    @Test
    fun fatigue() {
        val pscreen = PScreen("", fields = listOf(
            PField(PFieldType.TEXT, TextSpec("""Cвойство материала оказывать сопротивление изнашиванию в определённых условиях трения, оцениваемое величиной, обратной скорости изнашивания или интенсивности изнашивания."""
            ), 0)
        ))
        val string = gson.toJson(pscreen)
        assertTrue(string.isNotEmpty())
    }

    @Test
    fun g0() {
        val pscreen = PScreen("", fields = listOf(
            PField(PFieldType.MATH_TEXT, MathTextSpec("""g_0"""), 0),
            PField(PFieldType.TEXT, TextSpec("""Коэффициент, учитывающий влияние разности шагов зацепления зубьев шестерни и колеса."""
            ), 1)
        ))
        val string = gson.toJson(pscreen)
        assertTrue(string.isNotEmpty())
    }

    @Test
    fun source() {
        val pscreen = PScreen("Исходные параметры", fields = listOf(
            PField(PFieldType.TEXT, TextSpec("""Используется как источник начальных параметров, зависимых от ввода."""
            ), 0)
        ))
        val string = gson.toJson(pscreen)
        assertTrue(string.isNotEmpty())
    }

    @Test
    fun ra40() {
        val pscreen = PScreen("Ряд RA40", fields = listOf(
            PField(PFieldType.TEXT, TextSpec("""Нормальные линейные размеры.""""
            ), 0)
        ))
        val string = gson.toJson(pscreen)
        assertTrue(string.isNotEmpty())
    }

    @Test
    fun modules() {
        val pscreen = PScreen("Ряд стандартных модулей", fields = listOf(
            PField(PFieldType.TEXT, TextSpec("""Нормальные модули, таблица используется при расчетах зубчатых колёс""""
            ), 0)
        ))
        val string = gson.toJson(pscreen)
        assertTrue(string.isNotEmpty())
    }
}