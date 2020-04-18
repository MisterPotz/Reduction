package com.reduction_technologies.database.helpers

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception
import java.lang.IllegalStateException

internal class LiveDataClassStorageTest {

    @Test
    fun registerTypeContatins() {
        val liveDataClassStorage = LiveDataClassStorage()

        val liveData = liveDataClassStorage.registerType(Int::class)

        liveDataClassStorage.checkContains(Int::class).let {
            assertEquals(true, it)
        }
    }

    @Test
    fun registerTypeRegistersAndReturns() {
        val liveDataClassStorage = LiveDataClassStorage()

        val liveData = liveDataClassStorage.registerType(Int::class)

        val returned = liveDataClassStorage.getLiveData(Int::class)

        assertEquals(liveData, returned)
    }

    @Test
    fun registerTypeFails() {
        val liveDataClassStorage = LiveDataClassStorage()

        val liveData = liveDataClassStorage.registerType(Int::class)

        try {
            val returned = liveDataClassStorage.registerType(Int::class)
            fail<Exception>("Must fail")
        } catch (e : IllegalStateException) {
            return
        }

        try {
            val returned = liveDataClassStorage.getLiveData(Boolean::class)
            fail<Exception>("Must fail")
        } catch (e : IllegalStateException) {
            return
        }
    }

    @Test
    fun registerTypeFailsReturn() {
        val liveDataClassStorage = LiveDataClassStorage()

        val liveData = liveDataClassStorage.registerType(Int::class)

        try {
            val returned = liveDataClassStorage.getLiveData(Boolean::class)
            fail<Exception>("Must fail")
        } catch (e : IllegalStateException) {
            return
        }
    }
}