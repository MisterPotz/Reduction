package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.reducetechnologies.command_infrastructure.CalculationResults
import com.reducetechnologies.command_infrastructure.CalculationSdk
import com.reducetechnologies.command_infrastructure.PScreen
import com.reducetechnologies.command_infrastructure.WrappedPScreen
import com.reducetechnologies.di.CalculationSdkComponent
import com.reduction_technologies.database.helpers.LiveDataClassStorage
import javax.inject.Provider

/**
 * Вспомогательный класс-делегат при расчетах.
 * Владеет путями распространения обновлений при
 * новых данных от calculation_sdk, и также теми, которые отдал для установки в них значений, для
 * обратной передачи в calculation_sdk.
 * Объект имеет mutable структуру. Но в рамках одного вычисления остается неизменяемой. Пересоздает внутренние
 * объекты для расчетов при новом расчетном запросе
 */
class CalculationSdkHelper(
    private val calculationSdkComponentFactory: Provider<CalculationSdkComponent.Factory>
) {
    var isActive = false
        private set
    private var onSessionStopped: ((CalculationResults) -> Unit)? = null
    private enum class Direction { CHANGES_OUT, CHANGES_IN }

    private  var dispatchedLiveDatas : LiveDataClassStorage<Direction>? = null
    private var outData : MutableLiveData<WrappedPScreen>? = null
    private var inData : MutableLiveData<PScreen>? = null
    private var calculationSdk : CalculationSdk? = null

    private fun getLiveDataStorage() = LiveDataClassStorage<Direction>()

    private fun getOutLiveData() : MutableLiveData<WrappedPScreen>   {
        return dispatchedLiveDatas!!.registerOrReturn<WrappedPScreen>(Direction.CHANGES_OUT)
    }

    private fun getInLiveData() : MutableLiveData<PScreen>   {
        return dispatchedLiveDatas!!.registerOrReturn<PScreen>(Direction.CHANGES_IN)
    }

    private fun reinitLiveDatas() {
        dispatchedLiveDatas = getLiveDataStorage()
        outData = getOutLiveData()
        inData = getInLiveData()
    }

    private fun reinitCalculationSdk() {
        calculationSdk =  calculationSdkComponentFactory.get().build().getBuilder().buildSdk(null)
    }

    private fun reinit() {
        inData?.removeObserver(inObserver)
        reinitLiveDatas()
        reinitCalculationSdk()
    }

    private fun finish() {
        inData!!.removeObserver(inObserver)
        inData = null
        outData = null
        dispatchedLiveDatas = null
        calculationSdk = null
    }

    private val inObserver = Observer<PScreen> {
        TODO("Observer for information coming from user, then passed to sdk")
    }

    fun getDataForOut(): MutableLiveData<WrappedPScreen> = outData!!

    fun getDataForIn() : MutableLiveData<PScreen> = inData!!

    fun setOnSessionStoppedCallback(callback: (CalculationResults) -> Unit) {
        onSessionStopped = callback
    }

    // returns the live data for out direction
    fun startCalculation() : LiveData<WrappedPScreen> {
        outData!!.value = calculationSdk!!.init()
        return outData!!
    }

    private fun validateIncoming(pScreen: PScreen) {
        calculationSdk!!.validateCurrent(pScreen).let {
            if (it != null) {
                // propagating updated value
                outData!!.value = it
            } else {
                tryToFetchNext()
            }
        }
    }

    private fun tryToFetchNext() {
        if (calculationSdk!!.hasNextPScreen()) {
            val next = calculationSdk!!.getNextPScreen()
            if (next.isLast) {
                // fetching results
                val results = calculationSdk!!.finalResults()
                // результаты после получения обрабатываем (строим модели и т.д.)
            }
            outData!!.value = next
        } else {
            // now must finish the process
            finish()
        }
    }
}