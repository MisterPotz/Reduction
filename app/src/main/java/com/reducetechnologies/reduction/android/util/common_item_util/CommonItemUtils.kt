package com.reducetechnologies.reduction.android.util.common_item_util

import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.helpers.CategoryTag

class CommonItemUtils {
    /**
     * Maps list into map of iterator overs original list
     */
    fun splitByTags(list: List<CommonItem>): Map<CategoryTag, List<CommonItem>> {
        val sorted = mutableMapOf<CategoryTag, MutableList<CommonItem>>()
        list.map { commonItem ->
            val categoryTag = CategoryTag.values().find { it.title == commonItem.tag }
            if (categoryTag != null) {
                if (categoryTag !in sorted.keys) {
                    sorted[categoryTag] = mutableListOf(commonItem)
                } else {
                    sorted[categoryTag]!!.add(commonItem)
                }
            }
        }
        return sorted
    }

}