package com.reducetechnologies.reduction.android.util

import android.app.Application
import com.reducetechnologies.reduction.di.AppComponent
import com.reducetechnologies.reduction.di.DaggerAppComponent
import com.reduction_technologies.database.di.DatabaseModule
import com.reduction_technologies.database.helpers.AppLocale
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class App : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        appComponent = DaggerAppComponent.builder()
            .databaseModule(DatabaseModule(applicationContext, locale = AppLocale.RU))
            .build()
        // initialize calculation component
        appComponent.calculationStorage().get().let {
            runBlocking { it.init() }
        }
    }
}
