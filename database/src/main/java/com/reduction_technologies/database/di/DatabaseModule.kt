package com.reduction_technologies.database.di

import android.content.Context
import com.reduction_technologies.database.helpers.AppLocale
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.reducetechnologies.di.CalculationModule
import com.reducetechnologies.di.CalculationsComponent
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.helpers.ConstantDatabaseHelper
import com.reduction_technologies.database.helpers.Repository
import com.reduction_technologies.database.helpers.UserDatabaseHelper
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.*
import javax.inject.Provider

@Module(subcomponents = [CalculationsComponent::class])
class DatabaseModule(val context: Context, val locale: AppLocale) {

    private fun constantDatabaseHelper(): ConstantDatabaseHelper {
        return ConstantDatabaseHelper(context)
    }

    private fun userDatabaseHelper() =
        UserDatabaseHelper(context)

    @Provides
    @ApplicationScope
    fun calculationModule(repository: Repository): Deferred<CalculationModule> {
        val deferred = CompletableDeferred<CalculationModule>()
        var liveDataTables: LiveData<TableHolder>? = null

        val observer = object : Observer<TableHolder> {
            override fun onChanged(t: TableHolder?) {
                val adapter = GOSTableAdapter(t!!)
                deferred.complete(CalculationModule(adapter))
                liveDataTables!!.removeObserver(this)
            }
        }

        CoroutineScope(Dispatchers.Main).async {
            liveDataTables = repository.getTables()
            liveDataTables!!.observeForever(observer)
        }
        return deferred
    }

    @Provides
    @ApplicationScope
    fun context() = context

    @Provides
    @ApplicationScope
    fun repository(): Repository {
        return Repository(context(), constantDatabaseHelper(), userDatabaseHelper(), locale)
    }

    @Provides
    @ApplicationScope
    fun locale() : AppLocale {
        return locale
    }
}


@Component(modules = [DatabaseModule::class])
@ApplicationScope
interface DatabaseComponent {
    // injecting necessary classes into repository
    fun repository(): Repository

    fun calculationsBuilder(): Provider<CalculationsComponent.Builder>

    fun calculationModule(): Deferred<CalculationModule>
}
