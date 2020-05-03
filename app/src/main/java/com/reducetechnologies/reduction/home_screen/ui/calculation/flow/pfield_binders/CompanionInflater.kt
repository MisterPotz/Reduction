package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes

interface CompanionInflater {
    fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup) : View
}