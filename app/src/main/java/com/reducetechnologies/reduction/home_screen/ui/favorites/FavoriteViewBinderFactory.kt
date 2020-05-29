package com.reducetechnologies.reduction.home_screen.ui.favorites

import android.view.View
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.TextItemEncyclopediaViewBinder
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.ViewBinder
import com.reduction_technologies.database.databases_utils.CommonItem
import java.lang.IllegalStateException

object FavoriteViewBinderFactory :
    ViewBinder.Factory<CommonItem> {
    override fun createViewBinder(view: View, type: Int): ViewBinder<CommonItem> {
        return when(type) {
            0 -> TextItemEncyclopediaViewBinder(view)
            1 -> FavoriteMathItemViewBinder(view)
            else -> throw IllegalStateException("Can't create the binder")
        }
    }
}