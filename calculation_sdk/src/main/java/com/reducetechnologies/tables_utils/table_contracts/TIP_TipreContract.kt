package com.reducetechnologies.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reducetechnologies.tables_utils.TableExtractor
import com.reduction_technologies.database.json_utils.GsonRegister

/**
 * [improv] улучшение - <= 35 HRC
 * [harden] закалка - 35 < HRC <= 50
 * [cement] цементация - HRC > 50
 */
data class TIP(val improv: Float, val hard: Float, val cement: Float)

data class Tip_TipreRow(val TIPRE: Int, val TIP: TIP)

data class Tip_TipreTable(val rows: List<Tip_TipreRow>){
    companion object : GsonRegister, TableExtractor<Tip_TipreTable> {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }

        override fun extractFromStringWithGson(string: String, gson: Gson): Tip_TipreTable {
            return gson.fromJson(string, Tip_TipreTable::class.java)
        }
    }
}