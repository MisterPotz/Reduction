package com.reducetechnologies.reduction.home_screen.ui.calculation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import com.reducetechnologies.reduction.home_screen.ui.favorites.WithOwnNavController
import timber.log.Timber

class CalculationFragment : WithOwnNavController() {
    override fun getNavController(): NavController {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNavFragment(): NavHostFragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var calculationViewModel: CalculationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        calculationViewModel = ViewModelProvider(this).get(CalculationViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_calculation, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        calculationViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        SingletoneContextCounter.fragments++
        Timber.i("in onCreateView: current fragment amount: ${SingletoneContextCounter.fragments}")
        return root
    }

    override fun onPause() {
        super.onPause()
        Timber.i("in onPause: current fragment amount: ${SingletoneContextCounter.fragments}")
    }
}