package com.reduction_technologies.database

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import org.jetbrains.annotations.TestOnly
import javax.inject.Singleton

@Module
class DatabaseModule(val context: Context) {
    // TODO инкапсулировать эти хелперы в сущности, которые вместо базы данных отдают список
    @Provides
    @Singleton
    fun constantDatabaseHelper(): ConstantDatabaseHelper {
        return ConstantDatabaseHelper(context)
    }

    @Provides
    @Singleton
    fun userDatabaseHelper() = UserDatabaseHelper(context)

    @Provides
    @Singleton
    fun context() = context
}

@Singleton
@Component(modules = [DatabaseModule::class])
interface DatabaseComponent {
    // injecting necessary classes into repository
    fun repository(): Repository
}
