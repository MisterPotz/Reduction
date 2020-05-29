package com.reducetechnologies.reduction.home_screen.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.ItemSelectedCallback
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.ItemSorter
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.ViewBinder
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.ViewInflater
import com.reduction_technologies.database.databases_utils.CommonItem

class FavoritesAdapter(val inflater: LayoutInflater,
                       val viewInflater: ViewInflater,
                       val itemsSorter: ItemSorter<CommonItem>,
                       val viewBinderFactory: ViewBinder.Factory<CommonItem>,
                       val onClickListener : ItemSelectedCallback<CommonItem>) :
    RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {
    private var list : List<CommonItem>? = null

    fun setList(list: List<CommonItem>) {
        // can calculate difference here with diffutils
        if (this.list == null) {
            this.list = list
            notifyItemRangeInserted(0, list.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return this@FavoritesAdapter.list?.size ?: 0
                }

                override fun getNewListSize(): Int {
                    return list.size
                }

                override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return this@FavoritesAdapter.list!![oldItemPosition].title == list[newItemPosition].title
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldProduct = this@FavoritesAdapter.list!![oldItemPosition]
                    val newProduct = list[newItemPosition]
                    return oldProduct == newProduct
                }
            })
            this.list = list
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = viewInflater.inflate(parent, viewType)
        val binder = viewBinderFactory.createViewBinder(view, viewType)
        return FavoriteViewHolder(view, binder)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return itemsSorter(list!![position])
    }

    inner class FavoriteViewHolder(itemView: View, val binder: ViewBinder<CommonItem>) : RecyclerView.ViewHolder(itemView) {

        fun onBind(position: Int) {
            binder.bind(list!![position], onClickListener)
        }
    }
}