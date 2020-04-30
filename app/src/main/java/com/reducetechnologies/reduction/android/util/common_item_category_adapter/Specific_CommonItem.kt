package com.reducetechnologies.reduction.android.util.common_item_category_adapter

import com.reducetechnologies.reduction.android.util.ScatteredHolderBindDelegate

/**
 * Contains list of views to access to in order of their appearance in the holder
 */
internal data class Specific_CommonItem(val orderedList : List<EncapsulatedFrameItem>) : ScatteredHolderBindDelegate.Specific() {
}