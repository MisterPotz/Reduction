package com.reducetechnologies.reduction.home_screen.ui.favorites

import android.util.DisplayMetrics
import android.view.View
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.ItemSelectedCallback
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.ViewBinder
import com.reduction_technologies.database.databases_utils.CommonItem
import io.github.kexanie.library.MathView
import timber.log.Timber

class FavoriteMathItemViewBinder (val itemView: View) : ViewBinder<CommonItem> {
    val mathText: MathView = itemView.findViewById(R.id.itemNameMath)
    var currentItem: CommonItem? = null
    val displayMetrics = itemView.context.resources.displayMetrics
    val textSize =
        itemView.context.resources.getDimension(R.dimen.card_text_size) / displayMetrics.scaledDensity

    init {
        mathText.isVerticalScrollBarEnabled = false
        mathText.isHorizontalScrollBarEnabled = false
    }

    fun getDPI(size: Int, metrics: DisplayMetrics): Int {
        return size * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }

    private fun setTextSize(string: String, textSizeInSp: Int): String {
        return """<div style="font-size:${textSizeInSp}px;"> $string </div>"""
    }

    override fun current(): CommonItem {
        Timber.i("Current  requested: $currentItem ")
        return currentItem!!
    }

    override fun bind(item: CommonItem, callback: ItemSelectedCallback<CommonItem>?) {
        currentItem = item
        item.mathTitle!!.let {
            mathText.setText(setTextSize(it, textSize.toInt()))
        }
        if (callback != null) {
            itemView.setOnClickListener {
                callback(item)
            }
        }
    }
}