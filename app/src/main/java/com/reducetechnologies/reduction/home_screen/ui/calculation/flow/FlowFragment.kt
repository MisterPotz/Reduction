package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.reducetechnologies.command_infrastructure.needsInput
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.App
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationSdkCommute
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import com.reduction_technologies.database.di.ApplicationScope
import timber.log.Timber
import javax.inject.Inject

/**
 * Responsible for building protoscreen, and taking argument from it
 */
class FlowFragment() : Fragment(){
    @Inject
    @ApplicationScope
    lateinit var viewModel: SharedViewModel

    private lateinit var controlPrev: Button
    private lateinit var controlNext : Button
    private lateinit var controlEnter :Button
    private lateinit var pScreenManager : PScreenManager
    private lateinit var cardContainer : FrameLayout
    private lateinit var commute: CalculationSdkCommute

    private lateinit var buttonStateDelegate : ButtonStateDelegate

    private fun initCommute() {
        commute = viewModel.getActualCommute()!!
        // TODO прооблема - когда обновится экран - тогда сюда придет новый, да, но в этот момент
        //  экран скорее всего будет старый, так что должен быть какой-то стек. Но тогда это будет
        //  дублированием кода: CalculationSdkImpl уже умеет на самом деле помечать с индексами.
        commute.outData.observe(viewLifecycleOwner, Observer {
            // updating button states
            fetchPrevStatus()
            fetchNeedsInput(it.pScreen.needsInput())
            fetchHasNext(!it.isLast)

            pScreenManager.showPScreen(it.pScreen)
        })
        Timber.i("Commute initialized")
    }

    private fun setupEnterButton() {
        controlEnter.setOnClickListener {
            // filling response
            pScreenManager.getFilled().let {
                commute.inData.value = it
            }
        }
    }

    private fun nextEnterButton() {
        controlNext.setOnClickListener {

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
        controlEnter = controlModule.findViewById(R.id.controlEnter)
        buttonStateDelegate = ButtonStateDelegate(controlPrev, controlNext, controlEnter)

        val main = inflater.inflate(R.layout.split_control_card_layout, container, false).apply {
            val frame = findViewById<FrameLayout>(R.id.controls)
            frame.addView(controlModule)
            cardContainer = findViewById(R.id.card)
        }

        return main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity!!.application as App).appComponent.inject(this)
        pScreenManager = PScreenManager(cardContainer)
        initCommute()
        setupEnterButton()
    }

    private fun fetchPrevStatus() {
        commute.getAllRecent().let {
            buttonStateDelegate.hasPrev(it.isNotEmpty())
        }
    }

    private fun fetchNeedsInput(boolean: Boolean) {
        buttonStateDelegate.mustBeEntered(boolean)
    }

    private fun fetchHasNext(boolean: Boolean) {
        buttonStateDelegate.hasNext(boolean)
    }
}