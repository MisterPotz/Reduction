package com.reduction_technologies.database.di

import android.content.Context
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.helpers.ConstantDatabaseHelper
import com.reduction_technologies.database.helpers.Repository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext

class InstantExecutorExtension : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance()
            .setDelegate(object : TaskExecutor() {
                override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

                override fun postToMainThread(runnable: Runnable) = runnable.run()

                override fun isMainThread(): Boolean = true
            })
    }

    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

}


/**
 * Tests for livedata via livedata mocking observers
 */
@ExtendWith(InstantExecutorExtension::class)
internal class GOSTableModuleTest {
    // Mocking specific test values of tableholder
    val tableHolder = run<TableHolder> {
        val holder = mockk<TableHolder>()
        every { holder.fatigue } returns mockk() {
            every { rows } returns listOf(mockk() {
                every { load } returns -120
            })
        }
        holder
    }

    val mockedConstantDatabase = mockk<ConstantDatabaseHelper>() {
        every { getTables() } answers {
            tableHolder
        }
    }

    val repository = Repository(mockk<Context>(), mockedConstantDatabase, mockk())

    @Test
    fun getTablesTest() {

        val tables = runBlocking {
            repository.getTables()
        }
        assertEquals(-120, tables.value!!.fatigue.rows[0].load)
    }
}