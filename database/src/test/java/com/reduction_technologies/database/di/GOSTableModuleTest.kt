package com.reduction_technologies.database.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Собственно тесты можно пилить здесь.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest= Config.NONE)
internal class GOSTableModuleTest {
    lateinit var databaseComponent: DatabaseComponent

    @Before
    fun setUp() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        // Using dependencies to create component
        databaseComponent = DaggerDatabaseComponent.builder()
            .databaseModule(DatabaseModule(context))
            .build()
    }

    /**
     * Tests dagger-style table dependencies injection
    */
    @org.junit.Test
    fun get_source_via_table_component() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getSourceTable()

        Assertions.assertNotNull(table)
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun get_fatigue_via_table_component() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getFatigue()

        Assertions.assertNotNull(table)
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun get_g0() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getG0()

        Assertions.assertNotNull(table)
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun get_ed() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getEDTable()

        Assertions.assertNotNull(table)
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun get_HRC() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getHRCTable()

        Assertions.assertNotNull(table)
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun get_RA40() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getRA40()

        Assertions.assertNotNull(table)
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun get_SGTT() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getSGTTTable()

        Assertions.assertNotNull(table)
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun get_Modules() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getStandartModules()

        Assertions.assertNotNull(table)
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun get_tip_tipre() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getTIP_TipreTable()

        Assertions.assertNotNull(table)
    }

    /**
     * Tests dagger-style table dependencies injection
     */
    @org.junit.Test
    fun combined() {
        val tableComponent = DaggerGOSTableComponent.builder()
            .databaseComponent(databaseComponent)
            .gOSTableModule(GOSTableModule()).build()
        val table = tableComponent.getFatigue()
        val g0 = tableComponent.getG0()
        val source = tableComponent.getSourceTable()

        Assertions.assertNotNull(g0)
        Assertions.assertNotNull(table)
        Assertions.assertNotNull(g0)
        Assertions.assertNotNull(source)
    }
}