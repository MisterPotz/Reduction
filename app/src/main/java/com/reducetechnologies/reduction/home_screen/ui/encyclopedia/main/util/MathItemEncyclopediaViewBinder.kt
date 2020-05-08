package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util

import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.setMargins
import com.google.android.material.card.MaterialCardView
import com.reducetechnologies.reduction.R
import com.reduction_technologies.database.databases_utils.CommonItem
import io.github.kexanie.library.MathView
import timber.log.Timber

class MathItemEncyclopediaViewBinder(val itemView : View) : ViewBinder<CommonItem> {
    val group : MaterialCardView = itemView.findViewById(R.id.itemCard)
    val mathText : MathView = itemView.findViewById(R.id.itemNameMath)
    var currentItem : CommonItem? = null
    val displayMetrics = itemView.context.resources.displayMetrics
    val textSize = itemView.context.resources.getDimension(R.dimen.card_text_size) / displayMetrics.scaledDensity

    init {
        Timber.i("Given text size = $textSize")
        mathText.isVerticalScrollBarEnabled = false
        mathText.isHorizontalScrollBarEnabled = false
    }

    override fun bind(item: CommonItem) {
        currentItem = item
        item.mathTitle!!.let {
            // TODO ставить здесь ограничение по ширине - иначе webview разрастается и крашит все/
            //  нужно динамически парсить ресурс разметки и подменять там значение ширины, чтобы установить
            // fixing width - so katexview knows how to position itself
            val logicallyScaled = scaleMathField(it.length)
            val params = FrameLayout.LayoutParams(getDPI(logicallyScaled, displayMetrics), getDPI(50, displayMetrics))
            params.setMargins(getDPI(4, displayMetrics))
            group.layoutParams = FrameLayout.LayoutParams(params)
            mathText.setText(setTextSize(it, textSize.toInt()))
        }
    }

    fun getDPI(size: Int, metrics: DisplayMetrics): Int {
        return size * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }

    fun scaleMathField(symbols: Int) : Int {
        // uses empyrical-based scale - can't think of more smart thing
        return (350 * symbols.toFloat() / 44).toInt()
    }

    private fun setTextSize(string: String, textSizeInSp : Int) : String {
        return """<div style="font-size:${textSizeInSp}px;"> $string </div>"""
    }

    override fun current(): CommonItem {
        Timber.i("Current  requested: $currentItem ")
        return currentItem!!
    }
}