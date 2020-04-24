package com.reducetechnologies.reduction.android.util

import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

/**
 * Used for storing various states of inner layout managers, and state of the main manager
 */
class CategoryAdapterPositionSaver<Tag> : RecyclerPositionSaver {

    private val savedStates: MutableMap<Tag, Parcelable?> = mutableMapOf()
    private var savedState: Parcelable? = null

    fun getSaverForTag(tag: Tag): RecyclerPositionSaver {
        return object : RecyclerPositionSaver {
            override fun saveState(layoutManager: RecyclerView.LayoutManager) {
                savedStates[tag] = layoutManager.onSaveInstanceState()
                Timber.i("saveState for $tag in ${this@CategoryAdapterPositionSaver}")
            }

            override fun restoreState(layoutManager: RecyclerView.LayoutManager) {
                layoutManager.onRestoreInstanceState(savedStates[tag])
                Timber.i("restoreState for $tag")
            }

        }
    }

    fun restore(tag: Tag, layoutManager: RecyclerView.LayoutManager) {
        Timber.i("restore in $this")
        savedStates[tag]?.let {
            Timber.i("restore for tag $tag")
            layoutManager.onRestoreInstanceState(it)
        } ?: Timber.w("restore can't be done")
    }

    override fun saveState(layoutManager: RecyclerView.LayoutManager) {
        Timber.i("saveState for main, in $this")
        savedState = layoutManager.onSaveInstanceState()
    }

    override fun restoreState(layoutManager: RecyclerView.LayoutManager) {
        Timber.i("restoreState for main")
        layoutManager.onRestoreInstanceState(savedState)
    }
}