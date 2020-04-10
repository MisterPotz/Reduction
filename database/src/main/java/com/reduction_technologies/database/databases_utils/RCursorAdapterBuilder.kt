package com.reduction_technologies.database.databases_utils

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.reduction_technologies.database.helpers.Repository

/**
 *  Constructs a cursors with given query and returns it as wrapped into RCursorWrapper
 *  Requires an ItemReader to be set in order to give RCursorWrapper possibility to
 *  read items.
 *  Must be used wit entity, having a link to databaseHelper.
 *  Example usage:
 *  @See Repository
 */
class RCursorAdapterBuilder<T> internal constructor(
    val databaseHelper: SQLiteOpenHelper,
    val table: String,
    val columns: Array<String>
) {
    private var reader: Repository.ItemReader<T>? = null
    private var cursor: Cursor? = null

    // TODO if query is null - just request all SQL fields
    var query: Query? = null
        private set

    // Sets reader
    fun setReader(reader: Repository.ItemReader<T>): RCursorAdapterBuilder<T> {
        this.reader = reader
        return this
    }

    // Builds query
    fun buildQuery(builder: Query.() -> Unit): RCursorAdapterBuilder<T> {
        val query = Query()
        query.apply(builder)
        query.apply {
            table(this@RCursorAdapterBuilder.table)
            columns(this@RCursorAdapterBuilder.columns)
        }
        this.query = query
        return this
    }

    fun create(): Repository.RCursorWrapper<T> {
        val cursor = databaseHelper
                // If query was empty builds creates a query returning all rows
            .readableDatabase.query(
                query?.getQuery() ?: QueryParameters(
                    table,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
        return Repository.RCursorWrapper(
            cursor,
            reader!!
        )
    }

    fun SQLiteDatabase.query(parameters: QueryParameters): Cursor {
        return parameters.let {
            query(
                it.table,
                it.columnNames,
                it.selection,
                it.selectionArgs,
                it.groupBy,
                it.having,
                it.orderBy
            )
        }
    }
}