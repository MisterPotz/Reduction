package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.App
import com.reducetechnologies.reduction.android.util.common_item_category_adapter.CategoriesAdapterCommon
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import com.reduction_technologies.database.di.ApplicationScope
import timber.log.Timber
import javax.inject.Inject

class EncyclopediaFragment : Fragment() {
    @Inject
    @ApplicationScope
    lateinit var viewModel: SharedViewModel

    private lateinit var recyclerView: RecyclerView
    private var categoriesAdapterCommon : CategoriesAdapterCommon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
        (activity!!.application as App).appComponent.inject(this)

        val textView: TextView = view.findViewById(R.id.text_home)

        viewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        recyclerView = view.findViewById(R.id.categories_list)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.encyclopedia_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController()
                    .navigate(R.id.action_navigation_encyclopedia_to_settingsFragment)
                true
            }
            else -> false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val debug = Timber.DebugTree()
        Timber.plant(debug)
        Timber.i("onSaveInstanceState called in fragment")
        Timber.uproot(debug)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onPause() {
        super.onPause()
        Timber.i("saving in onPause")
        categoriesAdapterCommon?.onSaveState()

        Timber.i("in onPause: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onStart() {
        super.onStart()
        categoriesAdapterCommon =
            CategoriesAdapterCommon(viewModel.getAllSortedItems(), lifecycleOwner = viewLifecycleOwner, positionSaver = viewModel.getSavedLayoutPositions())
        Timber.i("recyclerView = $recyclerView")
        recyclerView.apply {
            adapter = categoriesAdapterCommon
            layoutManager = LinearLayoutManager(this@EncyclopediaFragment.context)
        }
        Timber.i("in onStart: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("in onResume: current fragment amount: ${SingletoneContextCounter.fragments}")
        categoriesAdapterCommon!!.restoreState()
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