package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Xml
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.setMargins
import com.google.android.material.card.MaterialCardView
import com.judemanutd.katexview.KatexView
import com.reducetechnologies.reduction.R
import com.reduction_technologies.database.databases_utils.CommonItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.IOException


class CommonItemEncyclopediaViewBinder(val itemView : View) : ViewBinder<CommonItem> {
    val group : MaterialCardView = itemView.findViewById(R.id.itemCard)
    val text : TextView = itemView.findViewById(R.id.itemName)
    // used to obtain real size of text
    val probe: TextView = itemView.findViewById(R.id.probe)
    val textHeight = probe.height
    val mathText : KatexView = itemView.findViewById(R.id.itemNameMath)
    var currentItem : CommonItem? = null
    val displayMetrics = text.context.resources.displayMetrics

    override fun bind(item: CommonItem) {
        currentItem = item
        item.mathTitle?.let {
            // TODO ставить здесь ограничение по ширине - иначе webview разрастается и крашит все/
            //  нужно динамически парсить ресурс разметки и подменять там значение ширины, чтобы установить
            // fixing width - so katexview knows how to position itself
            val logicallyScaled = scaleMathField(it.length)
            val params = FrameLayout.LayoutParams(getDPI(logicallyScaled, displayMetrics), getDPI(50, displayMetrics))
            params.setMargins(getDPI(4, displayMetrics))
            group.layoutParams = FrameLayout.LayoutParams(params)
            switch(true)
            mathText.setText(it)
        } ?: kotlin.run {
            switch(false)
            text.text = item.title
            val params = obtainAttributeSetForFrame()
            // setting back the parsed ones
            group.layoutParams = FrameLayout.LayoutParams(group.context, params)
        }
    }

    fun getDPI(size: Int, metrics: DisplayMetrics): Int {
        return size * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }

    fun scaleMathField(symbols: Int) : Int {
        // uses empyrical-based scale - can't think of more smart thing
        return (160 * symbols.toFloat() / 44).toInt() + 30
    }

    private fun obtainAttributeSetForFrame() : AttributeSet {
        val parser = group.context.resources.getLayout(R.layout.closed_card_item)
        var state = 0
        var attributes: AttributeSet? = null
        do {
            try {
                state = parser.next()
            } catch (e1: XmlPullParserException) {
                e1.printStackTrace()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            if (state == XmlPullParser.START_TAG) {
                if (parser.name == "com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.InterceptingMaterialCardView") {
                    attributes = Xml.asAttributeSet(parser)
                    break
                }
            }
        } while (state != XmlPullParser.END_DOCUMENT)
        return attributes!!
    }

    private fun switch(mathTextVisible: Boolean) {
        val mathVisibility = if (mathTextVisible) View.VISIBLE else View.GONE
        val textVisibilty = if (mathTextVisible) View.GONE else View.VISIBLE
        text.visibility = textVisibilty
        mathText.visibility = mathVisibility
    }

    companion object : ViewBinder.Factory<CommonItem> {
        override fun createViewBinder(view: View): ViewBinder<CommonItem> {
            return CommonItemEncyclopediaViewBinder(itemView = view)
        }
    }
}