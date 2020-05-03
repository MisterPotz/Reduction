package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.reducetechnologies.command_infrastructure.PTypeSpecific
import com.reducetechnologies.command_infrastructure.TextSpec
import com.reducetechnologies.command_infrastructure.TextType
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldBinder

class TextFieldBinder : PFieldBinder {
    private var text: TextView? = null
    override fun bind(spec: PTypeSpecific, view: View) {
        if (text == null) {
            text = view.findViewById(R.id.textField)
        }
        val textSpec = (spec as TextSpec)
        text!!.text = textSpec.text
        val id = when (textSpec.additional.type) {
            TextType.BODY -> R.style.Body1
            TextType.HEADLINE -> R.style.Headline6
        }
        TextViewCompat.setTextAppearance(text!!, id)
    }

    companion object : CompanionInflater {
        override fun inflate(
            inflater: LayoutInflater,
            viewGroup: ViewGroup
        ): View {
            return inflater.inflate(R.layout.text_pfield, viewGroup, false)
        }
    }
}