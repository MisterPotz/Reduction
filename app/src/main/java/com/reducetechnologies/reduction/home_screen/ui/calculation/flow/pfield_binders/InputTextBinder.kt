package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.reducetechnologies.command_infrastructure.InputTextSpec
import com.reducetechnologies.command_infrastructure.InputTextType
import com.reducetechnologies.command_infrastructure.PTypeSpecific
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldBinder
import timber.log.Timber
import java.lang.StringBuilder

class InputTextBinder : PFieldBinder {
    private lateinit var inputText: EditText
    private lateinit var titleText: TextView
    private lateinit var inputLayout: TextInputLayout
    private val stringBuilder = StringBuilder()
    private var textWatcher : TextWatcher? = null
    private var inputTextSpec : InputTextSpec? = null

    private fun pickNumberHint(inputType: InputTextType): String {
        return when (inputType) {
            InputTextType.FLOAT -> inputText!!.context.getString(R.string.float_hint)
            InputTextType.INTEGER -> inputText!!.context.getString(R.string.integer_hint)
        }
    }

    override fun init(view: View) {
        initialViewBind(view)
    }

    override fun bind(spec: PTypeSpecific) {
        stringBuilder.clear()
        clearState()
        rebind(spec)
    }

    private fun initialViewBind(view: View) {
        inputText = view.findViewById(R.id.textInputField)
        titleText = view.findViewById(R.id.inputTextTitle)
        inputLayout = view.findViewById(R.id.textInputLayout)
    }

    private fun clearState() {
        if (textWatcher != null) {
            inputText.removeTextChangedListener(textWatcher)
        }
    }

    private fun rebind(spec: PTypeSpecific) {
        inputTextSpec = spec as InputTextSpec
        inputTextSpec!!.additional.answer?.let { inputText!!.setText(it) } ?: kotlin.run { inputText!!.setText("") }

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    Timber.i("Got string: $s")
                    inputTextSpec!!.additional.answer = it.toString()
                }
            }
            override fun beforeTextChanged( s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        inputText.addTextChangedListener(textWatcher)
        titleText.text = inputTextSpec!!.title
        inputLayout.hint = buildHint(inputTextSpec!!.additional.hint, inputTextSpec!!.type)
        inputTextSpec!!.additional.error?.let {
            inputText.error = inputTextSpec!!.additional.error
        }
    }

    private fun buildHint(givenHint: String?, inputType: InputTextType): String {
        val builder = StringBuilder()
        if (givenHint != null) {
            builder.append("$givenHint, ")
        }
        val numberHint = pickNumberHint(inputType)
        builder.append(numberHint)
        return builder.toString().apply { builder.clear() }
    }

    companion object : CompanionInflater {
        override fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup): View {
            return inflater.inflate(R.layout.input_text_pfield, viewGroup, false)
        }
    }
}