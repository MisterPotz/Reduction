package com.reduction_technologies.database.databases_utils

/**
 * Incapsulates all the parameters that are used in SQLite query on a database for receiving a cursor
 */
data class QueryParameters(
    val table: String,
    val columnNames: Array<String>,
    val selection: String? = null,
    val selectionArgs: Array<String>? = null,
    val groupBy: String? = null,
    val having: String? = null,
    val orderBy: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QueryParameters

        if (table != other.table) return false
        if (!columnNames.contentEquals(other.columnNames)) return false
        if (selection != other.selection) return false
        if (selectionArgs != null) {
            if (other.selectionArgs == null) return false
            if (!selectionArgs.contentEquals(other.selectionArgs)) return false
        } else if (other.selectionArgs != null) return false
        if (groupBy != other.groupBy) return false
        if (having != other.having) return false
        if (orderBy != other.orderBy) return false

        return true
    }

    override fun hashCode(): Int {
        var result = table.hashCode()
        result = 31 * result + columnNames.contentHashCode()
        result = 31 * result + (selection?.hashCode() ?: 0)
        result = 31 * result + (selectionArgs?.contentHashCode() ?: 0)
        result = 31 * result + (groupBy?.hashCode() ?: 0)
        result = 31 * result + (having?.hashCode() ?: 0)
        result = 31 * result + (orderBy?.hashCode() ?: 0)
        return result
    }
}