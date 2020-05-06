package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.content.Context
import android.view.View
import android.view.ViewGroup

interface ViewInflater {
    fun inflate(parent: ViewGroup): View
    interface Factory {
        fun createInflater(context: Context) : ViewInflater
    }
}

interface ViewBinder<R> {
    fun bind(item: R)

    interface Factory<R> {
        fun createViewBinder(view:View) : ViewBinder<R>
    }
}