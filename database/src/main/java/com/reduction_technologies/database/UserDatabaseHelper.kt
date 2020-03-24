package com.reduction_technologies.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DataBase helper for data set with user-defined data. Only user frequent changing data can be stored here.
 * @see ConstantDatabaseHelper
 */
class UserDatabaseHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_ENTRIES  = "CREATE TABLE ${TABLE_NAME} (" +
            "${_ID} INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
            "${COLUMN_NAME_TITLE} TEXT NOT NULL," +
            "${COLUMN_NAME_ABOUT} TEXT, " +
            "${COLUMN_NAME_ADDITIONAL} BLOB)"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TABLE_NAME}"

    override fun getWritableDatabase(): SQLiteDatabase {
        throw RuntimeException("The $DATABASE_NAME database is not writable.")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // TODO must create database
        db?.execSQL(SQL_CREATE_ENTRIES)
        // Nothing to do
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Nothing to do
    }

    companion object {
        const val DATABASE_NAME = "userDatabase"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "FavoritesItems"

        const val COLUMN_NAME_TITLE = "TITLE"
        const val COLUMN_NAME_ABOUT = "ABOUT"
        const val COLUMN_NAME_ADDITIONAL = "ADDITIONAL"
        const val _ID = "_ID"
    }
}