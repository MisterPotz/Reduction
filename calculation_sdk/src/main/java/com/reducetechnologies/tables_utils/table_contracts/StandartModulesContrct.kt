package com.reducetechnologies.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reducetechnologies.tables_utils.TableExtractor
import com.reduction_technologies.database.json_utils.GsonRegister

data class StandartModulesTable(val list: List<Float>){
    companion object : GsonRegister, TableExtractor<StandartModulesTable> {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }

        override fun extractFromStringWithGson(string: String, gson: Gson): StandartModulesTable {
            return gson.fromJson(string, StandartModulesTable::class.java)
        }
    }
}