package com.reducetechnologies.miniTests

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.math.RoundingMode

internal class NumRoundTest {

    @Test
    fun round(value: Float, places: Int): Float {
        if (places < 0) throw IllegalStateException("Places in fun round were was < 0")
        var bd: BigDecimal = BigDecimal(value.toString())
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toFloat()
    }

    @Test
    fun roundThree(value: Float): Float {
        return round(value, 3)
    }

    @Test
    fun tryTo() {
        println(roundThree(3.8564f))
        println(roundThree(3.1f))
    }
}