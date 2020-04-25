package com.reducetechnologies.tables_utils

import com.google.gson.Gson

interface TableExtractor<T> {
    fun extractFromStringWithGson(string: String, gson: Gson) : T
}