package com.reducetechnologies.di

import com.reduction_technologies.database.tables_utils.table_contracts.FatigueTable
import com.reduction_technologies.database.tables_utils.table_contracts.G0Table
import com.reduction_technologies.database.tables_utils.table_contracts.WLimTable
import com.reduction_technologies.database.tables_utils.table_contracts.source_datatable.SourceDataTable
import dagger.Subcomponent

/**
 * Can produce gost table for further work with them
 */
interface GOSTableComponentInterface {
    fun getFatigue() : FatigueTable

    fun getG0() : G0Table

    fun getWLim() : WLimTable

    fun getSourceTable() : SourceDataTable
}