package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.App
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.*
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.di.ApplicationScope
import timber.log.Timber
import javax.inject.Inject

class EncyclopediaFragment : Fragment() {
    @Inject
    @ApplicationScope
    lateinit var viewModel: SharedViewModel

    private lateinit var recyclerView: RecyclerView
    private var categoryAdapter: CategoriesSimpleAdapter<CommonItem, CategoryCommonItem>? = null

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

        recyclerView = view.findViewById(R.id.categories_list)
        categoryAdapter =
            CategoriesSimpleAdapter<CommonItem, CategoryCommonItem>(
                null,
                CommonItemEncyclopediaViewInflater,
                CommonItemViewBinderFactory,
                { commonItem ->
                    val action =
                        EncyclopediaFragmentDirections.actionNavigationEncyclopediaToItemActivity(
                            commonItem.about!!
                        )
                    findNavController().navigate(action)
                }) {
                if (it.mathTitle != null) {
                    1
                } else 0
            }
        recyclerView.adapter = categoryAdapter!!
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
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
        val saved =
            SimplePositionSaver(
                recyclerView.layoutManager?.onSaveInstanceState(),
                categoryAdapter?.onSave()
                    ?: PositionsSaver(
                        SparseArray()
                    )
            )
        viewModel.savedEncyclopediaScreenState = saved

        Timber.i("in onPause: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAllSortedItems().observe(viewLifecycleOwner, Observer {
            Timber.i("Got sorted results, map : ${it.size}")
            val list = it.toSortedMap(Comparator { o1, o2 ->
                o1.getPosition() - o2.getPosition()
            }).map {
                CategoryCommonItem(
                    it.key.getPosition(),
                    viewModel.mapCategoryToLocal(it.key),
                    it.value
                )
            }
            categoryAdapter!!.setList(list)
        })
        viewModel.getFavorites().observe(viewLifecycleOwner, Observer {
            Timber.i("Have favorite items DB, size: ${it.size}")
        })
        Timber.i("in onStart: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("in onResume: current fragment amount: ${SingletoneContextCounter.fragments}")
        val savedState = viewModel.savedEncyclopediaScreenState
        savedState?.let { saver ->
            categoryAdapter!!.onRestore(saver.inner)
            saver.outer?.let {
                recyclerView.layoutManager?.onRestoreInstanceState(it)
            }
        }
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