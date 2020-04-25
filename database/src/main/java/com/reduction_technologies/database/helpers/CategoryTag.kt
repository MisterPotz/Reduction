package com.reduction_technologies.database.helpers

import com.reduction_technologies.database.utils.Positionable

enum class CategoryTag(val title : String) : Positionable {
    TABLE("table") {
        override fun getPosition() = 0
    },
    VARIABLE("variable") {
        override fun getPosition() = 1
    }
}