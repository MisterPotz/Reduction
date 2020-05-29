package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util

import com.google.gson.Gson
import com.reducetechnologies.command_infrastructure.CalculationResultsContainer
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.databases_utils.Tags

fun CalculationResultsContainer.toCommonItem(title: String, gson: Gson) : CommonItem {
    val commonItem = CommonItem(title = title, tag = Tags.RESULT.item)
    commonItem.additional = gson.toJson(this)
    return commonItem
}