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
import java.lang.IllegalStateException

abstract class CategoriesInfoDelegate<T> {
    abstract val tags: List<String>
    abstract val sorting: (T) -> Int

    val categoriesAmount
        get() = tags.size
}

/**
 * Получает список неких итемов, и сортирует их с помощью делегата по нужному признаку
 */
open class CategoriesAdapter<T>(
    val liveData: LiveData<List<T>>,
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

    val dataBuckets: MutableList<MutableLiveData<MutableList<T>>?> =
        MutableList(delegate.categoriesAmount) { null }

    val modelHolders: MutableMap<Int, ModelHolder<T>> = mutableMapOf()

    private lateinit var context: Context

    var inflater: LayoutInflater? = null

    // теперь нужно лайвдатно побить по категориям
    init {
        liveData.observe(lifecycleOwner, Observer {
            // its a bit dirty, temporary workournd
            clearBuckets()
            // TODO notifySetChanged()

            // this block is at one time a filter and diversifier
            it.forEach { item ->
                val bucket = delegate.sorting(item)
                if (bucket < delegate.categoriesAmount && bucket >= 0) {
                    if (dataBuckets[bucket] == null) {
                        dataBuckets[bucket] = MutableLiveData(mutableListOf())
                    }
                    dataBuckets[bucket]!!.value!!.add(item)
                }
            }
        })
    }

    private fun clearBuckets() {
        for (i in dataBuckets.indices) {
            dataBuckets[i] = null
        }
    }

    private fun setInflaterIfNot(parent: ViewGroup) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getOrCreateModelHolder(position: Int): ModelHolder<T> {
        if (!modelHolders.containsKey(position)) {
            val manager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            modelHolders[position] = ModelHolder(
                delegate.tags[position],
                ScatteredAdapter(
                    lifecycleOwner,
                    dataBuckets[position] as MutableLiveData<List<T>>,
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
        return dataBuckets.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        context = recyclerView.context
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: CategoryHolder<T>, position: Int) {
        holder.onBind(modelHolders[position]!!)
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