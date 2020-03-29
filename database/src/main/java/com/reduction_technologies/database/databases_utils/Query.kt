package com.reduction_technologies.database.databases_utils

import timber.log.Timber
import java.lang.IllegalStateException
import java.lang.StringBuilder

/**
 * Consider Query as a DSL for SQLite queries.
 * @See QueryParameters
 *
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