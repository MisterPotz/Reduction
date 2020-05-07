package com.reducetechnologies.command_infrastructure

class CalculationSdkBuilder() {
    /**
     * variables with necessary parameters
     */

    interface Options { }

    fun buildSdk(options : Options?) : CalculationSdk {
        // some logics
        return CalculationSdkImpl(PScreenSourceDelegate())
    }
}