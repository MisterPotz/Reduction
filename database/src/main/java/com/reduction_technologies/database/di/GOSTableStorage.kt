package com.reduction_technologies.database.di

import com.reducetechnologies.di.GOSTableComponentInterface
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.helpers.Repository
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class GOSTableStorage @Inject constructor(private val rep: Repository) {
    private var gosTableComponentInterface: GOSTableComponentInterface? = null

    fun isInit(): Boolean {
        return gosTableComponentInterface != null
    }

    suspend fun init(): GOSTableComponentInterface {
        val tables = CoroutineScope(coroutineContext + Dispatchers.IO).async{
            rep.getTables()
        }

        Timber.i("Waiting tables")
        gosTableComponentInterface =
            GOSTableAdapter(
                tableHolder = tables.await()
            )
        return gosTableComponentInterface!!
    }

    fun obtain(): GOSTableComponentInterface = gosTableComponentInterface!!
}