package com.reducetechnologies.reduction.android.util

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.command_infrastructure.PScreen
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldAdapter

object PScreenSimpleeInflater {
    fun inflatPScreen(
        pScreen: PScreen,
        container: ViewGroup,
        windowManager: WindowManager,
        inflater: LayoutInflater,
        inputable: Boolean
    ) {
        container.removeAllViews()
        val view = inflater.inflate(R.layout.pscreen_card, container, true)
        val recycler = view.findViewById<RecyclerView>(R.id.fieldsList)
        val title = view.findViewById<TextView>(R.id.pScreenTitle)
        title.text = pScreen.title
        val adapter = PFieldAdapter(container.context, pScreen, windowManager, inputable)
        recycler.layoutManager = LinearLayoutManager(container.context)
        recycler.adapter = adapter
    }
}