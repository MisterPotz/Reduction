package com.reducetechnologies.tables_utils.table_contracts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reducetechnologies.tables_utils.TableExtractor
import com.reduction_technologies.database.json_utils.GsonRegister

/**
 * Обрати внимание, что Gson переводит класс в json с именами полей такими же, как и в написании
 * самого клсса. То есть у тебя там должно быть не "RA40" в джисоне в дб, а "values". Но "values" не надо,
 * давай просто "list" (в других классах я уже начал так делать, чтобы не было путаницы. Когда просто
 * набор примитивных значений - list, когда ужже набор строк - rows)
 */
data class RA40Table(val list: List<Float>){
    companion object : GsonRegister, TableExtractor<RA40Table> {
        override fun prepareGson(): Gson {
            return GsonBuilder().create()
        }

        override fun extractFromStringWithGson(string: String, gson: Gson): RA40Table {
            return gson.fromJson(string, RA40Table::class.java)
        }
    }
}