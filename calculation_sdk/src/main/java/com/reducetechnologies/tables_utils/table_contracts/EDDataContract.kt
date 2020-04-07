package com.reducetechnologies.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reduction_technologies.database.json_utils.GsonRegister

data class EDDataRow(val key: Float,
                     val peds: List<Int>,
                     val tteds: List<Float>,
                     val maes: List<Float>,
                     val d1eds: List<Int>,
                     val l1eds: List<Int>,
                     val h1eds: List<Int>
)

data class EDDataTable(val map: List<EDDataRow>){
    companion object : GsonRegister {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }
    }
}