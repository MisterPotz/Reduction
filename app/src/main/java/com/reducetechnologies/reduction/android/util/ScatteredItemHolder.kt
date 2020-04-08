package com.reducetechnologies.reduction.android.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R
import com.reduction_technologies.database.databases_utils.CommonItem

enum class HolderItemsOrientation { SINGLE_TOP, SINGLE_BOTTOM }

/**
 *
 */
class ScatteredItemHolder(
    var orientation: HolderItemsOrientation,
    itemView: View,
    val layoutInflater: LayoutInflater
) : RecyclerView.ViewHolder(
    itemView
) {
    private lateinit var bigItem: View
    private lateinit var bigItemData: CommonItem

    private lateinit var smallItems: List<View>
    private lateinit var smallItemsData: List<CommonItem>

    private val orientationHandleDelegate: PositionHandlerDelegate = PositionHandlerDelegate()

    private lateinit var bigFrame: FrameLayout
    private lateinit var smallFrames: List<FrameLayout>


    // Содержит данные, хранящиеся в этом холдере
    private lateinit var scatteredItemData: List<CommonItem>

    fun onBind(position: HolderItemsOrientation, items: List<CommonItem>) {
        scatteredItemData = items
        orientation = position

        bigItemData = orientationHandleDelegate.obtainBigItem(items)

        smallItemsData = orientationHandleDelegate.obtainSmallItems(items)

        val bigItemFrame = orientationHandleDelegate.obtainBigCell(position, itemView)
        val smallItemsFrame = orientationHandleDelegate.obtainSmallCells(position, itemView, items)

        bigItem = obtainMaxItem(bigItemFrame, bigItemData)
        smallItems = obtainSmallItems(smallItemsFrame, smallItemsData)

        bigItemFrame.attach(bigItem)
        smallItemsFrame.zip(smallItems).forEach {
            it.first.attach(it.second)
        }

        updateView()
    }

    private fun obtainMaxItem(root: ViewGroup, maxItem: CommonItem): View {
        val view = layoutInflater.inflate(R.layout.closed_card_item, root, false)
        view.setupAsCommonItem(maxItem)
        // setupping callbacks here
        return view
    }

    private fun obtainSmallItems(roots: List<ViewGroup>, smallItems: List<CommonItem>): List<View> {
        val views = roots.map { layoutInflater.inflate(R.layout.closed_card_item, it, false) }
        for (i in views.indices) {
            views[i].setupAsCommonItem(smallItems[i])
        }
        // setupping callbacks here
        return views
    }

    private fun FrameLayout.attach(child: View) {
        addView(child)
    }

    fun updateView() {
        itemView.invalidate()
    }

    private fun View.setupAsCommonItem(item: CommonItem) {
        findViewById<TextView>(R.id.itemName).let {
            it.text = item.title
        }
    }
}