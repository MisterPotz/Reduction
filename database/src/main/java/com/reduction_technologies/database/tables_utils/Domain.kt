package com.reduction_technologies.database.tables_utils

import java.lang.IllegalStateException

interface DomainDefinable<in T : Comparable<T>> {
    fun isInDomain(num: T): Boolean
}

/**
 * Float is considered to be common across our calculations
 */
abstract class DomainDefinableFloat :
    DomainDefinable<Float>
// TODO create domain tests
/**
 * Shouldn't be used as common in our datatables. Instead,
 * @see TwoSidedDomain
 */
class OneSidedDomain(val conditionSign: String, val num: Float) : DomainDefinableFloat() {
    override fun isInDomain(num: Float): Boolean {
        if (conditionSign == "<") {
            num < this.num
        }
        return when (conditionSign) {
            "<" -> num < this.num
            "<=" -> num <= this.num
            ">" -> num > this.num
            ">=" -> num >= this.num
            "=" -> num == this.num
            else -> throw IllegalStateException("condition sign ${conditionSign} is not supported")
        }
    }
}

/**
 * Common domain class for data tables and calculations.
 */
class TwoSidedDomain(val leftSide: OneSidedDomain, val rightSide: OneSidedDomain) :
    DomainDefinableFloat() {
    override fun isInDomain(num: Float): Boolean {
        return leftSide.isInDomain(num) && rightSide.isInDomain(num)
    }
}
