package com.reducetechnologies.reduction.home_screen.ui.favorites

import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.ItemSorter
import com.reduction_technologies.database.databases_utils.CommonItem

class ViewTypeSorter  : ItemSorter<CommonItem> {
    override fun invoke(p1: CommonItem): Int {
        return if (p1.mathTitle != null) {
            1
        } else 0
    }
}