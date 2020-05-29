package com.reduction_technologies.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.reduction_technologies.database.di.DaggerDatabaseComponent
import com.reduction_technologies.database.di.DatabaseComponent
import com.reduction_technologies.database.di.DatabaseModule
import com.reduction_technologies.database.helpers.AppLocale
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    lateinit var databaseComponent: DatabaseComponent

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        // Using dependencies to create component
        databaseComponent = DaggerDatabaseComponent.builder()
            .databaseModule(
                DatabaseModule(
                    context,
                    AppLocale.RU
                )
            )
            .build()
    }

 /*   @org.junit.Test
    fun favoriteInsertion() {
        val repository = databaseComponent.repository()
        var assertEmpty = true;
        runBlocking {
            val liveData = repository.getFavorites()
            print("Asserting liveData")
            withContext(Dispatchers.Main) {
                liveData.observeForever {
                    println("Is empty check")
                    assertTrue(it.isEmpty()  == assertEmpty)
                }
            }

            assertEmpty = false
            println("not empty check")
            repository.addFavoriteItem(CommonItem(title = "Opa", tag = "TABLE"))

        }
    }*/
}