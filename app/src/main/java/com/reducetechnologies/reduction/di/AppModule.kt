package com.reducetechnologies.reduction.di

import com.reducetechnologies.di.CalculationSdkComponent
import com.reduction_technologies.database.di.DatabaseModule
import com.reduction_technologies.database.helpers.AppLocale
import dagger.Module

@Module(includes = [DatabaseModule::class], subcomponents = [CalculationSdkComponent::class])
class AppModule(){


}