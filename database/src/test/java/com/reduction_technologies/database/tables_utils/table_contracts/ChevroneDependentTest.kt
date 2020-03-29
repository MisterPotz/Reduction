package com.reduction_technologies.database.tables_utils.table_contracts

import com.reduction_technologies.database.tables_utils.table_contracts.source_datatable.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class NWRRowTest {
    @Test
    fun polymorphicSerializationTest() {
        val row = NWRRow(listOf(NWRSimple(1), NWRDependent(3, 4)))
        //val type = object : TypeToken<NWRRow>(){}.type
        val gson = NWRRow.prepareGson()

        val json = gson.toJson(row)
        val recreatedRow = gson.fromJson<NWRRow>(json, NWRRow::class.java)

        assertEquals(row, recreatedRow)
    }

    @Test
    fun equalityTest() {
        val item = NWRSimple(1)
        val dItem = NWRSimple(1)
        val item2 = NWRDependent(2, 3)
        val dItem2 = NWRDependent(2, 3)
        val list1 = listOf(item, item2)
        val list2 = listOf(dItem, dItem2)
        assertEquals(list1, list2)
        val row = NWRRow(listOf(NWRSimple(1), NWRDependent(3, 4)))
        val row2 = NWRRow(listOf(NWRSimple(1), NWRDependent(3, 4)))

        assertEquals(row, row2)
    }
}