package com.reducetechnologies.command_infrastructure

fun PField.needsInput() : Boolean {
    return pFieldType.needsInput
}

fun PScreen.needsInput() : Boolean {
    return fields.fold(false) { left, right ->
        left || right.needsInput()
    }
}