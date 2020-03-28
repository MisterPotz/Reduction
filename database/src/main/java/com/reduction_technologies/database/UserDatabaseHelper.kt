package com.reduction_technologies.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DataBase helper for data set with user-defined data. Only user frequent changing data can be stored here.
 * @see ConstantDatabaseHelper
 */
class UserDatabaseHelper(val context: Context) :
    SQLiteOpenHelper(context, database.title, null, database.version) {
    // TODO Надо как-то элегантнее сделать либо соответствие датабейза таблице либо организацию
    // работы с запросом таблиц посредством хелпера
    private val SQL_CREATE_ENTRIES = CommonItem.createTableWithContract(database.tables[UserTables.Favorites]!!.name)

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

    companion object : DatabaseConstantsContract {
        override val database: DatabaseType = DatabaseType.User
    }
}