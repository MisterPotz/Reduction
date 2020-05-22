package com.reduction_technologies.database.helpers

import android.content.Context
import androidx.lifecycle.LiveData
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.di.ApplicationScope
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception
import java.lang.RuntimeException
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

enum class AppLocale{ RU }

/**
 * The purpose of this class is to provide the rest code of application with useful data related
 * to GOST tables, encyclopedia, and user favorite items.
 *
 * Repository has two objectives:
 * 1) OBtaining data from various sources.
 * 2) Caching once obtained data in LiveData format - so once the data us updated,
 * repository can propagate those changes downstream in livedata, if it was once returned.
 */
@ApplicationScope
class Repository @Inject internal constructor(
    internal val context: Context,
    /**
     * THe field is injectable so instances of constant database can be mocked
     */
    internal val constantDatabaseHelper: ConstantDatabaseHelper,
    /**
     * Injectible for the sake of testing and reusability
     */
    internal val userDatabaseHelper: UserDatabaseHelper,

    private val locale : AppLocale
) {
    internal enum class LiveDataType { TABLES, ALL_ENCYCLOPEDIA }

    private val storageDelegate = LiveDataClassStorage<LiveDataType>()

    // get all tables from the database asynchonously, suspend - for structured concurrency
    fun getTables(): TableHolder {
        val tables = constantDatabaseHelper.getTables(locale)
        return tables
    }

    // get all encyclopedia items from the database asynchonously, suspend - for structured concurrency
    suspend fun getEncyclopediaItems(): LiveData<List<CommonItem>> {
        if (storageDelegate.checkContains(LiveDataType.ALL_ENCYCLOPEDIA)) {
            return storageDelegate.registerOrReturn<List<CommonItem>>(LiveDataType.ALL_ENCYCLOPEDIA)
        }
        return updateEncyclopediaItems()
    }

    private suspend fun updateEncyclopediaItems() : LiveData<List<CommonItem>> {
        val liveData =
            storageDelegate.registerOrReturn<List<CommonItem>>(LiveDataType.ALL_ENCYCLOPEDIA)

        // enforcing structured concurrency
        val job = CoroutineScope(coroutineContext + Dispatchers.IO).launch {
            withTimeout(10000) {
                val allItems = constantDatabaseHelper.getAllItems(locale)
                liveData.postValue(allItems)
            }
        }
        job.invokeOnCompletion { cause: Throwable? ->
            if (cause != null) {
                throw cause
            }
        }
        return liveData
    }
}


