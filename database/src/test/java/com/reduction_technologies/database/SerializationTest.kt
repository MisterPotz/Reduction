package com.reduction_technologies.database

import com.google.gson.GsonBuilder
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import com.google.gson.reflect.TypeToken



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

    /**
     * Tests serialization with generic behavior
     */
    @Test
    fun pairSerialization() {
        val pair = Pair(-5f, 10f)
        // Must be used when need to deal with generic types
        val pairType = object : TypeToken<Pair<Float, Float>>() {}.type

        val json = """{"first":-5.0,"second":10.0}"""
        @Language("JSON")
        val jsonFrom = """
{ 
  "first" : -5,
  "second": 10
}""".trimMargin()

        val gson = GsonBuilder().create()
        assertEquals(json, gson.toJson(pair, pairType))
        assertEquals(pair, gson.fromJson(jsonFrom, pairType))
    }

    /**
     * Test how array with null is deserialized
     */
    @Test
    fun arrWithNull() {
        val list = listOf<Int?>(null, 5,3,2)
        val type = object : TypeToken<List<Int?>>() {}.type
        @Language("JSON")
        val json = """
            [null,5,3,2]
        """.trimIndent()
        val gson = GsonBuilder().create()


        assertEquals(list, gson.fromJson(json, type))

        assertEquals(json, gson.toJson(list, type))

    }
}