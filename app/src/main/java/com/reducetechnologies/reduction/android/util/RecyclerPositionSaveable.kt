package com.reducetechnologies.reduction.android.util

interface RecyclerPositionSaveable {
    fun onSaveState()
    fun restoreState()
}