package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reducetechnologies.command_infrastructure.MathTextSpec
import com.reducetechnologies.command_infrastructure.PTypeSpecific
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldBinder
import io.github.kexanie.library.MathView
import timber.log.Timber

/**
 * [textSizeSp] - text size in sp
 *
 */
class MathTextBinder(val textSizeSp: Int = 14, val displayMetrics: DisplayMetrics) : PFieldBinder {
    private lateinit var mathView : MathView
    private var mathSpec : MathTextSpec? = null

    override fun bind(spec: PTypeSpecific) {
        mathSpec = spec as MathTextSpec
        val text = spec.text.let { setTextSize(it, textSizeSp) }
        mathView.setText(text)
    }

    override fun init(view: View) {
        mathView = view.findViewById(R.id.mathView)
        mathView.isVerticalScrollBarEnabled = false
        mathView.isHorizontalScrollBarEnabled = false
    }

    private fun setTextSize(string: String, textSizeInSp : Int) : String {
        return """<div style="font-size:${textSizeInSp}px;"> $string </div>"""
    }

    companion object : CompanionInflater {
        override fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup): View {
            return inflater.inflate(R.layout.math_pfield, viewGroup, false)
        }
    }
}