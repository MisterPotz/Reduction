package com.reduction_technologies.database.databases_utils

import android.database.Cursor

/**
 * Represents an ability to obtain some kind of item from the current cursor position
 */
interface ItemReader<T> {
    fun readItem(cursor: Cursor): T
}