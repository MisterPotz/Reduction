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
import com.reducetechnologies.reduction.android.util.ScatteredAdapter
import com.reducetechnologies.reduction.android.util.ScatteredHolderBindDelegate
import com.reducetechnologies.reduction.android.util.ScatteredHolderCreator
import com.reduction_technologies.database.utils.Positionable

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
    val itemHolderCreatorBuilder: ScatteredHolderBindDelegate.Builder<T>
) : RecyclerView.Adapter<CategoriesAdapter.CategoryHolder<T>>() {
    data class ModelHolder<T>(
        val text: String,
        val adapter: ScatteredAdapter<T>,
        val manager: LinearLayoutManager
    )

    val modelHolders: MutableMap<Int, ModelHolder<T>> = mutableMapOf()
    // holds references to liveDatas that are passed to lower adapters
    // the task of updating sources of those liveData lies on this adapter
    val subLiveDatas : MutableMap<Tag, MutableLiveData<List<T>>> = mutableMapOf()

    private lateinit var context: Context

    var inflater: LayoutInflater? = null

    private fun setInflaterIfNot(parent: ViewGroup) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getOrCreateModelHolder(position: Int): ModelHolder<T> {
        if (!modelHolders.containsKey(position)) {
            val category = liveData.value!!.keys.find { it.getPosition() == position }
            val manager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            modelHolders[position] = ModelHolder(
                delegate.tags[position],
                ScatteredAdapter(
                    lifecycleOwner,
                    subLiveDatas[category]!!,
                    tagHolderCreator,
                    itemHolderCreatorBuilder
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

        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: CategoryHolder<T>, position: Int) {
        holder.onBind(getOrCreateModelHolder(position))
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