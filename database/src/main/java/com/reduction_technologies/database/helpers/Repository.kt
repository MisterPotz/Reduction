package com.reduction_technologies.database.helpers

import android.content.Context
import android.database.Cursor
import com.reduction_technologies.database.databases_utils.*
import org.junit.jupiter.api.Assertions.assertTrue
import javax.inject.Inject

/**
 * The purpose of this class is to provide the rest code of application with useful data related
 * to GOST tables, encyclopedia, and user favorite items.
 * Some fields are in
 */
class Repository @Inject constructor(
    internal val context: Context,
    /**
     * THe field is injectable so instances of constant database can be mocked
     */
    val constantDatabaseHelper: ConstantDatabaseHelper,
    /**
     * Injectible for the sake of testing and reusability
     */
    val userDatabaseHelper: UserDatabaseHelper
) {
    /**
     * Returns [T] data, read from cursor.
     * Can be used for other purposes also
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
            assertTrue(cursor.count == 1)
            cursor.moveToPosition(0)
            return getItem(0)
        }
    }

    /**
     * Represents an ability to obtain some kind of item from the current cursor position
     */
    interface ItemReader<T> {
        fun readItem(cursor: Cursor): T
    }

    // TODO обложить тестами выдачу курсора через билдер И последующий запрос в реальную базу
    fun <T> constCursorBuilder(
        tableName: String = constMainTable().name,
        columns: Array<String> = constMainTable().columns.toTypedArray()
    ): RCursorAdapterBuilder<T> {
        return RCursorAdapterBuilder(
            constantDatabaseHelper,
            tableName,
            columns
        )
    }
}


