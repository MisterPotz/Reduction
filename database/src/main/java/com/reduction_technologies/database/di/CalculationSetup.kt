package com.reduction_technologies.database.di

import com.reduction_technologies.database.helpers.Repository
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
class CalculationSetup {
    @Provides
    @ApplicationScope
    fun getStorage(repository: Repository) : GOSTableStorage {
        return GOSTableStorage(repository)
    }
}

@Component(modules = [CalculationSetup::class, DatabaseModule::class])
@ApplicationScope
interface CalculationSetupComponent {
    fun getStorage() : GOSTableStorage
}