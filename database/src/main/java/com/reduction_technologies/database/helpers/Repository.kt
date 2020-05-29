package com.reduction_technologies.database.helpers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.di.ApplicationScope
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

enum class AppLocale { RU }

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

    private val locale: AppLocale
) {
    internal enum class LiveDataType { TABLES, ALL_ENCYCLOPEDIA, FAVORITES }

    private val storageDelegate = LiveDataClassStorage<LiveDataType>()

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

    suspend fun getFavorites(): LiveData<List<CommonItem>> {
        if (storageDelegate.checkContains(LiveDataType.FAVORITES)) {
            return storageDelegate.registerOrReturn<List<CommonItem>>(LiveDataType.FAVORITES)
        }
        return updateFavorites()
    }

    suspend fun addFavoriteItem(commonItem: CommonItem) {
        withContext(Dispatchers.IO) {
            userDatabaseHelper.insertCommonItem(commonItem)
            updateFavorites()
        }
    }

    private suspend fun updateFavorites(): LiveData<List<CommonItem>> {
        return getLiveDataOf(LiveDataType.FAVORITES) {
            val allItems = userDatabaseHelper.getList()
            it.postValue(allItems)
        }
    }

    private suspend fun updateEncyclopediaItems(): LiveData<List<CommonItem>> {
        return getLiveDataOf(LiveDataType.ALL_ENCYCLOPEDIA) {
            val allItems = constantDatabaseHelper.getAllItems(locale)
            it.postValue(allItems)
        }
    }

    private suspend fun getLiveDataOf(
        type: LiveDataType,
        databaseBLock: (MutableLiveData<List<CommonItem>>) -> Unit
    ): LiveData<List<CommonItem>> {
        val liveData =
            storageDelegate.registerOrReturn<List<CommonItem>>(type)

        // enforcing structured concurrency
        val job = CoroutineScope(Job(coroutineContext[Job]) + Dispatchers.IO).launch {
            withTimeout(10000) {
                databaseBLock(liveData)
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


