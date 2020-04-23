package com.reduction_technologies.database.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reducetechnologies.tables_utils.table_contracts.FatigueRow
import com.reducetechnologies.tables_utils.table_contracts.K_FE
import com.reduction_technologies.database.json_utils.GsonRegister

/**
 * [improv] улучшение - <= 35 HRC
 * [harden] закалка - > 35 HRC
 */
data class K_FE(val improv: Float, val hard: Float)

data class FatigueRow(val load: Int, val kHE: Float, val kFE: K_FE)

data class FatigueTable(val rows: List<FatigueRow>) {
    /**
     * Этот класс - FatigueTable - простой, в нем нет таких блоков, которые бы в рамках массива
     * отличались бы структурой. Поэтому GsonRegister не обязателен, gson иожно создать
     * и без дополнительных классов:
     * val gson = GsonBuilder.create()
     * и затем
     * val fatigueTable = gson.fromJson(some_json, FatigueTable::class.java)
     */
    companion object : GsonRegister {
        override fun prepareGson(): Gson {
            return  GsonBuilder().create()
        }
    }
}