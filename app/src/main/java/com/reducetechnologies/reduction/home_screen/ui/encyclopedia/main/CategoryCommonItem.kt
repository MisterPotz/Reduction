package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import com.reduction_technologies.database.databases_utils.CommonItem

class CategoryCommonItem(
    val id: Int,
    val title: String,
    val list: List<CommonItem>): Category<CommonItem> {

    override fun getItems(): List<CommonItem> {
        return list
    }

    override fun title(): String {
        return title
    }

    override fun categoryId(): Int {
        return id
    }
}