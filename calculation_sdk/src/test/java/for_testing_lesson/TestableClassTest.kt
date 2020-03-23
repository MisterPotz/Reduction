package for_testing_lesson

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class TestableClassTest {

    fun helping() {
        println("I jhelp somebody")
    }

    @Test
    fun generateWith() {
        val testingPatient = TestableClass()
        assertEquals(true, testingPatient.generateWith(3)
    }
}