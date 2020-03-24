package com.reduction_technologies.database

import com.google.gson.GsonBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SerializationTest {
    data class SimplePlain(val string: String, val avs: Int)

    data class Complicated(val simplePlain: List<SimplePlain>, val val2 : String)

    @Test
    fun gsonSerialization() {
        val string = """{"string":"some string","avs":5}"""
        val obj = SimplePlain("some string", 5)
        val gson = GsonBuilder().create()
        val output =  gson.fromJson(string, SimplePlain::class.java)
        assertEquals(obj, output)

        assertEquals(string, gson.toJson(obj))
    }

    @Test
    fun complicatedSerialization() {
        val json = """{"simplePlain":[{"string":"some string","avs":5},{"string":"other","avs":-10}],"val2":"string"}"""
        val obj = Complicated(listOf(SimplePlain("some string", 5),SimplePlain("other", -10)), "string")
        val gson = GsonBuilder().create()

        assertEquals(obj, gson.fromJson(json, Complicated::class.java))

        assertEquals(json, gson.toJson(obj))
    }
}