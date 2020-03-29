package com.reduction_technologies.database.tables_utils.table_contracts.source_datatable

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.reduction_technologies.database.json_utils.*

// ---------------------------------
abstract class GearTypeDependent : PolymorphicParent() {
    init {
        type = Type.PARENT.string
    }

    abstract fun getSign(isInner: Boolean): Int

    companion object
        : PolymorphicParent.Contract<GearTypeDependent>() {
        override val abstractClass: Class<GearTypeDependent> = GearTypeDependent::class.java
        enum class Type(val string: String) {
            DEPENDENT("dep"),
            INDEPENDENT("!dep"),
            PARENT("par")
        }

        override val typeRegistry:
                Map<String, Class<out GearTypeDependent>> =
            mapOf(
                Type.DEPENDENT.string to SignDependent::class.java,
                Type.INDEPENDENT.string to SignSimple::class.java
            )
    }
}

data class SignSimple(val i: Int) : GearTypeDependent() {
    init {
        type = Companion.Type.INDEPENDENT.string
    }

    override fun getSign(isInner: Boolean): Int {
        return i;
    }
}

/**
 * [def] - default version of sign, if outer
 * [ifInn] - if gear sign dependent
 */
data class SignDependent(val def: Int, val ifInn: Int) : GearTypeDependent() {
    init {
        type = Companion.Type.DEPENDENT.string
    }

    override fun getSign(isInner: Boolean): Int {
        return if (isInner) ifInn else def
    }
}

data class SignRow(val list: List<GearTypeDependent>) {
    companion object : GsonRegister {
        override fun prepareGson(): Gson {
            return GsonBuilder().register(SignRowGsonManager()).create()
        }

    }
}

class SignRowGsonManager() : PolymorphicGsonManager<GearTypeDependent>(
    contract = GearTypeDependent
)