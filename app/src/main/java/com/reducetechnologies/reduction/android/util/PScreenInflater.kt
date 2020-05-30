package com.reducetechnologies.reduction.android.util

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldAdapter

object PScreenSimpleeInflater {
    fun inflatPScreen(
        pScreen: PScreen,
        container: ViewGroup,
        displayMetrics: DisplayMetrics,
        windowManager: WindowManager,
        inflater: LayoutInflater,
        inputable: Boolean,
        links: HashMap<Destination, LinkCalledCallback>?
    ) {
        container.removeAllViews()
        val view = inflater.inflate(R.layout.pscreen_card, container, true)
        val recycler = view.findViewById<RecyclerView>(R.id.fieldsList)
        val title = view.findViewById<TextView>(R.id.pScreenTitle)
        title.text = pScreen.title
        val adapter =
            PFieldAdapter(container.context, pScreen, displayMetrics, windowManager, inputable)
        adapter.setupLinks(links)
        recycler.layoutManager = LinearLayoutManager(container.context)
        recycler.adapter = adapter
        recycler.setHasFixedSize(true)
    }

    fun fillAnswersWithDefaults(pScreen: PScreen) {
        pScreen.fields.forEach {
            val type = it.pFieldType
            when (type) {
                PFieldType.INPUT_TEXT -> (it.typeSpecificData as InputTextSpec).let { spec ->
                    if (spec.additional.answer == null && spec.default != null) {
                        spec.additional.answer = spec.default
                    }
                }
                PFieldType.INPUT_PICTURE -> (it.typeSpecificData as InputPictureSpec).let { spec ->
                    if (spec.additional.answer == null && spec.default != null) {
                        spec.additional.answer = spec.default
                    }
                }
                PFieldType.INPUT_LIST -> (it.typeSpecificData as InputListSpec).let { spec ->
                    if (spec.additional.answer == null && spec.default != null) {
                        spec.additional.answer = spec.default
                    }
                }
            }
        }
    }
}