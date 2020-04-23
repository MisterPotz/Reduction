package com.reducetechnologies.tables_utils.table_contracts.source_datatable

import com.google.gson.*
import com.reduction_technologies.database.json_utils.GsonRegister
import com.reduction_technologies.database.tables_utils.table_contracts.source_datatable.*
import com.reduction_technologies.database.tables_utils.table_contracts.source_datatable.NWRRowGsonManager

// TODO обложить тестами на равность и сериализацию
data class SourceDataTable(
    val tipreRow: TIPRERow,
    val npRow: NPRow,
    val betMiRow: BETMIRow,
    val betMaRow: BETMARow,
    val omegRow: OMEGRow,
    val nwRow: NWRow,
    val nzaC1Row: NZAC1Row,
    val nzaC2Row: NZAC2Row,
    val nwrRow: NWRRow,
    val bkanRow: BKANRow,
    val signRow: SignRow,
    val consolRow: ConsolRow
) {
    companion object : GsonRegister {
        override fun prepareGson(): Gson {
            return GsonBuilder()
                .register(NWRRowGsonManager())
                .register(SignRowGsonManager())
                .create()
        }
    }
}

