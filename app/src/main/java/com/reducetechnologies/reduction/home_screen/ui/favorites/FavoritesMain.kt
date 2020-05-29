package com.reducetechnologies.reduction.home_screen.ui.favorites

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.command_infrastructure.CalculationResultsContainer
import com.reducetechnologies.command_infrastructure.PField.Companion.gson
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.App
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import com.reduction_technologies.database.databases_utils.Tags
import com.reduction_technologies.database.di.ApplicationScope
import timber.log.Timber
import javax.inject.Inject

class FavoritesMain : Fragment() {
    @Inject
    @ApplicationScope
    lateinit var viewModel: SharedViewModel

    private lateinit var recyclerView: RecyclerView
    private var favoritesAdapter: FavoritesAdapter? = null

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
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity!!.application as App).appComponent.inject(this)

        recyclerView = view.findViewById(R.id.categories_list)

        favoritesAdapter = FavoritesAdapter(LayoutInflater.from(context), FavoriteViewInflater(
            LayoutInflater.from(view.context)), ViewTypeSorter(), FavoriteViewBinderFactory
        ) {
            Timber.i("Opening activity for item: ${it.title}")
            if (it.tag == Tags.RESULT.item) {
                // TODO для экономии времени здесь сохраненный результат передается как один, хотя в дебе хранится больше. Потом можно полный спектр сделать
                val container = gson.fromJson<CalculationResultsContainer>(it.additional, CalculationResultsContainer::class.java)
                val firstReducerData = gson.toJson(container.reducersDataList[0])
                val action = FavoritesMainDirections.actionFavoritesToResultsActivity2(firstReducerData)
                findNavController().navigate(action)
            }
        }

        recyclerView.adapter = favoritesAdapter!!
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val debug = Timber.DebugTree()
        Timber.plant(debug)
        Timber.i("onSaveInstanceState called in fragment")
        Timber.uproot(debug)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getFavorites().observe(viewLifecycleOwner, Observer {
            Timber.i("Have favorite items DB, size: ${it.size}")
            favoritesAdapter?.setList(it)
        })
    }
}
