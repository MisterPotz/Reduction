package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.reducetechnologies.command_infrastructure.InputListSpec
import com.reducetechnologies.command_infrastructure.PTypeSpecific
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldBinder
import timber.log.Timber

class InputListBinder(val inputable: Boolean) : PFieldBinder {
    private lateinit var view: View
    private lateinit var radioGroup: RadioGroup
    private lateinit var title: TextView
    private lateinit var inflater: LayoutInflater
    private var spec: InputListSpec? = null

    override fun bind(spec: PTypeSpecific) {
        this.spec = spec as InputListSpec
        reinflateRadioGroup()
        this.spec!!.additional.answer?.let {
            radioGroup.clearCheck()
            radioGroup.check(it)
        }
    }

    private fun reinflateRadioGroup() {
        radioGroup.removeAllViews()
        title.text = spec!!.title
        for (i in spec!!.additional.options.indices) {
            val radioButton: MaterialRadioButton = MaterialRadioButton(view.context)
            radioButton.id = i
            radioButton.text = spec!!.additional.options[i]
            radioButton.isEnabled = inputable
            radioButton.isClickable = inputable
            radioGroup.addView(radioButton)
        }
    }

    override fun init(view: View) {
        this.view = view
        radioGroup = view.findViewById(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            spec!!.additional.answer = i
        }
        title = view.findViewById(R.id.title)
        inflater = LayoutInflater.from(view.context)
        radioGroup.isEnabled = inputable
        radioGroup.isClickable = inputable
    }

    companion object : CompanionInflater {
        override fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup): View {
            return inflater.inflate(R.layout.input_list_pfield, viewGroup, false)
        }
    }
}