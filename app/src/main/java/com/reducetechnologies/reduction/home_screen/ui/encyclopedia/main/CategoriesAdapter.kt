package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.*
import com.reduction_technologies.database.utils.Positionable
import timber.log.Timber

abstract class CategoriesInfoDelegate<T> {
    abstract val tags: List<String>
    abstract val sorting: (T) -> Int

    val categoriesAmount
        get() = tags.size
}

/**
 * Получает лайв датные списки итемов, разбитые в мапе по ключу R
 */
open class CategoriesAdapter<Tag : Positionable, T>(
    val liveData: LiveData<Map<Tag, List<T>>>,
    val lifecycleOwner: LifecycleOwner,
    val delegate: CategoriesInfoDelegate<T>,
    val tagHolderCreator: ScatteredHolderCreator<T>,
    val itemHolderCreatorBuilder: ScatteredHolderBindDelegate.Builder<T>,
    val categoryAdapterPositionSaver: CategoryAdapterPositionSaver<Tag>
) : RecyclerView.Adapter<CategoriesAdapter.CategoryHolder<T>>(), RecyclerPositionSaveable {
    data class ModelHolder<T>(
        val text: String,
        val adapter: ScatteredAdapter<T>,
        val manager: LinearLayoutManager
    )

    val modelHolders: MutableMap<Int, ModelHolder<T>> = mutableMapOf()

    // holds references to liveDatas that are passed to lower adapters
    // the task of updating sources of those liveData lies on this adapter
    val subLiveDatas: MutableMap<Tag, MutableLiveData<List<T>>> = mutableMapOf()

    private lateinit var context: Context
    private var recyclerView: RecyclerView? = null

    var inflater: LayoutInflater? = null

    init {
        liveData.observe(lifecycleOwner, Observer {
            it.forEach {
                if (it.key in subLiveDatas) {
                    // updating source - launching chain of updates
                    subLiveDatas[it.key]!!.value = it.value
                } else {
                    subLiveDatas[it.key] = MutableLiveData(it.value)
                }
            }
            notifyDataSetChanged()
        })
    }

    private fun setInflaterIfNot(parent: ViewGroup) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getOrCreateModelHolder(position: Int): ModelHolder<T> {
        if (!modelHolders.containsKey(position)) {
            val category = liveData.value!!.keys.find { it.getPosition() == position }!!
            val manager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            modelHolders[position] = ModelHolder(
                delegate.tags[position],
                ScatteredAdapter(
                    lifecycleOwner,
                    subLiveDatas[category]!!,
                    tagHolderCreator,
                    itemHolderCreatorBuilder,
                    categoryAdapterPositionSaver.getSaverForTag(category)
                ),
                manager
            )
        }
        return modelHolders[position]!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder<T> {
        setInflaterIfNot(parent)
        val root =
            inflater!!.inflate(R.layout.category_tag_holder, parent, false)
        return CategoryHolder(root)
    }

    override fun getItemCount(): Int {
        return liveData.value?.size ?: 0
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        context = recyclerView.context
        this.recyclerView = recyclerView

        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: CategoryHolder<T>, position: Int) {
        val modelHolder = getOrCreateModelHolder(position)
        holder.onBind(getOrCreateModelHolder(position))
        val category = liveData.value!!.keys.find { it.getPosition() == position }!!

        categoryAdapterPositionSaver.restore(category, modelHolder.manager)
    }

    override fun onSaveState() {
        recyclerView?.layoutManager?.let {
            categoryAdapterPositionSaver.saveState(it)
        }
        for (i in modelHolders) {
            i.value.adapter.onSaveState()
        }
    }

    override fun restoreState() {
        recyclerView?.layoutManager?.let {
            categoryAdapterPositionSaver.restoreState(it)
        }
        Timber.i("modelHolders : ${modelHolders.size}")
 /*       for (i in modelHolders) {
            val category = liveData.value!!.keys.find { it.getPosition() == i.key }!!
            categoryAdapterPositionSaver.restore(category, i.value.manager)
        }*/
    }

    class CategoryHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tagTitle)
        val recycler = itemView.findViewById<RecyclerView>(R.id.itemList)

        fun onBind(modelHolder: ModelHolder<T>) {
            title.text = modelHolder.text
            recycler.adapter = modelHolder.adapter
            recycler.layoutManager = modelHolder.manager
        }
    }
}