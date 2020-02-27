package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.test.platform.app.InstrumentationRegistry
import com.reducetechnologies.reduction.di.DaggerAppComponent
import com.reduction_technologies.database.di.CalculationSetup
import com.reduction_technologies.database.di.DatabaseModule
import com.reduction_technologies.database.helpers.AppLocale
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class SharedViewModelTest {
    @Test
    fun cleanHelperInit() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val appComponent = DaggerAppComponent.builder().calculationSetup(CalculationSetup()).databaseModule(
            DatabaseModule(appContext, AppLocale.RU)
        ).build()
        val storageProvider = appComponent.calculationStorage()
        val storage = storageProvider.get()
        runBlocking {
            storage.init()
        }
        assertEquals(true, storage.isInit())
    }
}