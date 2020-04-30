package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.App
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import com.reduction_technologies.database.di.ApplicationScope
import timber.log.Timber
import javax.inject.Inject

/**
 * Contains control flow and shows protoscreen cards
 */
class FlowFragment() : Fragment(){
    @Inject
    @ApplicationScope
    lateinit var viewModel: SharedViewModel

    private lateinit var controlPrev: Button
    private lateinit var controlNext : Button
    private lateinit var pScreenManager : PScreenManager
    private lateinit var cardContainer : FrameLayout

    private fun fetchCurrentPScreen() {
        val commute = viewModel.getActualCommute()
        if (commute == null) {
            Timber.w("Can't display any commute because none is given")
        } else {
            commute.inData
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val controlModule = inflater.inflate(R.layout.calculation_flow_control, container, false)
        controlPrev = controlModule.findViewById<Button>(R.id.controlPrev)
        controlNext = controlModule.findViewById<Button>(R.id.controlNext)

        val main = inflater.inflate(R.layout.split_control_card_layout, container, false).apply {
            val frame = findViewById<FrameLayout>(R.id.controls)
            frame.addView(controlModule)
            cardContainer = findViewById(R.id.card)
        }

        return main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity!!.application as App).appComponent.inject(this)
        pScreenManager = PScreenManager(cardContainer, viewLifecycleOwner, childFragmentManager, viewModel)

    }
}