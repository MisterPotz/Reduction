package com.reducetechnologies.command_infrastructure

fun PField.needsInput() : Boolean {
    return pFieldType.needsInput
}

fun PScreen.needsInput() : Boolean {
    return fields.fold(false) { left, right ->
        left || right.needsInput()
    }
}

fun PField.hasLink() : Boolean {
    return this.pFieldType == PFieldType.LINK
}

fun PScreen.hasLinks() : Boolean {
    return fields.fold(false) {left, right ->
        left || right.hasLink()
    }
}