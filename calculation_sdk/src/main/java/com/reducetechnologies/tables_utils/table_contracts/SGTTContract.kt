package com.reducetechnologies.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reduction_technologies.database.json_utils.GsonRegister

data class SGTTTable(val SGTT: Array<Array<Int>>){
    companion object : GsonRegister {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }
    }
}