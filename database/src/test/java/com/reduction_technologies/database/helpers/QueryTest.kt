package com.reduction_technologies.database.helpers

import android.database.sqlite.SQLiteOpenHelper
import com.reduction_technologies.database.databases_utils.CommonItem
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException

internal class QueryTest {
    // Tests basic query building behavior
    @Test
    fun queryBuilderTest() {
        val sqlHelper = mockk<SQLiteOpenHelper>()
        val builder = RCursorAdapterBuilder<CommonItem>(sqlHelper, "table", arrayOf("col1", "col2"))

        builder.buildQuery {
            When(Query.Clause("NAME", Query.Operations.EQ, "five"))
            and(Query.Clause("TAG", Query.Operations.EQ, "table"))
            or(Query.Clause("ADDITIONAL", Query.Operations.GREATER, "4"))
        }

        val query = builder.query!!.getQuery()

        val expectedQuery = QueryParameters(
            "table", arrayOf("col1", "col2"),
            "WHEN NAME = ? AND TAG = ? OR ADDITIONAL > ?", arrayOf("five", "table", "4")
        )

        println(expectedQuery)
        println(query)
        assertEquals(expectedQuery, query)
    }

    @Test
    fun fails_when_necessary() {
        val sqlHelper = mockk<SQLiteOpenHelper>()
        val builder = RCursorAdapterBuilder<CommonItem>(sqlHelper, "table", arrayOf("col1", "col2"))

        val assertable: () -> RCursorAdapterBuilder<CommonItem> = {
            builder.buildQuery {
                // Here and goes as first - must fail
                and(Query.Clause("TAG", Query.Operations.EQ, "table"))
                or(Query.Clause("ADDITIONAL", Query.Operations.GREATER, "4"))
            }
        }

        assertThrows<IllegalStateException>(
            "Giving first condition via 'and' is not allowed by SQL standards"
        ) { assertable() }
    }
}