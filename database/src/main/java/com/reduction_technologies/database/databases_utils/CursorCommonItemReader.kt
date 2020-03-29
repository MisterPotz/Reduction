package com.reduction_technologies.database.databases_utils

import android.database.Cursor
import com.reduction_technologies.database.databases_utils.CommonItem.Companion.ABOUT
import com.reduction_technologies.database.databases_utils.CommonItem.Companion.ADDITIONAL
import com.reduction_technologies.database.databases_utils.CommonItem.Companion.TAG
import com.reduction_technologies.database.databases_utils.CommonItem.Companion.TITLE
import com.reduction_technologies.database.databases_utils.CommonItem.Companion._ID
import com.reduction_technologies.database.helpers.Repository

/**
 * Knows how to read a cursor at given position to produce a CommonItem object.
 * @See CommonItem
 */
object CursorCommonItemReader : Repository.ItemReader<CommonItem> {
    override fun readItem(cursor: Cursor): CommonItem {
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
}