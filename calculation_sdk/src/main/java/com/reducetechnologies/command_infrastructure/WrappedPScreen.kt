package com.reducetechnologies.command_infrastructure

/**
 * Contains the data and some necessary info for it
 */
data class WrappedPScreen(val pScreen: PScreen, var isLast: Boolean, val index: Int)