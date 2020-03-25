package com.reduction_technologies.database.tables_utils.table_contracts.source_datatable

data class TIPRERow(val list: List<Int>)

data class NPRow(val list: List<Int>)

data class BETMIRow(val list: List<Float>)

data class BETMARow(val list: List<Float>)

data class OMEGRow(val list: List<Float>)

data class NWRow(val list: List<Int>)

data class NZAC1Row(val list: List<Int>)

// ---------------------------------

data class NZAC2Row(val list: List<Int>)

// ---------------------------------

data class BKANRow(val list: List<Int>)

// ---------------------------------
abstract class GearTypeDependent {
    val type: String = Type.PARENT.string
    abstract fun getSign(isInner: Boolean): Int

    companion object {
        enum class Type(val string: String) { DEPENDENT("d"), INDEPENDENT("i"), PARENT("p") }
    }
}

data class SignSimple(val i: Int) : GearTypeDependent() {
    override fun getSign(isInner: Boolean): Int {
        return i;
    }
}

/**
 * [def] - default version of sign, if outer
 * [ifInn] - if gear sign dependent
 */
data class SignDependent(val def: Int, val ifInn: Int) : GearTypeDependent() {
    override fun getSign(isInner: Boolean): Int {
        return if (isInner) ifInn else def
    }
}

data class SignRow(val list: List<GearTypeDependent>)

// ---------------------------------
data class ConsolRow(val list: List<Int>)