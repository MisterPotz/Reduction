package com.reducetechnologies.command_infrastructure

import com.reducetechnologies.di.CalculationsComponent

class CalculationSdkBuilder(val calculationsComponent: CalculationsComponent) {
    fun buildSdk() : CalculationSdk {
        // some logics
        return CalculationSdkImpl(PScreenSourceDelegate(calculationsComponent = calculationsComponent))
    }
}