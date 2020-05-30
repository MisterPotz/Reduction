package com.reducetechnologies.reduction.home_screen.ui.calculation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.reducetechnologies.command_infrastructure.CalculationResults
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.App
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import com.reduction_technologies.database.di.ApplicationScope
import kotlinx.android.synthetic.main.fragment_calculation.*
import timber.log.Timber
import javax.inject.Inject

class CalculationFragment : Fragment() {
    @Inject
    @ApplicationScope
    lateinit var viewModel : SharedViewModel

    private lateinit var calculationContainer : FrameLayout

    init {
        Timber.i("CalculationFragment recreated")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        calculationViewModel = ViewModelProvider(this).get(CalculationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_calculation, container, false)

        SingletoneContextCounter.fragments++
        Timber.i("in onCreateView: current fragment amount: ${SingletoneContextCounter.fragments}")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity!!.application as App).appComponent.inject(this)

        registerCalculationButton()
    }

    private fun updateButton() {
        calcButton.text = if (viewModel.isCalculationActive()) getString(R.string.continue_calculation) else getString(
                    R.string.new_calculation)
    }

    private fun registerCalculationButton() {
        calcButton.setOnClickListener {
            val action = CalculationFragmentDirections.actionCalculationFragmentToFlowFragment()
            viewModel.startCalculation()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        updateButton()
        Timber.i("in onResume: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onStart() {
        super.onStart()
        Timber.i("in onStart: current fragment amount: ${SingletoneContextCounter.fragments}")

    }

    override fun onPause() {
        super.onPause()
        Timber.i("in onPause: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("in onStop: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SingletoneContextCounter.fragments--
        Timber.i("in onDestroyView: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("in onDestroy: current fragment amount: ${SingletoneContextCounter.fragments}")
    }
}