package com.reducetechnologies.di

import com.reducetechnologies.tables_utils.table_contracts.*
import com.reduction_technologies.database.tables_utils.table_contracts.FatigueTable
import com.reduction_technologies.database.tables_utils.table_contracts.G0Table
import com.reduction_technologies.database.tables_utils.table_contracts.source_datatable.SourceDataTable
import dagger.Component

/**
 * Can produce gost table for further work with them
 */
interface GOSTableComponentInterface {
    fun getFatigue() : FatigueTable

    fun getSourceTable() : SourceDataTable

    fun getG0() : G0Table

    fun getEDTable() : EDDataTable

    fun getHRCTable() : HRCTable

    fun getRA40() : RA40Table

    fun getSGTTTable() : SGTTTable

    fun getStandartModules() : StandartModulesTable

    fun getTIP_TipreTable() : Tip_TipreTable

    /*fun getWLim() : WLimTable

    fun getSourceTable() : SourceDataTable*/
}