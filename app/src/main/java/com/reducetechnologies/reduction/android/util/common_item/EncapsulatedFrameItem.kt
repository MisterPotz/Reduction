package com.reducetechnologies.reduction.android.util.common_item

import android.view.View
import android.widget.TextView

/**
 * Contains frameLayout that encapsulates textView within item of [type]
 */
internal data class EncapsulatedFrameItem(
    val textView: TextView,
    val containingView: View,
    val type: ItemSizeEnum
)