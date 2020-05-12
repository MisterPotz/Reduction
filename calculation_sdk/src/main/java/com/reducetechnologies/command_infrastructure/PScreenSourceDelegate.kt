package com.reducetechnologies.command_infrastructure

import com.reducetechnologies.command_infrastructure.p_screens.InputPScreen
import com.reducetechnologies.command_infrastructure.p_screens.StandbyPScreen
import com.reducetechnologies.di.CalculationsComponent

// contains calculation classes
internal class PScreenSourceDelegate() : PScreenSource() {
    // simple stack that is consumed. Being build with some other classes (or with calculation on flow)
    override protected val preparedStack: MutableList<PScreen> = mutableListOf(
        InputPScreen.getPScreen(),
        StandbyPScreen.getPScreen()
    )

    override fun isNextLast(): Boolean {
        return preparedStack.size == 1
    }

    override fun validate(pScreen: PScreen) : WrappedPScreen? {
        // логика проверки
        // если все норм возвратит нулл
        return null
    }
}