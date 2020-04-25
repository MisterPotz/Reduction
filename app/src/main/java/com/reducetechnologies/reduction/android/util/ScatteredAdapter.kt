package com.reducetechnologies.reduction.android.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

interface ScatteredHolderCreator<T> {
    // определяет тип в соответствии с позицкей
    fun getOrientation(position: Int): HolderItemsOrientation

    // для перевода из holderitemsorientation в гугловский viewtype
    fun toViewType(orientation: HolderItemsOrientation): Int

    // для обратного перевода
    fun toOrientation(viewType: Int): HolderItemsOrientation

    // creates view and performs necessary actions on each view before returning
    fun createView(
        viewType: Int,
        parent: ViewGroup,
        inflater: LayoutInflater
    ): Pair<View, ScatteredHolderBindDelegate.Specific?>

    // creates an iterator that traverses the list and returns sub-lists
    fun splitListToPacks(list: List<T>): Iterator<List<T>>
}

/**
 * Adapter for displaying lists with caustom holders for different positions (e.g. material style card lists)
 */
class ScatteredAdapter<T>(
    val lifecycleOwner: LifecycleOwner,
    // values
    private val liveList: LiveData<List<T>>,
    // delegate that knows how to create views based on orientation
    val creator: ScatteredHolderCreator<T>,
    // builder that builds delegates for holders, knows how to rebind holder views to new items
    val holderDelegateBuilder: ScatteredHolderBindDelegate.Builder<T>,
    val recyclerPositionSaver: RecyclerPositionSaver

) : RecyclerView.Adapter<ScatteredItemHolder<T>>(), RecyclerPositionSaveable {

    private lateinit var inflater: LayoutInflater
    private lateinit var context: Context
    private var recyclerView: RecyclerView? = null

    private var itemPacksMap: MutableMap<Int, List<T>> = mutableMapOf()

    private fun cleanMap() {
        itemPacksMap = mutableMapOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScatteredItemHolder<T> {
        val view = creator.createView(viewType, parent, inflater)
        holderDelegateBuilder.setSpecific(view.second)
        return ScatteredItemHolder<T>(
            creator.toOrientation(viewType),
            view.first,
            holderDelegateBuilder
        )
    }

    override fun getItemCount(): Int {
        return itemPacksMap.size
    }

    override fun getItemViewType(position: Int): Int {
        return creator.getOrientation(position).let { creator.toViewType(it) }
    }

    override fun onBindViewHolder(holder: ScatteredItemHolder<T>, position: Int) {
        holder.onBind(itemPacksMap[position]!!)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        context = recyclerView.context
        inflater = LayoutInflater.from(context)
        this.recyclerView = recyclerView
        liveList.observe(lifecycleOwner, Observer {
            cleanMap()
            val iterator = creator.splitListToPacks(it)
            iterator.withIndex().forEach {
                itemPacksMap[it.index] = it.value
            }
            notifyDataSetChanged()
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onSaveState() {
        recyclerView?.layoutManager?.let {
            recyclerPositionSaver.saveState(it)
        }
    }

    override fun restoreState() {
        recyclerView?.layoutManager?.let {
            recyclerPositionSaver.restoreState(it)
        }
    }
}