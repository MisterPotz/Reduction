package com.reducetechnologies.reduction.android.util.common_item

import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.CategoriesInfoDelegate
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.helpers.CommonItemTags

object CategoriesInfoDelegateCI : CategoriesInfoDelegate<CommonItem>() {
    override val tags: List<String> = CommonItemTags.tags
    // TODO предусмотреть фильтр - или можно в момент сортировки по бакетами пользоваться -1 от этого indexOfFirst
    override val sorting: (CommonItem) -> Int = {item ->
        tags.indexOfFirst { it == item.tag }
    }
}