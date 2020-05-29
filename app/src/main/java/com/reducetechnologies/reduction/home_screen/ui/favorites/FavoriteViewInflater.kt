package com.reducetechnologies.reduction.home_screen.ui.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.ViewInflater
import java.lang.IllegalStateException

class FavoriteViewInflater(val inflater: LayoutInflater):
    ViewInflater {
    override fun inflate(parent: ViewGroup, type: Int): View {
        return when (type) {
            0 -> LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_text, parent, false)
            1 -> LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_math, parent, false)
            else -> throw IllegalStateException("no such layout types exist")
        }
    }
    companion object :
        ViewInflater.Factory {
        override fun createInflater(context: Context): ViewInflater {
            return FavoriteViewInflater(
                LayoutInflater.from(context)
            )
        }
    }
}