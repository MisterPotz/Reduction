package com.reducetechnologies.tables_utils.table_contracts

import com.reduction_technologies.database.tables_utils.TwoSidedDomain

data class WLimRow(
    val domain: TwoSidedDomain,
    val skew : Boolean,
    val st_4: Float,
    val st_5: Float,
    val st_6: Float,
    val st_7: Float,
    val st_8: Float,
    val st_9: Float
)

data class WLimTable(val rows : List<WLimRow>)