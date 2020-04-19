package com.reducetechnologies.reduction.android.util.common_item

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import com.reducetechnologies.reduction.android.util.ScatteredHolderBindDelegate
import com.reduction_technologies.database.databases_utils.CommonItem
import java.lang.IllegalStateException

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
        val delegate = Delegate(_view!!, specific.max, specific.minOne, specific.minTwo)
        cleanBuilder()
        return delegate
    }
}

private class Delegate(
    view: View,
    val maxText : TextView,
    val minOneText : TextView,
    val minTwoText : TextView
) : ScatteredHolderBindDelegate<CommonItem>(view) {

    override fun inflateHolderView(item: List<CommonItem>) {
        maxText.text = item[0].title
        minOneText.text = item[1].title
        minTwoText.text = item[2].title
    }
}