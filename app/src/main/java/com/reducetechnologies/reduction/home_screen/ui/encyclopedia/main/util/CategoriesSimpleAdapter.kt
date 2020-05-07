package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util

import android.os.Parcelable
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.contains
import androidx.core.util.putAll
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.reduction_technologies.database.databases_utils.CommonItem
import timber.log.Timber

typealias ItemSelectedCallback<R> = (R) -> Unit

interface Category<R> {
    fun categoryId() : Int
    fun title(): String
    fun getItems(): List<R>
}

/**
 * [list] - all items, one item - one category
 * [viewInflaterFactory] - creates inflater for each item per category
 * [viewBinderFactory] - creates binders for each item
 * [onClickListener] - callback for clicking views
 */
class CategoriesSimpleAdapter<R, T: Category<R>>(
    private var list: List<T>? = null,
    val viewInflaterFactory: ViewInflater.Factory,
    val viewBinderFactory: ViewBinder.Factory<R>,
    val onClickListener : ItemSelectedCallback<R>
) : RecyclerView.Adapter<CategoriesSimpleAdapter<R, T>.CategorySimpleHolder>() {
    private val savedPositions: SparseArray<Parcelable?> = SparseArray()

    init {
        setHasStableIds(true)
    }

    private fun updateSavedPosition() {
        list?.forEach {
            if (it.categoryId() in savedPositions)  {
                // do nothing - category is already stored
            } else {
                savedPositions.put(it.categoryId(), null)
            }
        }
    }

    fun setList(list: List<T>) {
        // can calculate difference here with diffutils
        if (this.list == null) {
            this.list = list
            notifyItemRangeInserted(0, list.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return this@CategoriesSimpleAdapter.list?.size ?: 0
                }

                override fun getNewListSize(): Int {
                    return list.size
                }

                override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return this@CategoriesSimpleAdapter.list!![oldItemPosition].title() == list[newItemPosition].title()
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldProduct = this@CategoriesSimpleAdapter.list!![oldItemPosition].getItems()
                    val newProduct = list[newItemPosition].getItems()
                    return (newProduct.size == oldProduct.size)
                }
            })
            this.list = list
            result.dispatchUpdatesTo(this)
        }
        updateSavedPosition()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySimpleHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(com.reducetechnologies.reduction.R.layout.category_tag_holder, parent, false)
        return CategorySimpleHolder(view)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: CategorySimpleHolder, position: Int) {
        holder.onBind(position)
    }

    fun savePosition(holder: CategorySimpleHolder) {
        Timber.i("Saving position of ${holder.position}")
        savedPositions.put(list!![holder.position!!].categoryId(), holder.layoutManager.onSaveInstanceState())
    }

    override fun onViewRecycled(holder: CategorySimpleHolder) {
        savePosition(holder)
    }

    override fun onViewDetachedFromWindow(holder: CategorySimpleHolder) {
        savePosition(holder)
    }

    override fun getItemId(position: Int): Long {
        return list!![position].categoryId().toLong()
    }

    fun onSave() : PositionsSaver {
        // TODO здесь надо получить все холдеры (а надо ли? мб при отрывании рейсаклера отрываются и вьюхи?), onViewRecycled будет недостаточно
        return PositionsSaver(
            savedPositions
        )
    }

    fun onRestore(positionsSaver: PositionsSaver) {
        savedPositions.clear()
        savedPositions.putAll(positionsSaver.saved)
        notifyDataSetChanged()
    }

    inner class CategorySimpleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var position: Int? = null
        private val recyclerView = itemView.findViewById<RecyclerView>(com.reducetechnologies.reduction.R.id.itemList)
        private val title: TextView = itemView.findViewById(com.reducetechnologies.reduction.R.id.tagTitle)
        val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)

        @Suppress("UNCHECKED_CAST")
        fun onBind(position: Int) {
            this.position = position
            val list = this@CategoriesSimpleAdapter.list!![position].getItems()
            Timber.i("Binded CategorySimpleHolder, items: $list")

            if (recyclerView.adapter == null) {
                recyclerView.adapter =
                    OneCategoryAdapter(
                        list,
                        viewInflaterFactory.createInflater(recyclerView.context),
                        viewBinderFactory, {
                            savePosition(this)
                        },
                        onClickListener
                    )
            } else {
                (recyclerView.adapter as OneCategoryAdapter<R>).setList(list)
            }
            title.text =  this@CategoriesSimpleAdapter.list!![position].title()
            val restoredState = savedPositions[this@CategoriesSimpleAdapter.list!![position].categoryId()]
            if (recyclerView.layoutManager == null) {
                recyclerView.layoutManager = layoutManager
            }
            restoredState?.let {
                recyclerView.layoutManager!!.run {
                    onRestoreInstanceState(it)
                }
            }
        }
    }

    class OneCategoryAdapter<R>(
        private var list: List<R>? = null,
        val viewInflater: ViewInflater,
        val viewBinderFactory: ViewBinder.Factory<R>,
        val callbackOnSave: () -> Unit,
        val onClickListener: ItemSelectedCallback<R>
        ) :
        RecyclerView.Adapter<OneCategoryAdapter<R>.OneCategoryHolder>() {
        // TODO а как здесь реализовать изменения списка? как отреагирует внутренний ресайклер, когда изменится верхний?
        inner class OneCategoryHolder(itemView: View, val viewBinder: ViewBinder<R>) :
            RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener{
                    onClickListener(viewBinder.current())
                }
            }
            fun onBind(position: Int) {
                Timber.i("Binded OneCategoryAdapter")
                viewBinder.bind(list!![position])
            }
        }

        override fun onViewDetachedFromWindow(holder: OneCategoryHolder) {
            callbackOnSave()
        }

        override fun onViewRecycled(holder: OneCategoryHolder) {
            callbackOnSave()
        }

        fun setList(list: List<R>) {
            this.list = list
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OneCategoryHolder {
            val view = viewInflater.inflate(parent)
            val binder = viewBinderFactory.createViewBinder(view)
            return OneCategoryHolder(view, binder)
        }

        override fun onBindViewHolder(holder: OneCategoryHolder, position: Int) {
            holder.onBind(position)
        }
    }
}