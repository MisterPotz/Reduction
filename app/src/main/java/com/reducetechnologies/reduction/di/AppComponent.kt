package com.reducetechnologies.reduction.di

import com.reducetechnologies.command_infrastructure.CalculationSdkBuilder
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationFragment
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.EncyclopediaFragment
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.SharedViewModel
import com.reduction_technologies.database.di.ApplicationScope
import dagger.Component

@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent {
    fun sharedViewModel() : SharedViewModel
    fun inject(fragment: EncyclopediaFragment)
    fun inject(fragment: CalculationFragment)

}