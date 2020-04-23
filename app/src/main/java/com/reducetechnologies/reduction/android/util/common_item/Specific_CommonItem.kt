package com.reducetechnologies.reduction.android.util.common_item

import android.widget.TextView
import com.reducetechnologies.reduction.android.util.ScatteredHolderBindDelegate

/**
 * Contains list of textviews to access to in order of their appearance in the list
 * In the future can also contain views, sorted by size
 */
internal data class Specific_CommonItem(val orderedList : List<EncapsulatedFrameItem>) : ScatteredHolderBindDelegate.Specific() {
}