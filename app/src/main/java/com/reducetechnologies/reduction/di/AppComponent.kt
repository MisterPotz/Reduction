package com.reducetechnologies.reduction.di

import com.reduction_technologies.database.di.GOSTableStorage
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationFragment
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.FlowFragment
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.EncyclopediaFragment
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import com.reducetechnologies.reduction.home_screen.ui.favorites.FavoritesMain
import com.reduction_technologies.database.di.ApplicationScope
import dagger.Component
import javax.inject.Provider

@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent {
    fun sharedViewModel(): SharedViewModel
    fun inject(fragment: EncyclopediaFragment)
    fun inject(fragment: CalculationFragment)
    fun inject(fragment: FlowFragment)
    fun inject(fragment: FavoritesMain)

    fun calculationStorage() : Provider<GOSTableStorage>
}