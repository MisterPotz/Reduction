package com.reducetechnologies.command_infrastructure

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class WatchingStorageTest {

    class SampleObj

    @Test
    fun addToBack() {
        val f1 = SampleObj()
        val f2 = SampleObj()
        val storage = WatchingStorage<SampleObj>()
        storage.init(f1)

        storage.addToBack(f2)
        storage.currentToNext()
        storage.getCurrent().let {
            assertEquals(f2, it)
        }
    }

    @Test
    fun isCurrent() {
        val f1 = SampleObj()
        val storage = WatchingStorage<SampleObj>()
        storage.init(f1)

        assertTrue(storage.isCurrent(f1))
    }

    @Test
    fun commitCurrent() {
        val f1 = SampleObj()
        val f2 = SampleObj()
        val storage = WatchingStorage<SampleObj>()
        storage.init(f1)
        val current = storage.getCurrent()
        var catched = false
        // chekcing error
        try {
            storage.commitCurrent(f2)
        } catch (e: IllegalStateException) {
            catched = true
        }
        assertTrue(catched)
        storage.commitCurrent(f1)

        storage.addToBack(f2)
        storage.currentToNext()
        storage.getCurrent().let {
            assertEquals(f2, it)
        }
    }

    @Test
    fun isWaitingForCurrent() {
        val f1 = SampleObj()
        val f2 = SampleObj()
        val storage = WatchingStorage<SampleObj>()
        storage.init(f1)
        storage.getCurrent()

        assertEquals(f1, storage.getCurrent())
    }

    @Test
    fun replaceCurrentWith() {
        val f1 = SampleObj()
        val f2 = SampleObj()
        val storage = WatchingStorage<SampleObj>()
        storage.init(f1)
        storage.getCurrent()

        storage.replaceCurrentWith(f2)

        assertEquals(f2, storage.getCurrent())
    }

    @Test
    fun currentIsNotTouchedUnexplicitly() {
        val f1 = SampleObj()
        val f2 = SampleObj()
        val storage = WatchingStorage<SampleObj>()
        storage.init(f1)
        storage.getCurrent()

        storage.commitCurrent(f1)

       val current = storage.getCurrent()

        assertEquals(f1, current)
    }

    @Test
    fun isInit() {
        val f1 = SampleObj()
        val f2 = SampleObj()
        val storage = WatchingStorage<SampleObj>()
        var caught = false

        try {
            val curr = storage.getCurrent()
        } catch (e : java.lang.IllegalStateException) {
            caught = true
        }
        assertTrue(caught)
    }
}