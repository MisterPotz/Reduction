package com.reduction_technologies.database.databases_utils

import android.database.Cursor
import com.reduction_technologies.database.databases_utils.CommonItem.Companion.ABOUT
import com.reduction_technologies.database.databases_utils.CommonItem.Companion.ADDITIONAL
import com.reduction_technologies.database.databases_utils.CommonItem.Companion.TAG
import com.reduction_technologies.database.databases_utils.CommonItem.Companion.TITLE
import com.reduction_technologies.database.databases_utils.CommonItem.Companion._ID

/**
 * Knows how to read a cursor at given position to produce a commonitemobject.
 */
object CursorCommonItemReader {

    /**
     * Creates commonitem from cursor at given position
     */
    fun readCursor(cursor: Cursor, position: Int): CommonItem {
        cursor.moveToPosition(position)

        val id = cursor.getIntK(_ID)
        val title = cursor.getStringK(TITLE)
        val about = cursor.getStringK(ABOUT)
        val tag = cursor.getStringK(TAG)
        val additional =
            cursor.getStringK(ADDITIONAL).let { if (it?.isEmpty() != false) null else it }

        return CommonItem(
            id = id,
            about = about,
            tag = tag!!,
            title = title!!,
            // Decoding bytearray to string
            additional = additional
        )
    }

    private fun Cursor.getIntK(item: String): Int {
        return getInt(getColumnIndex(item))
    }

    private fun Cursor.getStringK(item: String): String? {
        return getString(getColumnIndex(item))
    }

    private fun Cursor.getBlobK(item: String): ByteArray? {
        return getBlob(getColumnIndex(item))?.let {
            // Checking if bytearray is malformed and last value is 0
            if (it.last() == 0.toByte()) {
                it.set(it.lastIndex, 20.toByte())
            }
            it
        }
    }

}