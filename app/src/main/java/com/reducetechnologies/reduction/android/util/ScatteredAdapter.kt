package com.reducetechnologies.reduction.android.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R

// TODO чувак который получает нужные записи по тегам
class ScatteredAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<ScatteredItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScatteredItemHolder {
        val view = createViewBasedOnType(getOrientation(viewType), parent)
        return ScatteredItemHolder(view, inflater)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            HolderItemsOrientation.SINGLE_BOTTOM.ordinal
        } else {
            HolderItemsOrientation.SINGLE_TOP.ordinal
        }
    }

    fun getOrientation(ordinal: Int): HolderItemsOrientation {
        return HolderItemsOrientation.values().find { it.ordinal == ordinal }!!
    }

    fun createViewBasedOnType(orientation: HolderItemsOrientation, parent: ViewGroup): View {
        return when (orientation) {
            HolderItemsOrientation.SINGLE_BOTTOM -> inflater.inflate(
                R.layout.holder_two_top_one_bottom,
                parent,
                false
            )
            HolderItemsOrientation.SINGLE_TOP -> inflater.inflate(
                R.layout.holder_one_top_two_bottom,
                parent,
                false
            )
        }
    }

    override fun onBindViewHolder(holder: ScatteredItemHolder, position: Int) {
        TODO("Not yet implemented")
    }
}