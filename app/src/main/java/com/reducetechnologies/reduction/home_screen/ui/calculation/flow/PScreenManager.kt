package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationSdkCommute
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import timber.log.Timber

/**
 * Manages screens that are shown before / during / after calculation process
 * [container] - view where current fragment is placed
 */
class PScreenManager(
    val container: ViewGroup,
    // to use livedata
    val lifecycleOwner: LifecycleOwner,
    val fragmentManager: FragmentManager,
    val viewModel: SharedViewModel

) {
    private val inflater = LayoutInflater.from(container.context)

    init {

        Timber.i("PScreenManager recreated")
        val commute = viewModel.getActualCommute()
        setActualView(commute)
    }

    private fun setActualView(commute: CalculationSdkCommute?) {
        if (commute == null) {
            // no calculation happenning, show default screen
        } else {
            // observing out data, must contain the latest protoscreen
            commute.outData.observe(lifecycleOwner, Observer {
                // using someone to build view of protoscreen
                Timber.i("Got latest pScreen: $it")
                // and then pass it to fragment that is responsible for displaying it
            })
        }
    }

    fun startCalculation() {
        viewModel.startCalculation().apply {
            setActualView(this)
        }
    }
}