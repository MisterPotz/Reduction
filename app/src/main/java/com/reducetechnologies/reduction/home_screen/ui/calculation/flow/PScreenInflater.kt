package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
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
    val context: Context,
    val container: ViewGroup,
    val windowManager: WindowManager
    // to use livedata

) {
    private val inflater = LayoutInflater.from(container.context)
    private var currentPScreen : PScreen? = null
    private var adapter : PFieldAdapter? = null

    fun showPScreen(pScreen: PScreen, isInputable: Boolean) {
        container.removeAllViews()
        currentPScreen = pScreen
        val view = inflater.inflate(R.layout.pscreen_card, container, true)
        val recycler = view.findViewById<RecyclerView>(R.id.fieldsList)
        val title = view.findViewById<TextView>(R.id.pScreenTitle)
        title.text = pScreen.title
        adapter =  PFieldAdapter(context, pScreen, windowManager, isInputable)
        recycler.layoutManager = LinearLayoutManager(container.context)
        recycler.adapter = adapter!!
    }

    // fills in input from view
    fun getFilled() : PScreen? {
        if (PScreenValidator.validatePScreen(pScreen = currentPScreen!!)) {
            return currentPScreen!!
        }
        return null
    }
}