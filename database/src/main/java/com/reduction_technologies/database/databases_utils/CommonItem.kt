package com.reduction_technologies.database.databases_utils

import android.content.ContentValues

interface HasId {
    fun getId() : Int
}

/**
 * Most common item that is contained in a row of tables: EncyclopediaItems and Favorites
 */
// TODO must serve like a prepared instance to be displayed with already localized texts
class CommonItem(
    var id: Int? = null,
    var title: String,
    var tag: String,
    var about: String? = null,
    var additional: String? = null,
    var textKey : String? = null, // for some items it may be more convenient to store with unique text key
    var mathTitle : String? = null // some items have math formulas in their titles - this field is to address that problem
) : HasId {

    fun getContentValues(): ContentValues {
        return ContentValues().apply {
            put(TITLE, title)
            put(TAG, tag)
            about?.let { put(ABOUT, about) }
            additional?.let { put(ADDITIONAL, additional) }
            textKey?.let { put(TEXT_KEY, textKey)}
            mathTitle?.let { put(MATH_TITLE, mathTitle) }
        }
    }

    // Compares to different CommonItems
    fun compare(item: CommonItem): Boolean {
        return title == item.title &&
            tag == item.tag &&
            about == item.about &&
            additional == item.additional &&
                textKey == item.textKey &&
                mathTitle == item.mathTitle
    }

    companion object {
        val TITLE = Columns.TITLE.castString()
        val TAG = Columns.TAG.castString()
        val ABOUT = Columns.ABOUT.castString()
        val ADDITIONAL = Columns.ADDITIONAL.castString()
        val _ID = Columns.ID.castString()
        val TEXT_KEY = Columns.TEXT_KEY.castString()
        val MATH_TITLE = Columns.MATH_TITLE.castString()

        // TODO make different helpers, that will adapt commonitem to the necessary
        //  condition (e.g. wrap CommonItem so a table element can be obtained)
        fun instance(contentValues: ContentValues): CommonItem {
            return CommonItem(
                title = contentValues.getAsString(TITLE),
                tag = contentValues.getAsString(TAG),
                about = contentValues.getAsString(ABOUT),
                additional = contentValues.getAsString(ADDITIONAL),
                textKey = contentValues.getAsString(TEXT_KEY),
                mathTitle = contentValues.getAsString(MATH_TITLE)
            )
        }

        /**
         * Probably it is better to save into user database not items themselves but ids from original database
         */
        fun createTableWithContract(tableName: String): String {
            return "CREATE TABLE ${tableName} (" +
                    "$_ID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                    "$TITLE TEXT NOT NULL," +
                    "$TAG TEXT NOT NULL," +
                    "$ABOUT TEXT, " +
                    "$ADDITIONAL TEXT, " +
                    "$TEXT_KEY TEXT UNIQUE," +
                    "$MATH_TITLE TEXT " +
                    ");"
        }
    }

    override fun getId(): Int {
        return id!!
    }
}