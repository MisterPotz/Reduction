package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reducetechnologies.reduction.R

class CommonItemEncyclopediaViewInflater(val inflater: LayoutInflater):
    ViewInflater {
    override fun inflate(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.closed_card_item, parent, false)
    }
    companion object :
        ViewInflater.Factory {
        override fun createInflater(context: Context): ViewInflater {
            return CommonItemEncyclopediaViewInflater(
                LayoutInflater.from(context)
            )
        }
    }
}