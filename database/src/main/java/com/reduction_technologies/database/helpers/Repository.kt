package com.reduction_technologies.database.helpers

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.reduction_technologies.database.databases_utils.ConstTables
import com.reduction_technologies.database.databases_utils.DatabaseType
import com.reduction_technologies.database.databases_utils.toTypedArray
import timber.log.Timber
import java.lang.IllegalStateException
import java.lang.StringBuilder
import javax.inject.Inject

/**
 * The purpose of this class is to provide the rest code of application with useful data related
 * to GOST tables, encyclopedia, and user favorite items.
 * Some fields are in
 */
class Repository @Inject constructor(
    val context: Context,
    /**
     * THe field is injectable so instances of constant database can be mocked
     */
    val constantDatabaseHelper: ConstantDatabaseHelper,
    /**
     * Injectible for the sake of testing and reusability
     */
    val userDatabaseHelper: UserDatabaseHelper
) {
    /**
     * Returns [T] data, read from cursor.
     * Can be used for other purposes also
     */
    class RCursorAdapter<T> internal constructor(
        val cursor: Cursor,
        val reader: ItemReader<T>
    ) {
        fun getItem(position: Int): T {
            cursor.moveToPosition(position)
            return reader.readItem(cursor)
        }
    }

    interface ItemReader<T> {
        fun readItem(cursor: Cursor): T
    }

    // TODO обложить тестами выдачу курсора через билдер И последующий запрос в реальную базу
    fun <T> constCursorBuilder(
        tableName: String =
            DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!.name,
        columns: Array<String> =
            DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!.columns.toTypedArray()
    ): RCursorAdapterBuilder<T> {
        return RCursorAdapterBuilder(constantDatabaseHelper, tableName, columns)
    }
}


/**
 *  Constructs a cursors with given query and returns it as wrapped into RCursorAdapter
 *  Requires an ItemReader to be set in order to give RCursorAdapter possibility to
 *  read items
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

    fun create(): Repository.RCursorAdapter<T> {
        val cursor = databaseHelper
            .readableDatabase.query(query!!.getQuery())
        return Repository.RCursorAdapter(cursor, reader!!)
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

/**
 * Currently, [Query] supports the following features:
 * 1) Building linear AND, OR conditions
 * 2) Each condition can be:  =, >=, <=, <>, >, <
 * with respect to the given target
 * [Query] can be used as independent entrty to construct query bodies for sqlite
 * Client using this query can setup thus query using apply block.
 * Once database name is given - it cannot be changes for the sake of safety
 * The same rule applies to column names.
 */
class Query {
    private var tableName: String? = null
    private var columnNames: Array<String>? = null
    private val clauseList: MutableList<Clause> = mutableListOf()
    private val connectors: MutableList<Connectors> = mutableListOf()

    enum class Connectors(val symbol: String) { AND("AND"), OR("OR") }


    data class Clause(
        val selectionName: String,
        val operation: Operations,
        // Later will be passed as the question
        val target: String
    )

    enum class Operations(val symbol: String) {
        EQ("="), LESS("<"), EQLESS("<="),
        EQGREATER(">="), GREATER(">"), NEQ("<>")
    }

    infix fun table(name: String): Query {
        if (tableName == null) {
            tableName = name
        } else {
            Timber.w("Can't re-set given database name")
        }
        return this
    }

    infix fun columns(columns: Array<String>): Query {
        if (columnNames == null) {
            columnNames = columns
        } else {
            Timber.w("Can't re-set given database column names")
        }
        return this
    }


    infix fun When(firstClause: Clause): Query {
        clauseList.add(firstClause)
        return this
    }

    infix fun and(nextClause: Clause): Query {
        if (clauseList.isEmpty()) throw IllegalStateException("Giving first condition via 'and' is not allowed by SQL standards")
        clauseList.add(nextClause)
        connectors.add(Connectors.AND)
        return this
    }

    infix fun or(nextClause: Clause): Query {
        if (clauseList.isEmpty()) throw IllegalStateException("Giving first condition via 'or' is not allowed by SQL standards")
        clauseList.add(nextClause)
        connectors.add(Connectors.OR)
        return this
    }

    private fun StringBuilder.space() {
        append(" ")
    }

    fun getClauseList(): List<Clause> = clauseList
    fun getConnectors(): List<Connectors> = connectors

    // Builds selection query
    fun getQuery(): QueryParameters {
        val selectionArgs = if (clauseList.isEmpty()) null else mutableListOf<String>()

        val selection = if (clauseList.isEmpty()) null else {
            val clauseList = getClauseList()
            val connectorsIterator = getConnectors().iterator()
            val stringBuilder = StringBuilder()
            clauseList.forEach {
                stringBuilder.apply {
                    space()
                    append(it.selectionName)
                    space()
                    stringBuilder.append(it.operation.symbol)
                    space()
                    stringBuilder.append("?")
                    selectionArgs!!.add(it.target)
                    if (connectorsIterator.hasNext()) {
                        append(" ")
                        append(connectorsIterator.next().symbol)
                    }
                }
            }
            stringBuilder.toString()
        }


        return QueryParameters(
            table = checkNotNull(tableName) { "Table name was not set" },
            columnNames = checkNotNull(columnNames) { "Table column names were not set" },
            selection = selection,
            selectionArgs = selectionArgs?.toTypedArray(),
            groupBy = null,
            having = null,
            orderBy = null
        )
    }
}

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