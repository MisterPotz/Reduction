package com.reducetechnologies.reduction.di

import com.reduction_technologies.database.di.DatabaseModule
import dagger.Module

@Module(includes = [DatabaseModule::class])
class AppModule {
}