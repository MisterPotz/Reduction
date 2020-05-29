package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util

import android.view.View
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.reducetechnologies.reduction.R
import com.reduction_technologies.database.databases_utils.CommonItem
import timber.log.Timber

class TextItemEncyclopediaViewBinder(val itemView: View) : ViewBinder<CommonItem> {
    val group: MaterialCardView = itemView.findViewById(R.id.itemCard)
    val text: TextView = itemView.findViewById(R.id.itemName)
    var currentItem: CommonItem? = null

    override fun current(): CommonItem {
        Timber.i("Current  requested: $currentItem ")
        return currentItem!!
    }

    override fun bind(item: CommonItem, callback: ItemSelectedCallback<CommonItem>?) {
        currentItem = item
        text.text = item.title
        if (callback != null) {
            itemView.setOnClickListener {
                callback(item)
            }
        }
    }
}