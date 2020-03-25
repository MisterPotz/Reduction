package com.reduction_technologies.database.tables_utils.table_contracts.source_datatable

import com.google.gson.*
import com.reduction_technologies.database.tables_utils.table_contracts.source_datatable.*
import java.lang.reflect.Type


// TODO обложить тестами на равность и сериализацию
data class SourceDataTable(
    val tipreRow: TIPRERow,
    val npRow : NPRow,
    val betMiRow: BETMIRow,
    val betMaRow : BETMARow,
    val omegRow: OMEGRow,
    val nwRow: NWRow,
    val nzaC1Row: NZAC1Row,
    val nzaC2Row: NZAC2Row,
    val nwrRow: NWRRow,
    val bkanRow : BKANRow,
    val signRow: SignRow,
    val consolRow: ConsolRow
)
