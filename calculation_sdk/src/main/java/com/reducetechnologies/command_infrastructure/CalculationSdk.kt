package com.reducetechnologies.command_infrastructure

/**
 * Probably must have some initial options
 * Later must have calculationComponent as input.
 * Instructions for each functions declare the behavior of this calculation sdk
 */
interface CalculationSdk {
    /**
     * Initializes inner stack, this must be called at start of each calculation. If calculation already
     * goes, on invoking this method again will return error.
     */
    fun init() : WrappedPScreen

    /**
     * Validates current screen. If given screen is not current - invokes error. If given protoscreen
     * contains some error that are uer-specific - returns alterneated protoscreen to show again to user.
     * User must re input data. After that, protoscreen is again validated
     */
    fun validateCurrent(pscreen : PScreen) : WrappedPScreen?

    /**
     * Get next protoscreen. If current given out is not yet validated - returns error.
     */
    fun getNextPScreen() : WrappedPScreen

    fun hasNextPScreen() : Boolean

    /**
     * Defines if next screen is last (that mmay be needed for some extra logics in visualization)
     */
    fun isNextLast() : Boolean

    fun finalResults() : CalculationResults
}

