package com.reduction_technologies.database.databases_utils

import com.google.gson.Gson
import com.reducetechnologies.tables_utils.GOSTableContract.EDData
import com.reducetechnologies.tables_utils.GOSTableContract.FATIGUE_CALCULATION_23
import com.reducetechnologies.tables_utils.GOSTableContract.G_0
import com.reducetechnologies.tables_utils.GOSTableContract.HRC
import com.reducetechnologies.tables_utils.GOSTableContract.MODULES
import com.reducetechnologies.tables_utils.GOSTableContract.RA40
import com.reducetechnologies.tables_utils.GOSTableContract.SGTT
import com.reducetechnologies.tables_utils.GOSTableContract.SOURCE_DATA
import com.reducetechnologies.tables_utils.GOSTableContract.TIP_Tipre
import com.reducetechnologies.tables_utils.TableExtractor
import com.reducetechnologies.tables_utils.TableHolder
import com.reducetechnologies.tables_utils.table_contracts.*
import com.reducetechnologies.tables_utils.table_contracts.FatigueTable
import com.reducetechnologies.tables_utils.table_contracts.G0Table
import com.reducetechnologies.tables_utils.table_contracts.source_datatable.SourceDataTable

// TODO сделать инъекция подготовленного для всего gson
fun List<CommonItem>.extractTableHolder(): TableHolder {
    val fatigue = findByTitle(FATIGUE_CALCULATION_23, FatigueTable.prepareGson(), FatigueTable)
    val g_0 = findByTitle(G_0, G0Table.prepareGson(), G0Table)
    val source = findByTitle(SOURCE_DATA, SourceDataTable.prepareGson(), SourceDataTable)
    val ra40 = findByTitle(RA40, RA40Table.prepareGson(), RA40Table)
    val modules = findByTitle(MODULES, StandartModulesTable.prepareGson(), StandartModulesTable)
    val eddata = findByTitle(EDData, EDDataTable.prepareGson(), EDDataTable)
    val hrc = findByTitle(HRC, HRCTable.prepareGson(), HRCTable)
    val sgtt = findByTitle(SGTT, SGTTTable.prepareGson(), SGTTTable)
    val tipre = findByTitle(TIP_Tipre, Tip_TipreTable.prepareGson(), Tip_TipreTable)

    return TableHolder(
        fatigue = fatigue,
        g_0 = g_0,
        source_data = source,
        ra40 = ra40,
        modules = modules,
        EDData = eddata,
        HRC = hrc,
        SGTT = sgtt,
        TIP_Tipre = tipre
    )
}

private inline fun <T> List<CommonItem>.findByTitle(
    string: String,
    gson: Gson,
    tableExtractor: TableExtractor<T>
): T {
    return find { it.title == string }!!.let {
        tableExtractor.extractFromStringWithGson(
            it.additional!!,
            gson
        )
    }
}
