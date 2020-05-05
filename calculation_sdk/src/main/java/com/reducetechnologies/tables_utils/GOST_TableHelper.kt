package com.reducetechnologies.tables_utils

import com.reducetechnologies.tables_utils.table_contracts.*
import com.reducetechnologies.tables_utils.table_contracts.FatigueTable
import com.reducetechnologies.tables_utils.table_contracts.G0Table
import com.reducetechnologies.tables_utils.table_contracts.source_datatable.SourceDataTable

/**
 * Helps with GOST tables. Contains names of needed gost tables, presents an interface to
 * obrain rows of each datatable. A row is a simple data object that can be serialized.
 */
object GOSTableContract {
    const val FATIGUE_CALCULATION_23 = "fatigue_2_3"
    const val W_LIM = "2.5"
    const val G_0 = "coeff_g0_2_4"
    const val SOURCE_DATA = "source_data_2_7"
    const val K_C = "2.6"
    const val RA40 = "R_A"
    const val MODULES = "module_standart"
    const val EDData = "eddata"
    const val HRC = "HRC"
    const val SGTT = "SGTT"
    const val TIP_Tipre = "tipre_tip"
}

/**
 * Structure, used to hold all necessary tables
 */
data class TableHolder(
    val fatigue : FatigueTable,
    /*val w_lim = "2.5",*/
    val g_0 : G0Table,
    val source_data : SourceDataTable,
/*  val K_C = "2.6",*/
    val ra40 : RA40Table,
    val modules : StandartModulesTable,
    val EDData : EDDataTable,
    val HRC : HRCTable,
    val SGTT : SGTTTable,
    val TIP_Tipre : Tip_TipreTable
)


