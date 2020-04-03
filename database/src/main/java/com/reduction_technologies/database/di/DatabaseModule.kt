package com.reduction_technologies.database.di

import android.content.Context
import com.reduction_technologies.database.helpers.ConstantDatabaseHelper
import com.reduction_technologies.database.helpers.Repository
import com.reduction_technologies.database.helpers.UserDatabaseHelper
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule(val context: Context) {
    // TODO инкапсулировать эти хелперы в сущности, которые вместо базы данных отдают список
    @Provides
    @ApplicationScope
    fun constantDatabaseHelper(context: Context): ConstantDatabaseHelper {
        return ConstantDatabaseHelper(context)
    }

    @Provides
    @ApplicationScope
    fun userDatabaseHelper(context: Context) =
        UserDatabaseHelper(context)

    @Provides
    @ApplicationScope
    fun context() = context
}

@ApplicationScope
@Component(modules = [DatabaseModule::class])
interface DatabaseComponent {
    // injecting necessary classes into repository
    fun repository(): Repository
}
