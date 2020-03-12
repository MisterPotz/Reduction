package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R
import kotlinx.android.synthetic.main.card_opened.view.*

interface ListView

class CategoriesAdapter() : RecyclerView.Adapter<CategoriesAdapter.CategoryHolder>() {
    var list: List<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val root =
            LayoutInflater.from(parent.context).inflate(R.layout.closed_card_item, parent, false)
        return CategoryHolder(root)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.itemView.apply {
            title.text = list!![position]
        }
    }

    class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}