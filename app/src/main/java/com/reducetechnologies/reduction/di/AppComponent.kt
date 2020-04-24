package com.reducetechnologies.reduction.di

import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.EncyclopediaFragment
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import com.reduction_technologies.database.di.ApplicationScope
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    //fun inject(target : MainActivity)

    fun sharedViewModel() : SharedViewModel
    fun inject(fragment: EncyclopediaFragment)
}