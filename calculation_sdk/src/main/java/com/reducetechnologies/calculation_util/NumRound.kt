package com.reducetechnologies.calculation_util

import java.math.BigDecimal
import java.math.RoundingMode

internal object NumRound {
    fun round(value: Float, places: Int): Float {
        if (places < 0) throw IllegalStateException("Places in fun round were was < 0")
        var bd: BigDecimal = BigDecimal(value.toString())
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toFloat()
    }

    fun roundThree(value: Float): Float {
        return round(value, 3)
    }
}