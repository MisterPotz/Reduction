package com.reduction_technologies.database.tables_utils.table_contracts.source_datatable

import com.google.gson.*
import com.reducetechnologies.tables_utils.TableExtractor
import com.reduction_technologies.database.json_utils.GsonRegister

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
    companion object : GsonRegister, TableExtractor<SourceDataTable> {
        override fun prepareGson(): Gson {
            return GsonBuilder()
                .register(NWRRowGsonManager())
                .register(SignRowGsonManager())
                .create()
        }

        override fun extractFromStringWithGson(string: String, gson: Gson): SourceDataTable {
            return gson.fromJson(string, SourceDataTable::class.java)
        }
    }
}

