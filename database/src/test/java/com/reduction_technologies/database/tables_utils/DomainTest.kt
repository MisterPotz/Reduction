package com.reduction_technologies.database.tables_utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class DomainTest {
    private fun List<Pair<Float, Boolean>>.checkAll(domainDefinableFloat: DomainDefinableFloat) {
        forEach {
            assertEquals(domainDefinableFloat.isInDomain(it.first), it.second)
        }
    }

    @Test
    fun leftDomain() {
        val oneSideDomain = OneSidedDomain("<", 5f)

        listOf<Pair<Float, Boolean>>(
            Pair(4f, true),
            Pair(5f, false),
            Pair(6f, false)
        ).checkAll(oneSideDomain)
    }

    @Test
    fun leftEqDomain() {
        val oneSideDomain = OneSidedDomain("<=", 5f)

        listOf<Pair<Float, Boolean>>(
            Pair(4f, true),
            Pair(5f, true),
            Pair(6f, false)).checkAll(oneSideDomain)
    }

    @Test
    fun rightDomain() {
        val oneSideDomain = OneSidedDomain(">", 5f)

        listOf<Pair<Float, Boolean>>(
            Pair(4f, false),
            Pair(5f, false),
            Pair(6f, true)).checkAll(oneSideDomain)
    }
    @Test
    fun rightEqDomain() {
        val oneSideDomain = OneSidedDomain(">=", 5f)

        listOf<Pair<Float, Boolean>>(
            Pair(4f, false),
            Pair(5f, true),
            Pair(6f, true)).checkAll(oneSideDomain)
    }

    @Test
    fun dLeftDomain() {
        val twoSidedDomain = TwoSidedDomain(OneSidedDomain("<", 5f),
            OneSidedDomain("<", 10f)
        )

        listOf(
            Pair(4f, true),
            Pair(6f, false),
            Pair(111f, false)).checkAll(twoSidedDomain)
    }

    @Test
    fun dMiddleDomain() {
        val twoSidedDomain = TwoSidedDomain(OneSidedDomain(">", 5f),
            OneSidedDomain("<", 10f)
        )

        listOf(
            Pair(4f, false),
            Pair(6f, true),
            Pair(111f, false)).checkAll(twoSidedDomain)
    }

    @Test
    fun dRightDomain() {
        val twoSidedDomain = TwoSidedDomain(OneSidedDomain(">", 5f),
            OneSidedDomain(">", 10f)
        )

        listOf(
            Pair(4f, false),
            Pair(6f, false),
            Pair(111f, true)).checkAll(twoSidedDomain)
    }
}