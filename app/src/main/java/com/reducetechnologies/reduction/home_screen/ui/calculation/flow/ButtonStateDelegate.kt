package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.view.View
import android.widget.Button

class ButtonStateDelegate(val prev: Button, var next: Button, var enter: Button) {
    private var hasPrev = false
    private var hasNext = false
    private var mustBeEntered = false

    private fun setHasPrevVisibility() {
//        prev.isClickable = hasPrev
        prev.isEnabled = hasPrev
    }

    private fun setHasNextVisibility() {
//        next.isClickable = hasNext && !mustBeEntered
        next.isEnabled = hasNext && !mustBeEntered
            // if (hasNext && !mustBeEntered) View.VISIBLE else View.GONE
    }

    private fun setEnterVisibility() {
//        enter.visibility = if (mustBeEntered) View.VISIBLE else View.GONE
        enter.isEnabled = mustBeEntered
    }

    fun hasPrev(boolean: Boolean) {
        hasPrev = boolean
        updateVisibility()
    }

    fun hasNext(boolean: Boolean) {
        hasNext = boolean
        updateVisibility()
    }

    fun mustBeEntered(boolean: Boolean) {
        mustBeEntered = boolean
        updateVisibility()
    }

    private fun updateVisibility() {
        setEnterVisibility()
        setHasPrevVisibility()
        setHasNextVisibility()
    }
}