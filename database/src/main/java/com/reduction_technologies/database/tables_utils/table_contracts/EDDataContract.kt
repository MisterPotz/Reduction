package com.reduction_technologies.database.tables_utils.table_contracts

data class EDDataRow(val key: Float,
                     val peds: List<Int>,
                     val tteds: List<Float>,
                     val maes: List<Float>,
                     val d1eds: List<Int>,
                     val l1eds: List<Int>,
                     val h1eds: List<Int>
)

data class EDDataTable(val map: List<EDDataRow>)