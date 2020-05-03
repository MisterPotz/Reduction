package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.command_infrastructure.PScreen
import com.reducetechnologies.reduction.R

/**
 * Manages screens that are shown before / during / after calculation process
 * [container] - view where current fragment is placed
 */
class PScreenInflater(
    val container: ViewGroup
    // to use livedata

) {
    private val inflater = LayoutInflater.from(container.context)
    private var currentPScreen : PScreen? = null

    fun showPScreen(pScreen: PScreen) {
        container.removeAllViews()
        currentPScreen = pScreen
        val view = inflater.inflate(R.layout.pscreen_card, container, true)
        val recycler = view.findViewById<RecyclerView>(R.id.fieldsList)
        val title = view.findViewById<TextView>(R.id.pScreenTitle)
        title.text = pScreen.title
        recycler.layoutManager = LinearLayoutManager(container.context)
        recycler.adapter = PFieldAdapter(pScreen)
    }

    // fills in input from view
    fun getFilled() : PScreen {
        TODO("Must survey all UI fields that has input and take the input to pscreen - then return in")
    }
}