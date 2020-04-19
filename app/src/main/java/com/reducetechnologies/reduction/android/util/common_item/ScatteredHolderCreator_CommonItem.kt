package com.reducetechnologies.reduction.android.util.common_item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
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

    private fun prepareViewSingleBottom(view: View, inflater: LayoutInflater): Specific_CommonItem {
        val max = view.findViewById<FrameLayout>(R.id.bottomSingleItem)
        val minOne = view.findViewById<FrameLayout>(R.id.topFirstItem)
        val minTwo = view.findViewById<FrameLayout>(R.id.topSecondItem)

        val viewMax = inflater.inflate(R.layout.closed_card_item, max, true)
        val viewMinOne = inflater.inflate(R.layout.closed_card_item, minOne, true)
        val viewMinTwo = inflater.inflate(R.layout.closed_card_item, minTwo, true)

        val textMax = viewMax.findViewById<TextView>(R.id.itemName)
        val textMinOne = viewMinOne.findViewById<TextView>(R.id.itemName)
        val textMinTwo = viewMinTwo.findViewById<TextView>(R.id.itemName)
        return Specific_CommonItem(textMax, textMinOne, textMinTwo)
    }

    private fun prepareViewSingleTop(view: View, inflater: LayoutInflater): Specific_CommonItem {
        val max = view.findViewById<FrameLayout>(R.id.topSingleItem)
        val minOne = view.findViewById<FrameLayout>(R.id.bottomFirstItem)
        val minTwo = view.findViewById<FrameLayout>(R.id.bottomSecondItem)

        val viewMax = inflater.inflate(R.layout.closed_card_item, max, true)
        val viewMinOne = inflater.inflate(R.layout.closed_card_item, minOne, true)
        val viewMinTwo = inflater.inflate(R.layout.closed_card_item, minTwo, true)

        val textMax = viewMax.findViewById<TextView>(R.id.itemName)
        val textMinOne = viewMinOne.findViewById<TextView>(R.id.itemName)
        val textMinTwo = viewMinTwo.findViewById<TextView>(R.id.itemName)
        return Specific_CommonItem(textMax, textMinOne, textMinTwo)
    }

    override fun getViewsForOneHolder(list: List<CommonItem>): Iterator<List<CommonItem>> {
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