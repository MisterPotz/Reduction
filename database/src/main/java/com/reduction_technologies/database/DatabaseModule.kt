package com.reduction_technologies.database

import android.content.Context
import com.reduction_technologies.database.helpers.ConstantDatabaseHelper
import com.reduction_technologies.database.helpers.Repository
import com.reduction_technologies.database.helpers.UserDatabaseHelper
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(val context: Context) {
    // TODO инкапсулировать эти хелперы в сущности, которые вместо базы данных отдают список
    @Provides
    @Singleton
    fun constantDatabaseHelper(context: Context): ConstantDatabaseHelper {
        return ConstantDatabaseHelper(context)
    }

    @Provides
    @Singleton
    fun userDatabaseHelper(context: Context) =
        UserDatabaseHelper(context)

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
