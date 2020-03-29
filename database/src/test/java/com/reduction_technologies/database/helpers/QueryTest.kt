package com.reduction_technologies.database.helpers

import com.reduction_technologies.database.databases_utils.CommonItem
import org.junit.jupiter.api.Test
import com.reduction_technologies.database.helpers.RCursorAdapterBuilder.Query.Clause
import com.reduction_technologies.database.helpers.RCursorAdapterBuilder.Query.Operations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*

internal class RepositoryTest {
    // Tests basic query building behavior
    @Test
    fun queryBuilderTest() {
        val builder = RCursorAdapterBuilder<CommonItem>()

        builder.buildQuery {
            When(Clause("NAME", Operations.EQ, "five"))
            and(Clause("TAG", Operations.EQ, "table"))
            or(Clause("ADDITIONAL", Operations.GREATER, "4"))
        }

        val query = builder.getQueryBody()

        assertEquals("WHEN NAME = ? AND TAG = ? OR ADDITIONAL > ?", query!!.first)

        assertTrue(Arrays.equals(arrayOf("five", "table", "4"), query!!.second))
    }
}