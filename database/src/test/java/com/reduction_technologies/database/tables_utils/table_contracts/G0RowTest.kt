package com.reduction_technologies.database.tables_utils.table_contracts

import com.google.gson.GsonBuilder
import com.reduction_technologies.database.json_utils.RuntimeTypeAdapterFactory
import com.reduction_technologies.database.tables_utils.OneSidedDomain
import com.reduction_technologies.database.tables_utils.TwoSidedDomain
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class G0RowTest {
    /**
     * Checking how well gson performs
     */
    @Test
    fun serialization() {
        val grow = G0Row(
            domain = TwoSidedDomain(OneSidedDomain(">", 10f), OneSidedDomain("<", 20f)),
            skew = false, list = listOf(null, 37f, 48f, 64f, 73f, 100f)
        )
        val gson = GsonBuilder()
            .create()

        val json = gson.toJson(grow, G0Row::class.java)
        println(json)

        val grow2 = gson.fromJson(json, G0Row::class.java)
        assertEquals(grow, grow2)
    }

    data class InnerTestable(val str: String)

    data class Testable(val otherMeaning: Int, val list: List<Float?>, val inner: InnerTestable)

    /**
     * Testing data classes ability to derive equality / hashCode methods
     */
    @Test
    fun listEqualityData() {
        val inner1 = InnerTestable("val")
        val inner2 = InnerTestable("val")

        val list1 = listOf<Float?>(null, 37f, 48f, 64f, 73f, 100f)
        val list2 = listOf<Float?>(null, 37f, 48f, 64f, 73f, 100f)
        val testable1 = Testable(3, list1, inner1)
        val testable2 = Testable(3, list2, inner2)

        assertEquals(list1, list2)
        assertEquals(testable1, testable2)
    }

    @Test
    fun equality() {
        val grow = G0Row(
            domain = TwoSidedDomain(OneSidedDomain(">", 10f), OneSidedDomain("<", 20f)),
            skew = false, list = listOf(null, 37f, 48f, 64f, 73f, 100f)
        )

        val grow2 = G0Row(
            domain = TwoSidedDomain(OneSidedDomain(">", 10f), OneSidedDomain("<", 20f)),
            skew = false, list = listOf(null, 37f, 48f, 64f, 73f, 100f)
        )
        assertEquals(grow, grow2)
    }

    @Test
    fun tablePerformance() {
        val row1 = G0Row(
            domain = TwoSidedDomain(OneSidedDomain(">", 10f), OneSidedDomain("<", 20f)),
            skew = false, list = listOf(null, 37f, 48f, 64f, 73f, 100f)
        )
        val row2 = G0Row(
            domain = TwoSidedDomain(OneSidedDomain("<", 15f), OneSidedDomain("<", 20f)),
            skew = false, list = listOf(-123f, 37f, 48f, null, 73f, 100f)
        )
        val table = G0Table(listOf(row1, row2))
        val gson = GsonBuilder().create()
        val json = gson.toJson(table)
        val actual = gson.fromJson(json, G0Table::class.java)
        assertEquals(table, actual)
    }
}