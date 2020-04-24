package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.lifecycle.*
import com.reducetechnologies.reduction.android.util.CategoryAdapterPositionSaver
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.helpers.CategoryTag
import com.reduction_technologies.database.helpers.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedViewModel  @Inject constructor(private val repository: Repository) :
    ViewModel() {

    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "Энциклопедия"
    }

    private val allItemsLiveData: MutableLiveData<List<CommonItem>> = MutableLiveData()
    private val sortedItemsLiveData: LiveData<Map<CategoryTag, List<CommonItem>>> =
        Transformations.switchMap<List<CommonItem>, Map<CategoryTag, List<CommonItem>>>(
            allItemsLiveData
        ) {
            MutableLiveData(splitByTags(it))
        }

    private val categoriesAdapterSaver: CategoryAdapterPositionSaver<CategoryTag> =
        CategoryAdapterPositionSaver()

    fun getAllItems(): LiveData<List<CommonItem>> {
        viewModelScope.launch {
            val items = repository.getEncyclopediaItems()
            Timber.v("Obtained items")
            withContext(coroutineContext + Dispatchers.Main) {
                Timber.v("Setting livedata on main thread")
                allItemsLiveData.value = items
            }
        }
//        viewModelScope.launch {
//            delay(3000)
//            allItemsLiveData.value = allItemsLiveData.value!!.toMutableList().apply {
//                add(CommonItem(20, "testing add", "variable", null, null))
//                add(CommonItem(22, "var 2", "variable", null, null))
//                add(CommonItem(24, "var 3", "variable", null, null))
////                add(CommonItem(27, "var 4", "variable", null, null))
//
//                add(CommonItem(21, "new table", "table", null, null))
//                add(CommonItem(26, "table 2", "table", null, null))
//
//            }
//        }
        return allItemsLiveData
    }

    fun getAllSortedItems(): LiveData<Map<CategoryTag, List<CommonItem>>> {
        getAllItems()
        return sortedItemsLiveData
    }

    fun getSavedLayoutPositions(): CategoryAdapterPositionSaver<CategoryTag> {
        return categoriesAdapterSaver
    }

    // TODO здесь должна быть структура / логика, которая бы била коммон итемы из репозитории
    //  по заданным правилам на категории. Тогда бы чуваки, которые бы использовали эту логику, могли бы не париться
    //  об этом.


    /**
     * Maps list into map of iterator overs original list
     */
    private fun splitByTags(list: List<CommonItem>): Map<CategoryTag, List<CommonItem>> {
        val sorted = mutableMapOf<CategoryTag, MutableList<CommonItem>>()
        list.map { commonItem ->
            val categoryTag = CategoryTag.values().find { it.title == commonItem.tag }
//            Timber.i("comminItem: ${commonItem.title} goes to $categoryTag")

            if (categoryTag != null) {
                if (categoryTag !in sorted.keys) {
                    sorted[categoryTag] = mutableListOf(commonItem)
                } else {
                    sorted[categoryTag]!!.add(commonItem)
                }
            }
        }
        return sorted
    }
}