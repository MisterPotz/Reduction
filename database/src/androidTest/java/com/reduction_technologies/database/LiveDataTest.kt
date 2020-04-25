package com.reduction_technologies.database

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.reduction_technologies.database.di.DaggerDatabaseComponent
import com.reduction_technologies.database.di.DatabaseComponent
import com.reduction_technologies.database.di.DatabaseModule
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith


/**
 * Tests on livedata / database on real device (to run those on roboelectric is can't be done
 *  - some roboelectric bug preventing livedata to be tested with it)
 */
@RunWith(AndroidJUnit4::class)
internal class LiveDataTest {
    lateinit var databaseComponent: DatabaseComponent

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Using dependencies to create component
        databaseComponent = DaggerDatabaseComponent.builder()
            .databaseModule(DatabaseModule(context))
            .build()
    }

    @org.junit.Test
    fun getTablesTest() {
        val repository = databaseComponent.repository()

        val asynced = CoroutineScope(Dispatchers.Default).async {
            repository.getTables()
        }

        runBlocking {
            withTimeout(3000) {
                val deferred = CompletableDeferred<Boolean>()
                val livedata = asynced.await()
                withContext(Dispatchers.Main) {
                     livedata.observeForever {
                        assertTrue(it != null)
                        deferred.complete(true)
                    }

                }
                deferred.await()
            }
        }
    }

    @org.junit.Test
    fun liveDataTest() {
        val repository = databaseComponent.repository()

        val liveData = MutableLiveData<Int>()

        CoroutineScope(Dispatchers.Default).launch {
            delay(1000)
            liveData.postValue(1000)
        }

        runBlocking {
            withTimeout(3000) {
                println(" I'm working in thread ${Thread.currentThread().name}")
                val deferred = CompletableDeferred<Boolean>()
                withContext(Dispatchers.Main) {
                    println(" I'm working in thread ${Thread.currentThread().name}")
                    liveData.observeForever {
                        println(" I'm working in thread ${Thread.currentThread().name}")
                        assertTrue(it != null)
                        deferred.complete(true)
                    }
                }

                deferred.await()
            }
        }
    }
}