package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util

import android.view.View
import com.reduction_technologies.database.databases_utils.CommonItem
import java.lang.IllegalStateException

object CommonItemViewBinderFactory :
    ViewBinder.Factory<CommonItem> {
    override fun createViewBinder(view: View, type: Int): ViewBinder<CommonItem> {
        return when(type) {
            0 -> TextItemEncyclopediaViewBinder(view)
            1 -> MathItemEncyclopediaViewBinder(view)
            else -> throw IllegalStateException("Can't create the binder")
        }
    }
}