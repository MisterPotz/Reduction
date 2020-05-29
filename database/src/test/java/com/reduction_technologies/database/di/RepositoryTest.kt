package com.reduction_technologies.database.di

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.helpers.AppLocale
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class RepositoryTest {
    lateinit var databaseComponent: DatabaseComponent

    @get:Rule
    val rule = InstantTaskExecutorRule()//если эта дичь не видит её, нужно синхронизировать грэдл

    @Before
    fun setUp() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()
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

    @org.junit.Test
    fun favoriteInsertion() {
        val repository = databaseComponent.repository()
        var assertEmpty = true;
        runBlocking {
            val liveData = repository.getFavorites()
            print("Asserting liveData")
            liveData.observeForever {
                println("Is empty check")
                assertTrue(it.isEmpty()  == assertEmpty)
            }
            assertEmpty = false
            println("not empty check")
            repository.addFavoriteItem(CommonItem(title = "Opa", tag = "TABLE"))

        }
    }
}