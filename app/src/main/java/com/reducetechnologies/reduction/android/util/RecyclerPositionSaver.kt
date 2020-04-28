package com.reducetechnologies.reduction.android.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Interface for a thing that stores current state of recyclerView
 */
interface RecyclerPositionSaver {
    fun saveState(layoutManager: RecyclerView.LayoutManager)
    fun restoreState(layoutManager: RecyclerView.LayoutManager)
}