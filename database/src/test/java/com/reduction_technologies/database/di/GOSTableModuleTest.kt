package com.reduction_technologies.database.di

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.test.core.app.ApplicationProvider
import com.reducetechnologies.tables_utils.TableHolder
import kotlinx.coroutines.*
import net.bytebuddy.implementation.bind.annotation.Super
import org.junit.Before
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.fail
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.coroutines.coroutineContext

/**
 * Собственно тесты можно пилить здесь.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
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

    @org.junit.Test
    fun getTablesTest() {
        val repository = databaseComponent.repository()

        val job = SupervisorJob()
        val asynced = CoroutineScope(Dispatchers.Default + job).async {
            repository.getTables()
        }

        runBlocking {
            withTimeout(4000) {
                val tables = asynced.await()
                assertTrue(tables != null)
            }
        }
    }

    @org.junit.Test
    fun getEntitiesTest() {
        val repository = databaseComponent.repository()

        val job = SupervisorJob()
        val asynced = CoroutineScope(Dispatchers.Default + job).async {
            repository.getEncyclopediaItems()
        }

        runBlocking {
            withTimeout(4000) {
                val items = asynced.await()

                assertTrue(items != null)
            }
        }
    }
}