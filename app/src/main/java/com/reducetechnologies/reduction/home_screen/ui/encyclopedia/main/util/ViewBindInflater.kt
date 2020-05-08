package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util

import android.content.Context
import android.view.View
import android.view.ViewGroup

interface ViewInflater {
    fun inflate(parent: ViewGroup, type: Int): View
    interface Factory {
        fun createInflater(context: Context) : ViewInflater
    }
}

interface ViewBinder<R> {
    fun bind(item: R)
    fun current() : R
    interface Factory<R> {
        fun createViewBinder(view:View, type: Int) : ViewBinder<R>
    }
}