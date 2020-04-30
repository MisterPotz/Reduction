package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.lifecycle.*
import com.reducetechnologies.di.CalculationSdkComponent
import com.reducetechnologies.reduction.android.util.CategoryAdapterPositionSaver
import com.reducetechnologies.reduction.android.util.common_item_util.CommonItemUtils
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationSdkCommute
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationSdkHelper
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.di.ApplicationScope
import com.reduction_technologies.database.helpers.CategoryTag
import com.reduction_technologies.database.helpers.Repository
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
class SharedViewModel @Inject constructor(
    private val repository: Repository,
    private val componentFactory: Provider<CalculationSdkComponent.Factory>
) : ViewModel() {

    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "Энциклопедия"
    }

    val commonItemUtils = CommonItemUtils()

    val calcSdkHelper: CalculationSdkHelper =
        CalculationSdkHelper(
            componentFactory
        )

    private val _allEncyclopdiaItems: LiveData<List<CommonItem>> by lazy {
        updateAllEncyclopediaItems()
    }

    private val sortedByTagItems: LiveData<Map<CategoryTag, List<CommonItem>>> by lazy {
        Transformations.switchMap(_allEncyclopdiaItems) {
            MutableLiveData(commonItemUtils.splitByTags(it))
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

    fun startCalculation(): CalculationSdkCommute? {
        // already calculating
        if (calcSdkHelper.isActive) {
            return null
        }
        return calcSdkHelper.startCalculation()
    }

    fun getActualCommute() : CalculationSdkCommute? {
        return calcSdkHelper.getCommuteIfActive()
    }
}