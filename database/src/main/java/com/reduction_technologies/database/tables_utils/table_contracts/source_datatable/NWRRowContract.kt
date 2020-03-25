package com.reduction_technologies.database.tables_utils.table_contracts.source_datatable

import com.google.gson.*
import com.reduction_technologies.database.json_utils.PolymorphicDeserializer
import com.reduction_technologies.database.json_utils.PolymorphicParent
import com.reduction_technologies.database.json_utils.PolymorphicSerializer

abstract class ChevroneDependent : PolymorphicParent() {
    init {
        type = Type.PARENT.string
    }

    open fun getNWR(isChevrone: Boolean): Int {
        return -1
    }

    companion object
        : PolymorphicParent.Contract<ChevroneDependent>() {
        enum class Type(val string: String) {
            DEPENDENT("dep"),
            INDEPENDENT("!dep"),
            PARENT("par")
        }

        override val typeRegistry:
                Map<String, Class<out ChevroneDependent>> =
            mapOf(
                Type.DEPENDENT.string to NWRDependent::class.java,
                Type.INDEPENDENT.string to NWRSimple::class.java
            )
    }
}

data class NWRSimple(val i: Int) : ChevroneDependent() {
    init {
        type = Companion.Type.INDEPENDENT.string
    }

    /**
     * Returns i independently
     */
    override fun getNWR(isChevrone: Boolean): Int {
        return i
    }
}

/**
 * [def] - default version of nwr, if not chevron
 * [ifChev] - if chevrone
 */
data class NWRDependent(val def: Int, val ifChev: Int) : ChevroneDependent() {
    init {
        type = Companion.Type.DEPENDENT.string
    }

    /**
     * Returns i dependently
     */
    override fun getNWR(isChevrone: Boolean): Int {
        return if (isChevrone) ifChev else def
    }
}

data class NWRRow(val list: List<ChevroneDependent>)


internal class NWRRowDeserializer(gson: Gson) : PolymorphicDeserializer<ChevroneDependent>(
    contract = ChevroneDependent,
    gson = gson
)

internal class NWRRowSerializer(gson: Gson) :
    PolymorphicSerializer<ChevroneDependent>(
        contract = ChevroneDependent,
        gson = gson
    )


