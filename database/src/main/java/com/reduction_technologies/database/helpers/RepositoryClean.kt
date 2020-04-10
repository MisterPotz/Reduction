package com.reduction_technologies.database.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.databases_utils.DatabaseType
import com.reduction_technologies.database.databases_utils.constMainTable
import com.reduction_technologies.database.databases_utils.userMainTable
import com.reduction_technologies.database.di.*
import com.reducetechnologies.tables_utils.TableHolder
import javax.inject.Inject

interface TableManager {
    fun getAllTables(): LiveData<TableHolder>
}

interface ConstantDBManager {
    fun getAllEncyclopediaItems(): LiveData<List<CommonItem>>
}

interface UserDBManager {
    fun getAllUserItems(): LiveData<List<CommonItem>>

    fun insertUsertItems(items: List<CommonItem>)
    fun deleteUserItems(items: List<CommonItem>)
}

/**
 * In future may have also some network thingies to wrap them and to use them - that's a real repository pattern!
 * Класс получает контекст и с помощью даггера разворачивает все, что ему здесь надо.
 */
// TODO обложить тестами? Посадить конструктор на зависимости от билдер
class RepositoryClean @Inject constructor(
    private val databaseComponent: DatabaseComponent,
    private val tableComponent: GOSTableComponent
) : TableManager, ConstantDBManager, UserDBManager {
    override fun getAllTables(): LiveData<TableHolder> {
        // making asynchronous request to the database
        return liveData {
            emit(TableHolder.tableHolderFromComponen(tableComponent))
        }
    }

    override fun getAllEncyclopediaItems(): LiveData<List<CommonItem>> {
        return liveData {
            // Making a request and releasing it
            val repository = databaseComponent.repository()
            emit(
                repository.getAllItemsFrom(DatabaseType.Constant, constMainTable())
            )
        }
    }

    override fun getAllUserItems(): LiveData<List<CommonItem>> {
        return liveData {
            // Making a request and releasing it
            val repository = databaseComponent.repository()
            emit(
                repository.getAllItemsFrom(DatabaseType.User, userMainTable())
            )
        }
    }

    override fun insertUsertItems(items: List<CommonItem>) {
        TODO("Not yet implemented")
    }

    override fun deleteUserItems(items: List<CommonItem>) {
        TODO("Not yet implemented")
    }
}