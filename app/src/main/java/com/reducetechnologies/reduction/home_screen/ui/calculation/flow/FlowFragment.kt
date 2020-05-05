package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.App
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import com.reduction_technologies.database.di.ApplicationScope
import javax.inject.Inject

/**
 * Responsible for building protoscreen, and taking argument from it
 */
class FlowFragment() : Fragment() {
    @Inject
    @ApplicationScope
    lateinit var viewModel: SharedViewModel

    private lateinit var controlPrev: Button
    private lateinit var controlNext: Button
    private lateinit var controlEnter: Button
    private lateinit var cardContainer: FrameLayout
    private lateinit var pScreenSwitcher: PScreenSwitcher
    private lateinit var buttonStateDelegate: ButtonStateDelegate
    private lateinit var pScreenInflater: PScreenInflater

    private fun setupEnterButton() {
        controlEnter.setOnClickListener {
            if (pScreenInflater.getFilled() != null) {
                pScreenSwitcher.enter()
                updateScreen()
            } else {
                Toast.makeText(context, getString(R.string.enter_data), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPrevButton() {
        controlPrev.setOnClickListener {
            pScreenSwitcher.prev()
            updateScreen()
        }
    }

    private fun setupNextButton() {
        controlNext.setOnClickListener {
            pScreenSwitcher.next()
            updateScreen()
        }
    }

    private fun updateScreen() {
        fetchAllButtons()
        pScreenInflater.showPScreen(pScreenSwitcher.current().pScreen, !pScreenSwitcher.currentWasValidatedSuccessfully)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main = inflater.inflate(R.layout.split_control_card_layout, container, false).apply {
            val frame = findViewById<FrameLayout>(R.id.controls)
            createControlView(inflater, frame)
//            frame.addView(controlModule)
        }
        cardContainer = main.findViewById(R.id.card)
        return main
    }

    private fun createControlView(inflater: LayoutInflater, parent: ViewGroup) {
        val controlModule = inflater.inflate(R.layout.calculation_flow_control, parent, true)
        controlPrev = controlModule.findViewById<Button>(R.id.controlPrev)
        controlNext = controlModule.findViewById<Button>(R.id.controlNext)
        controlEnter = controlModule.findViewById(R.id.controlEnter)
        buttonStateDelegate = ButtonStateDelegate(controlPrev, controlNext, controlEnter)
        setupEnterButton()
        setupNextButton()
        setupPrevButton()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity!!.application as App).appComponent.inject(this)
        // obtaining model delegate, responsible for switching screens
        pScreenSwitcher = viewModel.screenSwitcher()!!
        pScreenInflater = PScreenInflater(context!!,cardContainer, activity!!.windowManager)
        updateScreen()

    }

    private fun fetchPrevStatus() {
        buttonStateDelegate.hasPrev(pScreenSwitcher.havePrevious())
    }

    private fun fetchNeedsInput() {
        buttonStateDelegate.mustBeEntered(!pScreenSwitcher.currentWasValidatedSuccessfully)
    }

    private fun fetchHasNext() {
        buttonStateDelegate.hasNext(pScreenSwitcher.haveNext())
    }

    private fun fetchAllButtons() {
        fetchHasNext()
        fetchNeedsInput()
        fetchPrevStatus()
    }
}