package com.reducetechnologies.reduction.home_screen.ui.calculation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.reducetechnologies.command_infrastructure.CalculationResults
import com.reducetechnologies.command_infrastructure.CalculationSdk
import com.reducetechnologies.command_infrastructure.PScreen
import com.reducetechnologies.command_infrastructure.WrappedPScreen
import com.reducetechnologies.di.CalculationSdkComponent
import com.reduction_technologies.database.helpers.LiveDataClassStorage
import java.lang.IllegalStateException
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
    var onSessionStopped: ((CalculationResults) -> Unit)? = null
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
        inData!!.observeForever(inObserver)
    }

    private fun reinitCalculationSdk() {
        calculationSdk =  calculationSdkComponentFactory.get().build().getBuilder().buildSdk(null)
    }

    private fun reinit() {
        isActive = true
        inData?.removeObserver(inObserver)
        reinitLiveDatas()
        reinitCalculationSdk()
    }

    private fun finish() {
        onSessionStopped?.invoke(object : CalculationResults { })
        inData!!.removeObserver(inObserver)
        isActive = false
        inData = null
        outData = null
        dispatchedLiveDatas = null
        calculationSdk = null
    }

    private val inObserver = Observer<PScreen> {
        validateIncoming(it)
    }

    fun getDataForOut(): MutableLiveData<WrappedPScreen> = outData!!

    fun getDataForIn() : MutableLiveData<PScreen> = inData!!

    // returns the live data for out direction
    fun startCalculation() : CalculationSdkCommute{
        if (isActive) {
            throw IllegalStateException("isActive, can't reinit")
        }
        reinit()
        outData!!.value = calculationSdk!!.init()
        return getCommuteIfActive()!!
    }

    fun getCommuteIfActive() : CalculationSdkCommute? {
        if (isActive) {
            return object : CalculationSdkCommute(inData!!, outData!!) {
                override fun getAllRecent(): List<WrappedPScreen> {
                    return calculationSdk!!.getAllValidated()
                }
            }
        } else
            return null
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