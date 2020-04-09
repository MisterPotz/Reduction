package com.reduction_technologies.database.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.reducetechnologies.di.CalculationComponent
import kotlinx.android.synthetic.*
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
internal class CalculationsTest {
    lateinit var databaseComponent: DatabaseComponent

    @Before
    fun setUp() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        // Using dependencies to create component
        databaseComponent = DaggerDatabaseComponent.builder()
            .databaseModule(DatabaseModule(context))
            .build()
        val calculationComponent: CalculationComponent = DaggerCalculationComponent.builder()
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
}