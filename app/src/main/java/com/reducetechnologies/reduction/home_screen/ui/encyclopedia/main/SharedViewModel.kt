package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.content.Context
import androidx.lifecycle.*
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.common_item_util.CommonItemUtils
import com.reduction_technologies.database.di.GOSTableStorage
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
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.reducetechnologies.calculations_entity.CalculationsEntity
import com.reducetechnologies.command_infrastructure.CalculationResults
import com.reducetechnologies.command_infrastructure.CalculationResultsContainer
import com.reducetechnologies.reduction.android.util.SortedCalculationResults
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationFinishCallback
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.toCommonItem
import timber.log.Timber
import java.lang.IllegalStateException
import java.sql.Time

@ApplicationScope
class SharedViewModel @Inject constructor(
    private val context: Context,
    private val repository: Repository,
    private val storageProvider: Provider<GOSTableStorage>,
    private val appLocale: AppLocale
) : ViewModel() {

    private val gson by lazy { GsonBuilder().create() }

    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "Энциклопедия"
    }

    var sortedResults: SortedCalculationResults? = null
        private set

    val commonItemUtils = CommonItemUtils()

    val calcSdkHelper: CalculationSdkHelper by lazy {
        CalculationSdkHelper(storageProvider)
    }

    var savedEncyclopediaScreenState: SimplePositionSaver? = null

    private var pScreenSwitcher: PScreenSwitcher? = null

    private val _allEncyclopdiaItems: LiveData<List<CommonItem>> by lazy {
        runBlocking {
            repository.getEncyclopediaItems()
        }
    }

    private val _favorites: LiveData<List<CommonItem>> by lazy {
        runBlocking {
            repository.getFavorites()
        }
    }

    private val sortedByTagItems: LiveData<Map<CategoryTag, List<CommonItem>>> by lazy {
        Transformations.switchMap(_allEncyclopdiaItems) {
            MutableLiveData(commonItemUtils.splitByTags(it))
        }
    }

    fun getAllEncyclopediaItems(): LiveData<List<CommonItem>> {
        fetchEncyclopediaItems()
        return _allEncyclopdiaItems
    }

    private fun fetchEncyclopediaItems() {
        viewModelScope.launch {
            repository.getEncyclopediaItems()
        }
    }

    private fun fetchFavorites() {
        viewModelScope.launch {
            repository.getFavorites()
        }
    }

    fun getAllSortedItems(): LiveData<Map<CategoryTag, List<CommonItem>>> {
        viewModelScope.launch {
            repository.getEncyclopediaItems()
        }
        return sortedByTagItems
    }


    fun getFavorites(): LiveData<List<CommonItem>> {
        fetchFavorites()
        return _favorites
    }

    /**
     * UI may need to picture some graphic events that happen after calculation is finished
     */
    fun startCalculation(onCalculationFinished: CalculationFinishCallback): Boolean {
        // already calculating
        if (calcSdkHelper.isActive) {
            return false
        }
        calcSdkHelper.startCalculation(
            object : CalculationFinishCallback {
                override fun invoke(calculationResults: CalculationResults) {
                    Timber.i("Got results in ViewModel")
                    // here storing results in db with timestamps and sorting results
                    pushCalculationToRepository(calculationResults)
                    // TODO обрабратывать пустые результаты!
                    sortedResults = makeSortedResults(calculationResults)
                    Timber.i("Dispatching results to store in db and invoking ui callback")
                    onCalculationFinished(calculationResults)
                }
            }
        )
        // TODO в будущем, когда будут результаты, pScreenSwitcher надо будет обращать в нулл, иначе будет баг и краш
        pScreenSwitcher = PScreenSwitcher(calcSdkHelper)
        return true
    }

    private fun pushCalculationToRepository(results: CalculationResults) {
        if (results !is CalculationResultsContainer) return
        if (results.reducersDataList.isEmpty()) return
        val currentSize = _favorites.value?.size ?: 0
        val commonItem = results.toCommonItem(
            "Result ${currentSize + 1}",
            gson
        )
        viewModelScope.launch {
            Timber.i("Dispatching calculation results to favorite")
            repository.addFavoriteItem(commonItem)
        }
    }

    fun finishCurrentCalculation() {
        pScreenSwitcher = null
    }

    fun isCalculationActive(): Boolean {
        return calcSdkHelper.isActive
    }

    fun screenSwitcher(): PScreenSwitcher? {
        return pScreenSwitcher
    }

    fun mapCategoryToLocal(categoryTag: CategoryTag): String {
        return when (categoryTag) {
            CategoryTag.TABLE -> context.getString(R.string.tables)
            CategoryTag.VARIABLE -> context.getString(R.string.variables)
        }
    }

    /**
     * Makes sorted results and
     */
    private fun makeSortedResults(initialResults: CalculationResults): SortedCalculationResults {
        if (initialResults !is CalculationResultsContainer) {
            throw IllegalStateException("Cant make sorted lists out of empty interface")
        }
        val weight = CalculationsEntity.sortByWeight(initialResults.reducersDataList)
        val vol = CalculationsEntity.sortByVolume(initialResults.reducersDataList)
        val sumAw = CalculationsEntity.sortBySumAW(initialResults.reducersDataList)
        val hrc = CalculationsEntity.sortByMinSumHRC(initialResults.reducersDataList)
        val diffSg = CalculationsEntity.sortByDiffSG(initialResults.reducersDataList)
        val uDesc = CalculationsEntity.sortByUDescending(initialResults.reducersDataList)

        return SortedCalculationResults(
            simple = initialResults.reducersDataList,
            weight = weight,
            sumAw = sumAw,
            diffSGD = diffSg,
            uDesc = uDesc,
            volume = vol,
            hrcMin = hrc
        )
    }
}