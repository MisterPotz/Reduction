package com.reduction_technologies.database

import android.content.ContentValues

class CommonItem{

    lateinit var title: String
    lateinit var tag: String
    var about: String? = null
    var additional: String? = null

    fun getContentValues(): ContentValues {
        return ContentValues().apply {
            put(COLUMN_NAME_TITLE, title)
            put(COLUMN_NAME_TAG, tag)
            about?.let { put(COLUMN_NAME_ABOUT, about) }
            additional?.let { put(COLUMN_NAME_ADDITIONAL, additional) }
        }
    }

    companion object {
        const val COLUMN_NAME_TITLE = "TITLE"
        const val COLUMN_NAME_TAG = "TAG"
        const val COLUMN_NAME_ABOUT = "ABOUT"
        const val COLUMN_NAME_ADDITIONAL = "ADDITIONAL"
        const val _ID = "_ID"

        // TODO make different helpers, that will adapt commonitem to the necessary
        //  condition (e.g. wrap CommonItem so a table element can be obtained)
        fun instance(contentValues: ContentValues): CommonItem {
            return CommonItem().apply {
                title = contentValues.getAsString(COLUMN_NAME_TITLE)
                tag = contentValues.getAsString(COLUMN_NAME_TAG)
                about = contentValues.getAsString(COLUMN_NAME_ABOUT)
                additional = contentValues.getAsString(COLUMN_NAME_ADDITIONAL)
            }
        }

        fun createTableWithContract(tableName: String): String {
            return "CREATE TABLE ${tableName} (" +
                    "${_ID} INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                    "${COLUMN_NAME_TITLE} TEXT NOT NULL," +
                    "${COLUMN_NAME_TAG} TEXT NOT NULL" +
                    "${COLUMN_NAME_ABOUT} TEXT, " +
                    "${COLUMN_NAME_ADDITIONAL} BLOB)"
        }
    }
}