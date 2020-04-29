package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.lifecycle.MutableLiveData
import com.reducetechnologies.command_infrastructure.CalculationResults
import com.reducetechnologies.command_infrastructure.CalculationSdk
import com.reducetechnologies.command_infrastructure.PScreen
import com.reduction_technologies.database.helpers.LiveDataClassStorage

/**
 * Вспомогательный класс-делегат при расчетах. Владеет путями распространения обновлений при
 * новых данных от calculation_sdk, и также теми, которые отдал для установки в них значений, для
 * обратной передачи calculation_sdk
 */
class CalculationSdkHelper(private val calculationSdk: CalculationSdk) {
    var isActive = false
        private set
    private var onSessionStopped: ((CalculationResults) -> Unit)? = null


    private enum class Direction { CHANGES_OUT, CHANGES_IN }

    private val dispatchedLiveDatas by lazy { LiveDataClassStorage<Direction>() }

    private val outData by lazy {
        dispatchedLiveDatas.registerOrReturn<PScreen>(Direction.CHANGES_OUT)
    }

    private val inData by lazy {
        dispatchedLiveDatas.registerOrReturn<PScreen>(Direction.CHANGES_IN)
    }

    fun getDataForOut(): MutableLiveData<PScreen> = outData

    fun getDataForIn() = inData

    fun setOnSessionStoppedCallback(callback: (CalculationResults) -> Unit) {
        onSessionStopped = callback
    }
}