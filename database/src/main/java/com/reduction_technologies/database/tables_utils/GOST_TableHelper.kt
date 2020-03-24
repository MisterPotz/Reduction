package com.reduction_technologies.database.tables_utils

/**
 * Helps with GOST tables. Contains names of needed gost tables, presents an interface to
 * obrain rows of each datatable. A row is a simple data object that can be serialized.
 */
object GOST_TableHelper {
    const val FATIGUE_CALCULATION_23 = "2.3"
    const val W_LIM = "2.5"
    const val G_0 = "2.4"
    const val SOURCE_DATA = "2.7"
    const val K_C = "2.6"
    const val RA = "R_A"
}

data class FatigueCalculation(val loadMode: Int, val K_HE: Float, val K_FE: Float)

data class WLim(
    val domain: TwoSidedDomain,
    val st_4: Float,
    val st_5: Float,
    val st_6: Float,
    val st_7: Float,
    val st_8: Float,
    val st_9: Float
)