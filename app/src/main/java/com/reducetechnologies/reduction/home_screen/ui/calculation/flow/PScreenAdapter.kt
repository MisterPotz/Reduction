package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.reduction.R
import timber.log.Timber
import java.lang.StringBuilder

interface PFieldBinder{
    fun bind(spec : PTypeSpecific, view: View)

}

class PFieldHolder(val pFieldBinder : PFieldBinder, itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun onBind(spec: PTypeSpecific) {
        pFieldBinder.bind(spec, view = itemView)
    }
}

class PFieldAdapter(val pScreen : PScreen) : RecyclerView.Adapter<PFieldHolder>() {
    private var inflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PFieldHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val type = viewType.toPFieldType()
        val view = getView(type, parent)
        return PFieldHolder(getPFieldBinder(type), view)
    }

    override fun getItemCount(): Int {
        return pScreen.fields.size
    }

    override fun getItemViewType(position: Int): Int {
        return pScreen.fields[position].pFieldType.ordinal
    }

    override fun onBindViewHolder(holder: PFieldHolder, position: Int) {
        holder.onBind(pScreen.fields[position].typeSpecificData)
    }

    private fun getPFieldBinder(type: PFieldType) : PFieldBinder {
        return when (type) {
            PFieldType.TEXT -> object : PFieldBinder {
                private var text : TextView? = null
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
            }
            PFieldType.INPUT_TEXT -> object : PFieldBinder {
                private var inputText : EditText? = null
                private var titleText : TextView? = null
                private var inputLayout : TextInputLayout? = null
                private val stringBuilder = StringBuilder()
                private fun pickNumberHint(inputType: InputTextType) : String {
                    return when (inputType) {
                        InputTextType.FLOAT -> inputText!!.context.getString(R.string.float_hint)
                        InputTextType.INTEGER -> inputText!!.context.getString(R.string.integer_hint)
                    }
                }
                override fun bind(spec: PTypeSpecific, view: View) {
                    stringBuilder.clear()
                    if (inputText == null) {
                        inputText = view.findViewById(R.id.textInputField)
                    }
                    if (titleText == null) {
                        titleText = view.findViewById(R.id.inputTextTitle)
                    }
                    if (inputLayout == null) {
                        inputLayout = view.findViewById(R.id.textInputLayout)
                    }
                    val textSpec = (spec as InputTextSpec)
                    inputText!!.setOnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_NULL && event.action == KeyEvent.ACTION_DOWN) {
                            Timber.i("Any button was pressed")
                            textSpec.additional.answer = inputText!!.text.toString()
                        }
                        true
                    }
                    titleText!!.text = textSpec.title
                    inputLayout!!.hint = buildHint(textSpec.additional.hint, textSpec.type)
                    inputText!!.setText(textSpec.default)
                    textSpec.additional.error?.let {
                        inputText!!.error = textSpec.additional.error
                    }
                }
                private fun buildHint(givenHint : String?, inputType: InputTextType) : String {
                    val builder = StringBuilder()
                    if (givenHint != null) {
                        builder.append("$givenHint, ")
                    }
                    val numberHint = pickNumberHint(inputType)
                    builder.append(numberHint)
                    return builder.toString().apply { builder.clear() }
                }
            }
            else -> TODO ("not all cases implemented")
        }
    }

    private fun getView(type: PFieldType, root: ViewGroup) : View {
        return when (type) {
            PFieldType.TEXT -> inflater!!.inflate(R.layout.text_pfield, root, false)
            PFieldType.INPUT_TEXT -> inflater!!.inflate(R.layout.input_text_pfield, root, false)
            else -> TODO("not all cases implemented")
        }
    }
}