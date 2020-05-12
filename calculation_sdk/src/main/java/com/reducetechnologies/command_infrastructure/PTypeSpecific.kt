package com.reducetechnologies.command_infrastructure

import com.reduction_technologies.database.tables_utils.DomainDefinable
import com.reduction_technologies.database.tables_utils.DomainDefinableFloat
import com.reduction_technologies.database.tables_utils.OneSidedDomain
import com.reduction_technologies.database.tables_utils.TwoSidedDomain

/**
 * Contains data that depends on [PFieldType]
 */
interface PTypeSpecific

// InputText
enum class InputTextType {
    INTEGER, FLOAT
}

/**
 * [hint] - can contain some additional data (like, this value is measured in kylonewtons, etc.)
 * [domain] - defines verification logics (validation of user input)
 */
data class AdditionalInputText(val encyclodpediaId: Int? = null,
                               var answer: String? = null,
                               var error: String? = null,
                               val hint: String? = null,
                               val domain : TwoSidedDomain? = null)

data class InputTextSpec(
    val title: String,
    val type: InputTextType,
    val default: String?,
    val additional: AdditionalInputText
) : PTypeSpecific

// InputImage
data class AdditionalInputImage(val imagePaths: List<String>, val encyclodpediaId: Int? = null, var answer: Int? = null)

data class InputPictureSpec(
    val title: String,
    val default: Int?,
    val additional: AdditionalInputImage
) : PTypeSpecific

// InputList
data class AdditionalInputList(val options: List<String>, val encyclodpediaId: Int? = null, var answer: Int? = null, val hint: String? = null)

data class InputListSpec(
    val title: String,
    val default: Int?,
    val additional: AdditionalInputList
) : PTypeSpecific

// TextField
enum class TextType{
    BODY, HEADLINE
}

data class AdditionalText(val type: TextType, val encyclodpediaId: Int? = null)

data class TextSpec(val text: String, val additional: AdditionalText = AdditionalText(TextType.BODY)) : PTypeSpecific

// Math text field
/**
 * [text] is formatted as per katex documentation
 */
data class AdditionalMathText(val stub : Int = 0)
data class MathTextSpec(var text: String, val mathTextField: AdditionalMathText = AdditionalMathText()) : PTypeSpecific

// Picture
enum class PictureSourceType {
    PATH, TABLE_ID
}

interface PictureSource
data class PictureStringPath(val string: String) : PictureSource
data class PictureDataTable(val id: Int) : PictureSource

data class PictureSpec(val pictureSourceType: PictureSourceType, val source: PictureSource) :
    PTypeSpecific