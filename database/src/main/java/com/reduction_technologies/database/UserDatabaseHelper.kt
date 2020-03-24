package com.reduction_technologies.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DataBase helper for data set with user-defined data. Only user frequent changing data can be stored here.
 * @see ConstantDatabaseHelper
 */
class UserDatabaseHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_ENTRIES  = CommonItemContract.createTableWithContract(TABLE_NAME)

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TABLE_NAME}"

    override fun getWritableDatabase(): SQLiteDatabase {
        throw RuntimeException("The $DATABASE_NAME database is not writable.")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Nothing to do
    }

    companion object {
        const val DATABASE_NAME = "userDatabase"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "FavoritesItems"
    }
}