package com.reducetechnologies.reduction.android.util

import android.app.Application
import com.reducetechnologies.reduction.di.AppComponent
import com.reducetechnologies.reduction.di.DaggerAppComponent

class App : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().build()
    }
}
