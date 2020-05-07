package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.judemanutd.katexview.KatexView
import com.reducetechnologies.command_infrastructure.MathTextSpec
import com.reducetechnologies.command_infrastructure.PTypeSpecific
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldBinder
import com.reduction_technologies.database.databases_utils.prepareMathText

class MathTextBinder : PFieldBinder {
    private lateinit var mathView : KatexView
    private var mathSpec : MathTextSpec? = null

    override fun bind(spec: PTypeSpecific) {
        mathSpec = spec as MathTextSpec
        val text = prepareMathText(spec.text)
        mathView.setText(text)
    }

    override fun init(view: View) {
        mathView = view.findViewById(R.id.mathView)
    }

    companion object : CompanionInflater {
        override fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup): View {
            return inflater.inflate(R.layout.math_pfield, viewGroup, false)
        }
    }
}