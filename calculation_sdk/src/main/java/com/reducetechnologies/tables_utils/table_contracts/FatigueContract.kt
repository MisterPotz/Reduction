package com.reduction_technologies.database.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reducetechnologies.tables_utils.TableExtractor
import com.reduction_technologies.database.json_utils.GsonRegister

/**
 * [improv] улучшение - <= 35 HRC
 * [harden] закалка - > 35 HRC
 */
data class K_FE(val improv: Float, val hard: Float)

data class FatigueRow(val load: Int, val kHE: Float, val kFE: K_FE)

data class FatigueTable(val rows: List<FatigueRow>) {
    companion object : GsonRegister, TableExtractor<FatigueTable> {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }

        override fun extractFromStringWithGson(string: String, gson: Gson): FatigueTable {
            return gson.fromJson(string, FatigueTable::class.java)
        }
    }
}