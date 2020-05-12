package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.content.Context
import androidx.lifecycle.*
import com.reducetechnologies.di.CalculationSdkComponent
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.common_item_util.CommonItemUtils
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationSdkHelper
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PScreenSwitcher
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.SimplePositionSaver
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.di.ApplicationScope
import com.reduction_technologies.database.helpers.AppLocale
import com.reduction_technologies.database.helpers.CategoryTag
import com.reduction_technologies.database.helpers.Repository
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
class SharedViewModel @Inject constructor(
    private val context : Context,
    private val repository: Repository,
    private val componentFactory: Provider<CalculationSdkComponent.Factory>,
    private val appLocale: AppLocale
) : ViewModel() {

    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "Энциклопедия"
    }

    val commonItemUtils = CommonItemUtils()

    val calcSdkHelper: CalculationSdkHelper =
        CalculationSdkHelper(
            componentFactory
        )

    var savedEncyclopediaScreenState : SimplePositionSaver? = null

    private var pScreenSwitcher : PScreenSwitcher? = null

    private val _allEncyclopdiaItems: LiveData<List<CommonItem>> by lazy {
        updateAllEncyclopediaItems()
    }

    private val sortedByTagItems: LiveData<Map<CategoryTag, List<CommonItem>>> by lazy {
        Transformations.switchMap(_allEncyclopdiaItems) {
            MutableLiveData(commonItemUtils.splitByTags(it))
        }
    }

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

    fun startCalculation(): Boolean {
        // already calculating
        if (calcSdkHelper.isActive) {
            return false
        }
        calcSdkHelper.startCalculation()
        // TODO в будущем, когда будут результаты, pScreenSwitcher надо будет обращать в нулл, иначе будет баг и краш
        pScreenSwitcher = PScreenSwitcher(calcSdkHelper)
        return true
    }

    fun isCalculationActive() : Boolean {
        return calcSdkHelper.isActive
    }

    fun screenSwitcher() : PScreenSwitcher? {
        return pScreenSwitcher
    }

    fun mapCategoryToLocal(categoryTag: CategoryTag) : String {
        return when (categoryTag) {
            CategoryTag.TABLE -> context.getString(R.string.tables)
            CategoryTag.VARIABLE -> context.getString(R.string.variables)
        }
    }
}