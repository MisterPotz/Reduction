package com.reduction_technologies.database.databases_utils

import android.database.Cursor
import com.reduction_technologies.database.helpers.Repository
import org.junit.jupiter.api.Assertions

/**
 * Returns [T] data, read from cursor.
 * Can be used for other purposes also
 * This class is only for internal database usage
 */
class RCursorWrapper<T> internal constructor(
    val cursor: Cursor,
    val reader: ItemReader<T>
) {
    fun getItem(position: Int): T {
        cursor.moveToPosition(position)
        return reader.readItem(cursor)
    }

    /**
     * In case you also need to check that such entry is single (after searching conditions)
     * you may want to use this function
     */
    fun getSingle(): T {
        Assertions.assertTrue(cursor.count == 1)
        cursor.moveToPosition(0)
        return getItem(0)
    }

    fun getList(): List<T> {
        val list: MutableList<T> = mutableListOf()
        for (i in 0 until cursor.count) {
            list.add(getItem(i))
        }
        return list
    }
}

// For database helpers, they should have this method to build necessary queries
interface CursorBuilder {
    fun getCommonCursorBuilder(tableName: String,
                               columns: Array<String>) : RCursorAdapterBuilder<CommonItem>
}