package com.reducetechnologies.reduction.home_screen.ui.calculation

import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.di.CalculationModule
import com.reducetechnologies.di.DaggerCalculationsComponent
import com.reduction_technologies.database.di.GOSTableStorage
import java.lang.IllegalStateException
import javax.inject.Provider

/**
 * Переинициализирует calculation sdk при начале расчета. В остальном делегирует задачи calculation sdk
 * Адаптирует выводы calculation sdk к единому выводу - стек прошедших скринов и текущий
 */
class CalculationSdkHelper(
    private val tableProvider: Provider<GOSTableStorage>
    ) {
    var isActive = false
        private set
    var onSessionStopped: ((CalculationResults) -> Unit)? = null

    private var calculationSdk: CalculationSdk? = null

    private fun reinitCalculationSdk() {
        val calculationModule = CalculationModule(tableProvider.get().obtain())
        val component = DaggerCalculationsComponent.builder().calculationModule(calculationModule).build()
        calculationSdk = CalculationSdkBuilder(component).buildSdk()
}

    private fun reinit() {
        isActive = true
        reinitCalculationSdk()
    }

    private fun finish() {
        onSessionStopped?.invoke(object : CalculationResults {})
        isActive = false
        calculationSdk = null
    }

    // returns the live data for out direction
    fun startCalculation(): CurrentPScreenStatus {
        if (isActive) {
            throw IllegalStateException("isActive, can't reinit")
        }
        reinit()
        val first = calculationSdk!!.init()
        return CurrentPScreenStatus(calculationSdk!!.getAllValidated(), first)
    }

    fun validate(pScreen: PScreen): CurrentPScreenStatus {
        val currentValidated = calculationSdk!!.validateCurrent(pScreen)
        val validated = calculationSdk!!.getAllValidated()
        return CurrentPScreenStatus(validated, currentValidated)
    }

    fun hasNext() : Boolean {
        return calculationSdk!!.hasNextPScreen()
    }

    /**
     * Когда карточка последняя - этот метод уже не должен вызываться, только финиш
     */
    fun next() : CurrentPScreenStatus {
        val next = calculationSdk!!.getNextPScreen()
        return CurrentPScreenStatus(calculationSdk!!.getAllValidated(), next)
    }

    fun finishCalculation() {
        finish()
    }

    fun finalResults() : CalculationResults {
        return calculationSdk!!.finalResults()
    }

    fun getCurrentStatus() : CurrentPScreenStatus {
        return CurrentPScreenStatus(calculationSdk!!.getAllValidated(), calculationSdk!!.currentPending())
    }
}

data class CurrentPScreenStatus(
    val previous: List<WrappedPScreen>,
    val currentPending: WrappedPScreen?
)