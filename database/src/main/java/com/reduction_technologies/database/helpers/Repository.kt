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
class Repository @Inject internal constructor(
    internal val context: Context,
    /**
     * THe field is injectable so instances of constant database can be mocked
     */
    internal val constantDatabaseHelper: ConstantDatabaseHelper,
    /**
     * Injectible for the sake of testing and reusability
     */
    internal val userDatabaseHelper: UserDatabaseHelper
) {
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
            assertTrue(cursor.count == 1)
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

    /**
     * Represents an ability to obtain some kind of item from the current cursor position
     */
    interface ItemReader<T> {
        fun readItem(cursor: Cursor): T
    }

    // TODO обложить тестами выдачу курсора через билдер И последующий запрос в реальную базу
    fun <T> constCursorBuilder(
        tableName: String,
        columns: Array<String>
    ): RCursorAdapterBuilder<T> {
        val tableContract = constMainTable()
        return cursorBuilder(
            DatabaseType.Constant,
            tableContract.name,
            tableContract.columns.toTypedArray()
        )
    }


    fun <T> userCursorBuilder(
        tableName: String,
        columns: Array<String>
    ): RCursorAdapterBuilder<T> {
        val tableContract = userMainTable()
        return cursorBuilder(
            DatabaseType.User,
            tableContract.name,
            tableContract.columns.toTypedArray()
        )
    }

    fun <T> cursorBuilder(
        databaseType: DatabaseType,
        tableName: String,
        columns: Array<String>
    ): RCursorAdapterBuilder<T> {
        return when (databaseType) {
            DatabaseType.Constant ->
                RCursorAdapterBuilder(constantDatabaseHelper, tableName, columns)
            DatabaseType.User ->
                RCursorAdapterBuilder(userDatabaseHelper, tableName, columns)
        }
    }

    // Specifying database and table parameters
    fun getAllItemsFrom(
        databaseType: DatabaseType,
        tableContract: TableContract
    ): List<CommonItem> {
        val cursor : RCursorWrapper<CommonItem> = cursorBuilder<CommonItem>(
            databaseType,
            tableContract.name,
            tableContract.columns.toTypedArray()
        )
            .setReader(CursorCommonItemReader).create()
        return cursor.getList()
    }
}


