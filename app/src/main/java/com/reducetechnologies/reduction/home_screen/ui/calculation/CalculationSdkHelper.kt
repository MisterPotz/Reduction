package com.reducetechnologies.reduction.home_screen.ui.calculation

import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.di.CalculationModule
import com.reducetechnologies.di.DaggerCalculationsComponent
import com.reduction_technologies.database.di.GOSTableStorage
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Provider

typealias CalculationFinishCallback = (CalculationResults) -> Unit

/**
 * Переинициализирует calculation sdk при начале расчета. В остальном делегирует задачи calculation sdk
 * Адаптирует выводы calculation sdk к единому выводу - стек прошедших скринов и текущий
 */
class CalculationSdkHelper(
    private val tableProvider: Provider<GOSTableStorage>
) {
    var isActive = false
        private set
    private var onSessionStopped: MutableList<(CalculationResults) -> Unit> = mutableListOf()

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
        isActive = false
        calculationSdk = null
        cleanCallbacks()
    }

    fun finishWithError(error: Error) {
        Timber.i("Helper finishing with error")
        if (!isActive) {
            throw IllegalStateException("Can't finish what's already finished")
        }
        onSessionStopped.forEach { it.invoke(error) }
        cleanCallbacks()
        isActive = false
        calculationSdk = null
    }

    private fun pullCallbacks() {
        onSessionStopped.forEach { it(calculationSdk!!.finalResults()) }
    }

    private fun cleanCallbacks() {
        onSessionStopped.clear()
    }

    // calls given callback when calculation is finished
    fun startCalculation(onSessionStopped: ((CalculationResults) -> Unit)): CurrentPScreenStatus {
        if (isActive) {
            throw IllegalStateException("isActive, can't reinit")
        }
        this.onSessionStopped.add(onSessionStopped)
        reinit()
        val first = calculationSdk!!.init()
        return CurrentPScreenStatus(calculationSdk!!.getAllValidated(), first)
    }

    suspend fun validate(pScreen: PScreen): CurrentPScreenStatus {
        return withContext(Dispatchers.Default) {
            // ЗДЕСЬ ЗАПУСКАЕТСЯ В КАКОЙ-ТО МОМЕНТ РАСЧЕТ
            val currentValidated = calculationSdk!!.validateCurrent(pScreen)
            val validated = calculationSdk!!.getAllValidated()
            CurrentPScreenStatus(validated, currentValidated)
        }
    }

    fun hasNext(): Boolean {
        return calculationSdk!!.hasNextPScreen()
    }

    fun addCallback(onSessionStopped: (CalculationResults) -> Unit) {
        this.onSessionStopped.add(onSessionStopped)
    }

    /**
     * Когда карточка последняя - этот метод уже не должен вызываться, только финиш
     */
    suspend fun next(): CurrentPScreenStatus {
        // as calculating screens currently is CPU limited, default dispatcher is used
        return withContext(Dispatchers.Default) {
            val previous = calculationSdk!!.getAllValidated()
            if (calculationSdk!!.isFinished()) {
                val results = calculationSdk!!.finalResults()
                results as CalculationResultsContainer
                val status = results.reducersDataList.size > 0
                Timber.i("Calculation is finished: status - $status")
                if (status) {
                    pullCallbacks()
                    cleanCallbacks()
                } else {
                    finishWithError(CalculationNotPossible)
                }
                CurrentPScreenStatus(previous, null)
            } else {
                val next = calculationSdk!!.getNextPScreen()
                CurrentPScreenStatus(previous, next)
            }
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