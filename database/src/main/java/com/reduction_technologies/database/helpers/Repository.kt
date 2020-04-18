package com.reduction_technologies.database.helpers

import android.content.Context
import androidx.lifecycle.LiveData
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.databases_utils.CommonItem
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

/**
 * The purpose of this class is to provide the rest code of application with useful data related
 * to GOST tables, encyclopedia, and user favorite items.
 *
 * Repository has two objectives:
 * 1) OBtaining data from various sources.
 * 2) Caching once obtained data in LiveData format - so once the data us updated,
 * repository can propagate those changes downstream in livedata, if it was once returned.
 */
class Repository @Inject internal constructor(
    internal val context: Context,
    /**
     * THe field is injectable so instances of constant database can be mocked
     */
    internal val constantDatabaseHelper: ConstantDatabaseHelper,
    /**
     * Injectible for the sake of testing and reusability
     */
    internal val userDatabaseHelper: UserDatabaseHelper
) {

    private val storageDelegate = LiveDataClassStorage()

    // get all tables from the database asynchonously, suspend - for structured concurrency
    suspend fun getTables(): LiveData<TableHolder> {
        val liveData = storageDelegate.registerType(TableHolder::class)

        // enforcing structured concurrency
        val task = CoroutineScope(coroutineContext + Dispatchers.IO).launch {
            val tables = constantDatabaseHelper.getTables()
            liveData.postValue(tables)
        }

        return liveData
    }

    // get all encyclopedia items from the database asynchonously, suspend - for structured concurrency
    suspend fun getEncyclopediaItems(): List<CommonItem> {
        // enforcing structured concurrency
        return withContext(coroutineContext + Dispatchers.IO) {
            constantDatabaseHelper.getAllItems()
        }
    }
}


