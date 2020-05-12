package com.reducetechnologies.reduction.android.util

import android.app.Application
import com.reducetechnologies.reduction.di.AppComponent
import com.reducetechnologies.reduction.di.DaggerAppComponent
import com.reduction_technologies.database.di.DatabaseModule
import com.reduction_technologies.database.helpers.AppLocale

class App : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().databaseModule(DatabaseModule(applicationContext, locale = AppLocale.RU)).build()
    }
}
