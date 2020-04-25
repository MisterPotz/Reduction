package com.reduction_technologies.database.helpers

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception
import java.lang.IllegalStateException

internal class LiveDataClassStorageTest {

    enum class TestEnum { ONE, TWO }
    @Test
    fun registerTypeContatins() {
        val liveDataClassStorage = LiveDataClassStorage<TestEnum>()

        val liveData = liveDataClassStorage.registerOrReturn<Int>(TestEnum.ONE)

        val liveData2 = liveDataClassStorage.registerOrReturn<Int>(TestEnum.ONE)
        assertEquals(liveData, liveData2)
    }
}