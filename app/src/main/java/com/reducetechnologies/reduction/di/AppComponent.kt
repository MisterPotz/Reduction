package com.reducetechnologies.reduction.di

import com.reducetechnologies.reduction.home_screen.MainActivity
import com.reduction_technologies.database.json_utils.JsonModule
import javax.inject.Singleton
import dagger.Component
/**
 * Created on : Feb 09, 2019
 * Author     : AndroidWave
 */
@Singleton
@Component(modules = [JsonModule::class])
interface AppComponent {
    fun inject(target : MainActivity)
}