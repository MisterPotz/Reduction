package com.reducetechnologies.reduction.android.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalStateException

enum class HolderItemsOrientation(val onTop: Int, val onBottom: Int) {
    SINGLE_TOP(
        1,
        2
    ),
    SINGLE_BOTTOM(2, 1)
}

/**
 * Can store view for this holder in implementation
 * Main purpose is to inflate view
 */
abstract class ScatteredHolderBindDelegate<T>(view: View) {
    abstract fun inflateHolderView(item: List<T>)

    /**
     * Specific contains implementation-specific data when view is builded.
     * E.g. - contains references to necessary views, so client don't have to look for view each time
     */
    abstract class Specific

    abstract class Builder<T> {
        protected open var _view: View? = null
        protected var _orientation: HolderItemsOrientation? = null
        protected var _specific : Specific? = null

        fun setView(view: View) {
            this._view = view
        }

        fun setOrientation(orientation: HolderItemsOrientation) {
            this._orientation = orientation
        }
        // not necessary parameter but useful
        open fun setSpecific(specific: Specific?) {
            this._specific = specific
        }

        fun cleanBuilder() {
            _view = null
            _orientation = null
            _specific = null
        }

        fun checkBuilder() {
            if (_view == null || _orientation == null) {
                throw IllegalStateException("Parameters for builder were not set")
            }
        }

        /**
         * Must call cleanBuilder after each delegate creation
         * Must throw errors if some ingredients are missing
         */
        abstract fun build(): ScatteredHolderBindDelegate<T>
    }
}

class ScatteredItemHolder<T>(
    val orientation: HolderItemsOrientation,
    val view: View,
    val delegateBuilder: ScatteredHolderBindDelegate.Builder<T>
) : RecyclerView.ViewHolder(view) {
    private val delegate: ScatteredHolderBindDelegate<T> =
        delegateBuilder.run { setView(view); setOrientation(orientation); build() }

    // при ребинде холдера получаем итем
    fun onBind(items: List<T>) {
        delegate.inflateHolderView(items)
    }
}