package com.reducetechnologies.reduction.home_screen.ui.calculation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import timber.log.Timber

class CalculationFragment : Fragment() {

//    private lateinit var calculationViewModel: CalculationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        calculationViewModel = ViewModelProvider(this).get(CalculationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_calculation, container, false)
        val textView: TextView = root.findViewById(R.id.title)

        SingletoneContextCounter.fragments++
        Timber.i("in onCreateView: current fragment amount: ${SingletoneContextCounter.fragments}")
        return root
    }

    override fun onResume() {
        super.onResume()
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