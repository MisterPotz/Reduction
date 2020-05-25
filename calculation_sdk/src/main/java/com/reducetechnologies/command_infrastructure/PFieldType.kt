package com.reducetechnologies.command_infrastructure

enum class PFieldType(val needsInput : Boolean) {
    INPUT_TEXT(true),
    INPUT_PICTURE(true),
    INPUT_LIST(true),

    MATH_TEXT(false),
    TEXT(false),
    PICTURE(false),
    LINK(false)
}

fun Int.toPFieldType() : PFieldType {
    return PFieldType.values()[this]
}