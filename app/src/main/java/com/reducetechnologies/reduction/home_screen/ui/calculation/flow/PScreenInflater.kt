package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.view.LayoutInflater
import android.view.ViewGroup
import com.reducetechnologies.command_infrastructure.PScreen

/**
 * Manages screens that are shown before / during / after calculation process
 * [container] - view where current fragment is placed
 */
class PScreenInflater(
    val container: ViewGroup
    // to use livedata

) {
    private val inflater = LayoutInflater.from(container.context)
    private var currentPScreen : PScreen? = null

    fun showPScreen(pScreen: PScreen) {

    }

    // fills in input from view
    fun getFilled() : PScreen {
        TODO("Must survey all UI fields that has input and take the input to pscreen - then return in")
    }
}