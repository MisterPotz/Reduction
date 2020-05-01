package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.reducetechnologies.command_infrastructure.PScreen
import com.reducetechnologies.command_infrastructure.needsInput
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationSdkCommute
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import timber.log.Timber

/**
 * Manages screens that are shown before / during / after calculation process
 * [container] - view where current fragment is placed
 */
class PScreenManager(
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