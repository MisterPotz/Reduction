package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import kotlinx.android.synthetic.main.apptoolbar.*
import timber.log.Timber

class EncyclopediaFragment : Fragment() {
    private lateinit var encyclopediaViewModel: EncyclopediaViewModel
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_encyclopedia_main, container, false)

        SingletoneContextCounter.fragments++
        Timber.i("in onCreateView: current fragment amount: ${SingletoneContextCounter.fragments}")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = view.findViewById(R.id.text_home)

        encyclopediaViewModel =
            ViewModelProvider(this).get(EncyclopediaViewModel::class.java)
        encyclopediaViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        categoriesAdapter = CategoriesAdapter()
        encyclopediaViewModel.categoriesListLD().observe(viewLifecycleOwner, Observer {
            Timber.i("Data set updated")
            categoriesAdapter.list = it
            categoriesAdapter.notifyDataSetChanged()
        })
        categoriesAdapter.list = encyclopediaViewModel.categoriesListLD().value!!

        view.findViewById<RecyclerView>(R.id.categories_list).apply {
            adapter = categoriesAdapter
            layoutManager = LinearLayoutManager(this@EncyclopediaFragment.context)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onPause() {
        super.onPause()
        Timber.i("in onPause: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("in onResume: current fragment amount: ${SingletoneContextCounter.fragments}")

    }

    override fun onStart() {
        super.onStart()
        Timber.i("in onStart: current fragment amount: ${SingletoneContextCounter.fragments}")

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