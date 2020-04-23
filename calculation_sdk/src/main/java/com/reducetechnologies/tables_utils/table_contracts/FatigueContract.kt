package com.reducetechnologies.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reduction_technologies.database.json_utils.GsonRegister

/**
 * [improv] улучшение - <= 35 HRC
 * [harden] закалка - > 35 HRC
 */
data class K_FE(val improv: Float, val hard: Float)

data class FatigueRow(val load: Int, val kHE: Float, val kFE: K_FE)

data class FatigueTable(val rows: List<FatigueRow>) {
    companion object : GsonRegister {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }
    }
}