package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import com.reducetechnologies.command_infrastructure.*
import com.reduction_technologies.database.tables_utils.TwoSidedDomain
import timber.log.Timber
import java.lang.NumberFormatException

object PScreenValidator {
    fun validatePictureSelection(spec: InputPictureSpec) : Boolean {
        return spec.additional.answer?.let {
            it < spec.additional.imagePaths.size && it > -1
        } ?: false
    }

    fun validateListSelection(spec: InputListSpec) : Boolean {
        return spec.additional.answer?.let {
            it < spec.additional.options.size && it > -1
        } ?: false
    }

    fun validateInputTextSelection(spec: InputTextSpec) : Boolean {
        return spec.additional.answer?.let {
            val number = tryExtractNumber(it, spec.type)
            number != null && validateNumber(number, spec.additional.domain)
        } ?: false
    }

    fun validatePScreen(pScreen: PScreen) : Boolean {
        return pScreen.fields.foldIndexed(true) { i, prev, pField ->
            val result = prev && when (pField.pFieldType) {
                PFieldType.INPUT_TEXT -> validateInputTextSelection(pField.typeSpecificData as InputTextSpec)
                PFieldType.INPUT_PICTURE -> validatePictureSelection(pField.typeSpecificData as InputPictureSpec)
                PFieldType.INPUT_LIST -> validateListSelection(pField.typeSpecificData as InputListSpec)
                else -> true
            }
            Timber.i("For $i it is $result")
            result
        }
    }

    fun validateNumber(float: Float, domain: TwoSidedDomain?): Boolean {
        return domain?.isInDomain(float) ?: false
    }

    fun tryExtractNumber(string: String, type: InputTextType): Float? {
        var extracted: Boolean = false
        var number: Float = 0.0F
        when (type) {
            InputTextType.FLOAT -> try {
                number = string.toFloat()
                extracted = true
            } catch (e: NumberFormatException) {
            }
            InputTextType.INTEGER -> try {
                number = string.toInt().toFloat()
                extracted = true
            } catch (e: NumberFormatException) {
            }
        }
        return if (extracted) number else null
    }
}