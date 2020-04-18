package com.reduction_technologies.database.helpers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.databases_utils.CommonItem
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

/**
 * The purpose of this class is to provide the rest code of application with useful data related
 * to GOST tables, encyclopedia, and user favorite items.
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

    // get all tables from the database asynchonously, suspend - for structured concurrency
    suspend fun getTables(): TableHolder {
        // enforcing structured concurrency
        return withContext(coroutineContext + Dispatchers.IO) {
            constantDatabaseHelper.getTables()
        }
    }

    // get all encyclopedia items from the database asynchonously, suspend - for structured concurrency
    suspend fun getEncyclopediaItems(): List<CommonItem> {
        // enforcing structured concurrency
        return withContext(coroutineContext + Dispatchers.IO) {
            constantDatabaseHelper.getAllItems()
        }
    }
}


