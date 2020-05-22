package com.reducetechnologies.reduction.di

import com.reduction_technologies.database.di.CalculationSetup
import com.reduction_technologies.database.di.DatabaseModule
import dagger.Module

@Module(includes = [DatabaseModule::class, CalculationSetup::class])
class AppModule(){

}