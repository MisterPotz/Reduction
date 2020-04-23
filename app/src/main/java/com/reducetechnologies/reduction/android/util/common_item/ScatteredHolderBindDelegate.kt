package com.reducetechnologies.reduction.android.util.common_item

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.core.view.minusAssign

import com.reducetechnologies.reduction.android.util.ScatteredHolderBindDelegate
import com.reduction_technologies.database.databases_utils.CommonItem
import java.lang.Double.max
import java.lang.IllegalStateException
import kotlin.math.max

class HolderBindDelegateBuilder_CommonItem : ScatteredHolderBindDelegate.Builder<CommonItem>() {
    override fun setSpecific(specific: ScatteredHolderBindDelegate.Specific?) {
        if (!(specific is Specific_CommonItem)) {
            throw IllegalStateException("wrong specification, only Specific_CommonItem allowed")
        }
        super.setSpecific(specific)
    }

    override fun build(): ScatteredHolderBindDelegate<CommonItem> {
        checkBuilder()
        val specific = _specific as Specific_CommonItem
        val delegate = Delegate(_view!!, specific.orderedList)
        cleanBuilder()
        return delegate
    }
}


/**
 * For inner usage
 * [orderedTextList] - contains text
 */
private class Delegate(
    view: View,
    val orderedTextList: List<EncapsulatedFrameItem>
) : ScatteredHolderBindDelegate<CommonItem>(view) {

    // TODO проверка что на каждый итем в списке есть текст
    override fun inflateHolderView(item: List<CommonItem>) {
        // clean before work
        clean()
        // change displayed values
        item.zip(orderedTextList).forEach {
            it.second.containingView.visibility = View.VISIBLE
            it.second.containingView.visibility = View.VISIBLE
            it.second.textView.text = it.first.title
        }
        // beautify work
        equalSizes()
        // TODO also here size of biggest item must be equalized to sum of smallest
        // hide unused views
        hideUnused(item.size)
    }

    private fun hideUnused(size: Int) {
        if (size < orderedTextList.size) {
            for (i in size until orderedTextList.size) {
                orderedTextList[i].textView.visibility = View.GONE
                orderedTextList[i].containingView.visibility = View.GONE
            }
        }
    }

    private fun getMaxFrame(): View {
        return orderedTextList.filter { it.type == ItemSizeEnum.BIG }[0].containingView
    }

    private fun getSmallItems(): List<View> {
        return return orderedTextList.filter { it.type == ItemSizeEnum.SMALL }
            .map { it.containingView }
    }

    private fun equalSizes() {
        // get width is not always available, (e.g. first display), so to understand real sizes
        // of items use asynchornous computations
        val bigFrameLayout = getMaxFrame()
        val smallFrameLayouts = getSmallItems()

        bigFrameLayout.post {
            val bigSize = bigFrameLayout.width /*+ bigFrameLayout.marginStart + bigFrameLayout.marginEnd*/

            val smallSizes = smallFrameLayouts.sumBy { it.width + + it.marginStart + it.marginEnd }.run {
                this - smallFrameLayouts.first().marginStart - smallFrameLayouts.last().marginEnd
            }

            val maxSize = max(bigSize, smallSizes)
            if (bigSize < maxSize) {
                bigFrameLayout.minimumWidth = maxSize
            }

            if (smallSizes < maxSize) {
                smallFrameLayouts[0].minimumWidth = maxSize / 2
                smallFrameLayouts[1].minimumWidth = maxSize / 2
            }
            for (i in orderedTextList) {
                i.containingView.invalidate()
            }
        }
    }

    private fun clean() {
        for (i in orderedTextList) {
            i.containingView.minimumWidth = DefaultValues.minimumWidthFrame
            i.textView.text = DefaultValues.defaultItemText
        }
    }
}