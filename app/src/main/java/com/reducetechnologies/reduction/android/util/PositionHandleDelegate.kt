package com.reducetechnologies.reduction.android.util

import android.view.View
import android.widget.FrameLayout
import com.reducetechnologies.reduction.R
import com.reduction_technologies.database.databases_utils.CommonItem
import java.lang.IllegalStateException

//TODO обложить тестОм
/**
 * Знает по каким критериям рассортировать итемы на большой и маленькие, на основе текущей переданной
 * позиции вьюхи холдера определяет какие фреймы надо подтянуть чтобы потом в них записать нужные вьюхи
 */
class PositionHandlerDelegate() {

    fun findMax(items: List<CommonItem>): Int {
        return items.withIndex().maxBy { it.value.title.length }!!.index
    }

    fun obtainBigCell(
        orientation: HolderItemsOrientation,
        view: View
    ): FrameLayout {
        return view.findViewById(
            when (orientation) {
                HolderItemsOrientation.SINGLE_TOP -> R.id.topSingleItem
                HolderItemsOrientation.SINGLE_BOTTOM -> R.id.bottomSingleItem
            }
        )
    }

    fun obtainBigItem(items: List<CommonItem>) : CommonItem {
        return items[findMax(items)]
    }

    fun obtainSmallItems(
        items: List<CommonItem>
    ): List<CommonItem> {
        val maxItemIndex = findMax(items)
        return items.drop(maxItemIndex)
    }

    fun obtainSmallCells(
        orientation: HolderItemsOrientation,
        view: View,
        items: List<CommonItem>
    ): List<FrameLayout> {
        val smallItems = obtainSmallItems(items)
        return smallItems.mapIndexed { index: Int, commonItem: CommonItem ->
            getSmallFrame(orientation, index, view)
        }
    }

    private fun getSmallFrame(
        position: HolderItemsOrientation,
        index: Int,
        view: View
    ): FrameLayout {
        return when (position) {
            HolderItemsOrientation.SINGLE_TOP -> smallFrameSingleTop(index, view)
            HolderItemsOrientation.SINGLE_BOTTOM -> smallFrameSingleBottom(index, view)
        }
    }

    private fun smallFrameSingleTop(index: Int, view: View): FrameLayout {
        return when (index) {
            0 -> view.findViewById<FrameLayout>(R.id.bottomFirstItem)
            1 -> view.findViewById<FrameLayout>(R.id.bottomSecondItem)
            else -> throw IllegalStateException("No more than 2 small itemsis currently allowd for a holder")
        }
    }

    private fun smallFrameSingleBottom(index: Int, view: View): FrameLayout {
        return when (index) {
            0 -> view.findViewById<FrameLayout>(R.id.topFirstItem)
            1 -> view.findViewById<FrameLayout>(R.id.topSecondItem)
            else -> throw IllegalStateException("No more than 2 small itemsis currently allowd for a holder")
        }
    }
}