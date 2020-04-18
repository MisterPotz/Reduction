package com.reducetechnologies.reduction.di

import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.EncyclopediaFragment
import com.reduction_technologies.database.di.ApplicationScope
import dagger.Component
/**
 * Created on : Feb 09, 2019
 * Author     : AndroidWave
 */
@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent {
    //fun inject(target : MainActivity)

    fun inject(fragment: EncyclopediaFragment)
}