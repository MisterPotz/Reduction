package com.reduction_technologies.database.databases_utils

import android.content.ContentValues

/**
 * Most common item that is contained in a row of tables: EncyclopediaItems and Favorites
 */
class CommonItem(
    var id: Int? = null,
    var title: String,
    var tag: String,
    var about: String? = null,
    var additional: String? = null
) {

    fun getContentValues(): ContentValues {
        return ContentValues().apply {
            put(TITLE, title)
            put(TAG, tag)
            about?.let { put(ABOUT, about) }
            additional?.let { put(ADDITIONAL, additional) }
        }
    }

    // Compares to different CommonItems
    fun compare(item: CommonItem): Boolean {
        return title == item.title &&
            tag == item.tag &&
            about == item.about &&
            additional == item.additional
    }

    companion object {
        val TITLE = Columns.TITLE.castString()
        val TAG = Columns.TAG.castString()
        val ABOUT = Columns.ABOUT.castString()
        val ADDITIONAL = Columns.ADDITIONAL.castString()
        val _ID = Columns.ID.castString()

        // TODO make different helpers, that will adapt commonitem to the necessary
        //  condition (e.g. wrap CommonItem so a table element can be obtained)
        fun instance(contentValues: ContentValues): CommonItem {
            return CommonItem(
                title = contentValues.getAsString(TITLE),
                tag = contentValues.getAsString(TAG),
                about = contentValues.getAsString(ABOUT),
                additional = contentValues.getAsString(ADDITIONAL)
            )
        }

        fun createTableWithContract(tableName: String): String {
            return "CREATE TABLE ${tableName} (" +
                    "$_ID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                    "$TITLE TEXT NOT NULL," +
                    "$TAG TEXT NOT NULL" +
                    "$ABOUT TEXT, " +
                    "$ADDITIONAL TEXT)"
        }
    }
}