package com.reducetechnologies.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reducetechnologies.tables_utils.TableExtractor
import com.reduction_technologies.database.json_utils.GsonRegister

data class SGTTTable(val SGTT: Array<Array<Int>>){
    companion object : GsonRegister, TableExtractor<SGTTTable> {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }

        override fun extractFromStringWithGson(string: String, gson: Gson): SGTTTable {
            return gson.fromJson(string, SGTTTable::class.java)
        }
    }
}