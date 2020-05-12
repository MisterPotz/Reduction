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
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PScreenValidator
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PScreenValidator.validateNumber
import com.reduction_technologies.database.tables_utils.TwoSidedDomain
import timber.log.Timber
import java.lang.NumberFormatException
import java.lang.StringBuilder

class InputTextBinder(val inputable: Boolean) : PFieldBinder {
    private lateinit var inputText: EditText
    private lateinit var titleText: TextView
    private lateinit var inputLayout: TextInputLayout
    private var textWatcher: TextWatcher? = null
    private var inputTextSpec: InputTextSpec? = null
    private val stringBuilder: StringBuilder = StringBuilder()

    override fun init(view: View) {
        initialViewBind(view)
        inputText.isEnabled = inputable
        inputText.isClickable = inputable
    }

    override fun onAttach() {
        validateCurrentInput()
    }

    override fun bind(spec: PTypeSpecific) {
        clearState()
        rebind(spec)
        validateCurrentInput()
    }

    private fun initialViewBind(view: View) {
        inputText = view.findViewById(R.id.textInputField)
        titleText = view.findViewById(R.id.inputTextTitle)
        inputLayout = view.findViewById(R.id.textInputLayout)
    }

    private fun clearState() {
        stringBuilder.clear()
        if (textWatcher != null) {
            inputText.removeTextChangedListener(textWatcher)
        }
    }

    private fun validateCurrentInput() {
        val currentText = inputText.text.toString()
        val number = PScreenValidator.tryExtractNumber(currentText, inputTextSpec!!.type)
        if (number != null) {
            val validated = validateNumber(number, inputTextSpec!!.additional.domain)
            if (validated) {
                inputText.error = null
            } else {
                inputText.error = "не в обл. определения"
            }
        }else {
            inputText.error = "не число"
        }
    }

    private fun rebind(spec: PTypeSpecific) {
        inputTextSpec = spec as InputTextSpec
        setDefaultIfNotSet()
        inputTextSpec!!.additional.answer?.let { inputText!!.setText(it) }
            ?: kotlin.run { inputText!!.setText("") }

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    Timber.i("Got string: $s")
                    inputTextSpec!!.additional.answer = it.toString()
                    validateCurrentInput()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        inputText.addTextChangedListener(textWatcher)
        titleText.text = inputTextSpec!!.title
        inputLayout.hint = buildHint()
        inputTextSpec!!.additional.error?.let {
            inputText.error = inputTextSpec!!.additional.error
        }
    }

    private fun setDefaultIfNotSet() {
        if (inputTextSpec!!.additional.answer == null && inputTextSpec!!.default != null) {
            inputTextSpec!!.additional.answer = inputTextSpec!!.default
        }
    }


    private fun pickNumberHint(inputType: InputTextType): String {
        return when (inputType) {
            InputTextType.FLOAT -> inputText.context.getString(R.string.float_hint)
            InputTextType.INTEGER -> inputText.context.getString(R.string.integer_hint)
        }
    }

    private fun buildHint(): String {
        val givenTextHint = inputTextSpec!!.additional.hint
        val inputTypeHint = inputTextSpec!!.type.let { pickNumberHint(it) }
        val rangeHint = constructRangeHint(inputTextSpec!!.additional.domain)

        if (givenTextHint != null) {
            stringBuilder.append("$givenTextHint, ")
        }
        stringBuilder.append(inputTypeHint)
        if (rangeHint != null) {
            stringBuilder.append(", $rangeHint")
        }
        return stringBuilder.toString()
    }

    private fun constructRangeHint(domain: TwoSidedDomain?): String? {
        return domain?.unequalityNotation()
    }

    companion object : CompanionInflater {
        override fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup): View {
            return inflater.inflate(R.layout.input_text_pfield, viewGroup, false)
        }
    }
}