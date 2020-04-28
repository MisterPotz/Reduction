package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.lifecycle.*
import com.reducetechnologies.reduction.android.util.CategoryAdapterPositionSaver
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.di.ApplicationScope
import com.reduction_technologies.database.helpers.CategoryTag
import com.reduction_technologies.database.helpers.Repository
import kotlinx.coroutines.*
import javax.inject.Inject

@ApplicationScope
class SharedViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "Энциклопедия"
    }

    private val _allEncyclopdiaItems: LiveData<List<CommonItem>> by lazy {
        updateAllEncyclopediaItems()
    }

    private val sortedByTagItems: LiveData<Map<CategoryTag, List<CommonItem>>> by lazy {
        Transformations.switchMap(_allEncyclopdiaItems) {
            MutableLiveData(splitByTags(it))
        }
    }

    private val categoriesAdapterSaver: CategoryAdapterPositionSaver<CategoryTag> =
        CategoryAdapterPositionSaver()

    fun getAllEncyclopediaItems(): LiveData<List<CommonItem>> {
        updateAllEncyclopediaItems()
        return _allEncyclopdiaItems
    }

    private fun updateAllEncyclopediaItems(): LiveData<List<CommonItem>> {
        val task = viewModelScope.async {
            repository.getEncyclopediaItems()
        }
        return runBlocking {
            task.await()
        }
    }

    fun getAllSortedItems(): LiveData<Map<CategoryTag, List<CommonItem>>> {
        updateAllEncyclopediaItems()
        return sortedByTagItems
    }

    fun getSavedLayoutPositions(): CategoryAdapterPositionSaver<CategoryTag> {
        return categoriesAdapterSaver
    }

    /**
     * Maps list into map of iterator overs original list
     */
    private fun splitByTags(list: List<CommonItem>): Map<CategoryTag, List<CommonItem>> {
        val sorted = mutableMapOf<CategoryTag, MutableList<CommonItem>>()
        list.map { commonItem ->
            val categoryTag = CategoryTag.values().find { it.title == commonItem.tag }
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