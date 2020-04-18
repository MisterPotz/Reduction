package com.reducetechnologies.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reducetechnologies.tables_utils.TableExtractor
import com.reduction_technologies.database.json_utils.GsonRegister

data class HRCTable(val HRC: Array<Array<Float>>){
    companion object : GsonRegister, TableExtractor<HRCTable> {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }

        override fun extractFromStringWithGson(string: String, gson: Gson): HRCTable {
            return gson.fromJson(string, HRCTable::class.java)
        }
    }
}