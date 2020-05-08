package com.reducetechnologies.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reducetechnologies.tables_utils.TableExtractor
import com.reduction_technologies.database.json_utils.GsonRegister
import com.reduction_technologies.database.tables_utils.TwoSidedDomain

data class G0Row(
    val domain: TwoSidedDomain,
    val skew: Boolean,
    // 4 - 9 степени точности
    val list : List<Float?>
) {
    override fun toString(): String {
        return "G0Row: domain = $domain, skew = $skew, $list"
    }
}

data class G0Table(val rows : List<G0Row>) {
    companion object : GsonRegister, TableExtractor<G0Table> {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }

        override fun extractFromStringWithGson(string: String, gson: Gson): G0Table {
            return gson.fromJson(string, G0Table::class.java)
        }
    }
}