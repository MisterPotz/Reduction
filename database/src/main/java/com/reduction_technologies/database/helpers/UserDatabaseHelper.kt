package com.reduction_technologies.database.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.reduction_technologies.database.databases_utils.*

/**
 * DataBase helper for data set with user-defined data. Only user frequent changing data can be stored here.
 * @see ConstantDatabaseHelper
 */
internal class UserDatabaseHelper(val context: Context) :
    SQLiteOpenHelper(context, database.title, null, database.version),
    CursorBuilder {
    // TODO Надо как-то элегантнее сделать либо соответствие датабейза таблице либо организацию
    // работы с запросом таблиц посредством хелпера
    private val SQL_CREATE_ENTRIES =
        CommonItem.createTableWithContract(database.tables[UserTables.Favorites]!!.name)

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${database.tables[UserTables.Favorites]}"

    override fun getWritableDatabase(): SQLiteDatabase {
        throw RuntimeException("The ${database.title} database is not writable.")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Nothing to do
    }

    companion object :
        DatabaseConstantsContract {
        override val database: DatabaseType =
            DatabaseType.User
    }

    override fun getCommonCursorBuilder(
        tableName: String,
        columns: Array<String>
    ): RCursorAdapterBuilder<CommonItem> {
        return RCursorAdapterBuilder(this, tableName, columns)
    }
}