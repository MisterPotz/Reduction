package com.reducetechnologies.reduction.home_screen.ui.calculation

import androidx.lifecycle.LiveData
import com.reducetechnologies.command_infrastructure.PScreen
import com.reducetechnologies.command_infrastructure.WrappedPScreen

abstract class CalculationSdkCommute(val inData: LiveData<PScreen>, val outData: LiveData<WrappedPScreen>) {
    /**
     * gets all recent validated items - may need in case user wants to go to previous screen
     */
    abstract fun getAllRecent() : List<WrappedPScreen>
}