package com.reduction_technologies.database.di

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

    private fun constantDatabaseHelper(): ConstantDatabaseHelper {
        return ConstantDatabaseHelper(context)
    }

    private fun userDatabaseHelper() =
        UserDatabaseHelper(context)

    @Provides
    @ApplicationScope
    fun context() = context

    @Provides
    @ApplicationScope
    fun repository(): Repository {
        return Repository(context(), constantDatabaseHelper(), userDatabaseHelper())
    }
}


@Component(modules = [DatabaseModule::class])
@ApplicationScope
interface DatabaseComponent {
    // injecting necessary classes into repository
    fun repository(): Repository
}
