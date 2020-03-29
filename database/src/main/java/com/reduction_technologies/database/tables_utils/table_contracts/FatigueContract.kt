package com.reduction_technologies.database.tables_utils.table_contracts

/**
 * [improv] улучшение
 * [harden] закалка
 */
data class K_FE(val improv: Float, val hard: Float)

data class FatigueRow(val load: Int, val kHE: Float, val kFE: K_FE)

data class FatigueTable(val rows: List<FatigueRow>)