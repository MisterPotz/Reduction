package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.view.View
import android.widget.TextView
import com.reducetechnologies.reduction.R
import com.reduction_technologies.database.databases_utils.CommonItem

class CommonItemEncyclopediaViewBinder(val itemView : View) : ViewBinder<CommonItem> {
    val text : TextView = itemView.findViewById(R.id.itemName)
    var currentItem : CommonItem? = null

    override fun bind(item: CommonItem) {
        text.text = item.title
        currentItem = item
    }
    companion object : ViewBinder.Factory<CommonItem> {
        override fun createViewBinder(view: View): ViewBinder<CommonItem> {
            return CommonItemEncyclopediaViewBinder(itemView = view)
        }
    }
}