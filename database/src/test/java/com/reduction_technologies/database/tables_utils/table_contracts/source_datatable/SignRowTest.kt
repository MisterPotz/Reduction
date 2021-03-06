package com.reduction_technologies.database.tables_utils.table_contracts.source_datatable

import com.reducetechnologies.tables_utils.table_contracts.source_datatable.SignDependent
import com.reducetechnologies.tables_utils.table_contracts.source_datatable.SignRow
import com.reducetechnologies.tables_utils.table_contracts.source_datatable.SignSimple
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SignRowTest {
    @Test
    fun deSerializationEquality() {
        val row = SignRow(
            listOf(SignSimple(3), SignSimple(-12), SignDependent(12, 123))
        )

        val gson = SignRow.prepareGson()
        val json = gson.toJson(row)
        val row2 = gson.fromJson(json, SignRow::class.java)
        assertEquals(row, row2)
    }

    @Test
    fun deserializationWorkability() {
        val row = SignRow(
            listOf(SignSimple(3), SignSimple(-12), SignDependent(12, 123))
        )
        val gson = SignRow.prepareGson()
        val json = gson.toJson(row)
        val row2 = gson.fromJson(json, SignRow::class.java)

        assertTrue(row2.list[1].getSign(true) == -12)
        assertTrue(row2.list[1].getSign(false) == -12)

        assertTrue(row2.list[2].getSign(true) == 123)
        assertTrue(row2.list[2].getSign(false) == 12)
    }
}