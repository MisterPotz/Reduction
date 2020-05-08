package com.reducetechnologies.reduction.android.util.common_item_category_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.cardview.widget.CardView
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.HolderItemsOrientation
import com.reducetechnologies.reduction.android.util.ScatteredHolderBindDelegate
import com.reducetechnologies.reduction.android.util.ScatteredHolderCreator
import com.reduction_technologies.database.databases_utils.CommonItem
import java.lang.IllegalStateException
import kotlin.math.min

object ScatteredHolderCreator_CommonItem : ScatteredHolderCreator<CommonItem> {
    // Количество итемов на один холдер
    private val amountOnHolder = 3

    override fun getOrientation(position: Int): HolderItemsOrientation {
        return when (position % 2) {
            0 -> HolderItemsOrientation.SINGLE_BOTTOM
            1 -> HolderItemsOrientation.SINGLE_TOP
            else -> throw IllegalStateException("Cannot return such orientation")
        }
    }

    override fun toViewType(orientation: HolderItemsOrientation): Int {
        return HolderItemsOrientation.values().indexOfFirst { orientation == it }
    }

    override fun toOrientation(viewType: Int): HolderItemsOrientation {
        return HolderItemsOrientation.values()[viewType]
    }

    override fun createView(
        viewType: Int,
        parent: ViewGroup,
        inflater: LayoutInflater
    ): Pair<View, ScatteredHolderBindDelegate.Specific?> {
        return when (toOrientation(viewType)) {
            HolderItemsOrientation.SINGLE_BOTTOM -> inflater.inflate(
                R.layout.holder_two_top_one_bottom,
                parent,
                false
            ).let {
                Pair(it, prepareViewSingleBottom(it, inflater))
            }

            HolderItemsOrientation.SINGLE_TOP -> inflater.inflate(
                R.layout.holder_one_top_two_bottom,
                parent,
                false
            ).let {
                Pair(it, prepareViewSingleTop(it, inflater))
            }
        }
    }

    private fun prepareView(
        @IdRes ids: List<Int>,
        types: List<ItemSizeEnum>,
        view: View,
        inflater: LayoutInflater
    ): Specific_CommonItem {
        val items = (ids.indices).map {
            val frameParent = view.findViewById<FrameLayout>(ids[it])
            inflater.inflate(R.layout.closed_card_item_text, frameParent, true)
            val card = frameParent.findViewById<CardView>(R.id.itemCard)
            val view = card.findViewById<TextView>(R.id.itemName)

            EncapsulatedFrameItem(view, card, types[it])
        }
        return Specific_CommonItem(items)
    }

    private fun prepareViewSingleBottom(view: View, inflater: LayoutInflater): Specific_CommonItem {
        val ids = listOf(R.id.topFirstItem, R.id.topSecondItem, R.id.bottomSingleItem)
        val sizes = listOf(ItemSizeEnum.SMALL, ItemSizeEnum.SMALL, ItemSizeEnum.BIG)
        return prepareView(ids, sizes, view, inflater)
    }

    private fun prepareViewSingleTop(view: View, inflater: LayoutInflater): Specific_CommonItem {
        val ids = listOf(R.id.topSingleItem, R.id.bottomFirstItem, R.id.bottomSecondItem)
        val sizes = listOf(ItemSizeEnum.BIG, ItemSizeEnum.SMALL, ItemSizeEnum.SMALL)
        return prepareView(ids, sizes, view, inflater)
    }

    override fun splitListToPacks(list: List<CommonItem>): Iterator<List<CommonItem>> {
        return object : Iterator<List<CommonItem>> {
            var current = 0
            override fun hasNext(): Boolean {
                return current < list.size
            }

            override fun next(): List<CommonItem> {
                // take current, current + 1, current + 2 ... current + (amountOnHolder - 1)
                val lastIndexToTake = min(list.size - 1, current + (amountOnHolder - 1))
                val newList = list.slice(current..lastIndexToTake)
                current += amountOnHolder
                return newList
            }
        }
    }
}