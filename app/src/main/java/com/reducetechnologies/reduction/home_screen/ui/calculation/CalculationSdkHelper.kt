package com.reducetechnologies.reduction.home_screen.ui.calculation

import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.di.CalculationModule
import com.reducetechnologies.di.DaggerCalculationsComponent
import com.reduction_technologies.database.di.GOSTableStorage
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import javax.inject.Provider

typealias CalculationFinishCallback = (CalculationResults) -> Unit

/**
 * Переинициализирует calculation sdk при начале расчета. В остальном делегирует задачи calculation sdk
 * Адаптирует выводы calculation sdk к единому выводу - стек прошедших скринов и текущий
 */
class CalculationSdkHelper(
    private val tableProvider: Provider<GOSTableStorage>
)  {
    var isActive = false
        private set
    private var onSessionStopped: ((CalculationResults) -> Unit)? = null

    // backend entry point
    private var calculationSdk: CalculationSdk? = null

    private fun reinitCalculationSdk() {
        val calculationModule = CalculationModule(tableProvider.get().obtain())
        val component =
            DaggerCalculationsComponent.builder().calculationModule(calculationModule).build()
        calculationSdk = CalculationSdkBuilder(component).buildSdk()
    }

    private fun reinit() {
        isActive = true
        reinitCalculationSdk()
    }

    private fun finish() {
        onSessionStopped = null
        isActive = false
        calculationSdk = null
        onSessionStopped = null
    }

    private fun pullCallbacks() {
        onSessionStopped?.invoke(calculationSdk!!.finalResults())
    }

    // calls given callback when calculation is finished
    fun startCalculation(onSessionStopped: ((CalculationResults) -> Unit)): CurrentPScreenStatus {
        if (isActive) {
            throw IllegalStateException("isActive, can't reinit")
        }
        this.onSessionStopped = onSessionStopped
        reinit()
        val first = calculationSdk!!.init()
        return CurrentPScreenStatus(calculationSdk!!.getAllValidated(), first)
    }

    suspend fun validate(pScreen: PScreen): CurrentPScreenStatus {
        return withContext(Dispatchers.Default) {
            val currentValidated = calculationSdk!!.validateCurrent(pScreen)
            val validated = calculationSdk!!.getAllValidated()
            CurrentPScreenStatus(validated, currentValidated)
        }
    }

    fun hasNext(): Boolean {
        return calculationSdk!!.hasNextPScreen()
    }

    /**
     * Когда карточка последняя - этот метод уже не должен вызываться, только финиш
     */
    suspend fun next(): CurrentPScreenStatus {
        // as calculating screens currently is CPU limited, default dispatcher is used
        return withContext(Dispatchers.Default) {
            val next = calculationSdk!!.getNextPScreen()
            // pull callbacks on finish
            if (calculationSdk!!.isFinished()) {
                pullCallbacks()
            }
            val status = CurrentPScreenStatus(calculationSdk!!.getAllValidated(), next)
            status
        }
    }

    fun getCurrentStatus(): CurrentPScreenStatus {
        return CurrentPScreenStatus(
            calculationSdk!!.getAllValidated(),
            calculationSdk!!.currentPending()
        )
    }
}

data class CurrentPScreenStatus(
    val previous: List<WrappedPScreen>,
    val currentPending: WrappedPScreen?
)