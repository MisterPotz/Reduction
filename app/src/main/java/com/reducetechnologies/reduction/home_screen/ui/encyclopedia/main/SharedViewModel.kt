package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.content.Context
import androidx.lifecycle.*
import com.google.gson.GsonBuilder
import com.reducetechnologies.calculations_entity.CalculationsEntity
import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.SortedCalculationResults
import com.reducetechnologies.reduction.android.util.common_item_util.CommonItemUtils
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationFinishCallback
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationSdkHelper
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PScreenSwitcher
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.SimplePositionSaver
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util.toCommonItem
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.di.ApplicationScope
import com.reduction_technologies.database.di.GOSTableStorage
import com.reduction_technologies.database.helpers.AppLocale
import com.reduction_technologies.database.helpers.CategoryTag
import com.reduction_technologies.database.helpers.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

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

    private var calculationResults: SortedCalculationResults? = null
    private var calculationState: Output? = null

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

    fun mapCategoryToLocal(categoryTag: CategoryTag): String {
        return when (categoryTag) {
            CategoryTag.TABLE -> context.getString(R.string.tables)
            CategoryTag.VARIABLE -> context.getString(R.string.variables)
        }
    }

    // CALCULATION RELATED CODE --------------------------------------------------------------------

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

    fun finishCurrentCalculation() {
        pScreenSwitcher = null
        if (calcSdkHelper.isActive) {
            calcSdkHelper.finishWithError(FinishedEarly)
        }
        calculationState = null
        calculationResults = null
    }

    fun getCalculationResults(): SortedCalculationResults {
        if (calculationResults == null || calculationState is Error) {
            throw IllegalStateException("Can't return results because they dont fucking exist")
        }
        return calculationResults!!
    }

    fun isCalculationActive(): Boolean {
        return calcSdkHelper.isActive
    }

    private fun setCalculationResults(calculationResults: CalculationResults) {
        Timber.i("Calculation results: $calculationResults")
        when (calculationResults) {
            is CalculationResultsContainer -> {
                Timber.i("Got ${calculationResults.reducersDataList.size} variants")
                this.calculationResults = makeSortedResults(calculationResults)
                pushCalculationToRepository(calculationResults)
            }
            is NoVariants ->{
                Timber.i("Calculation not possible")
                calculationState = CalculationNotPossible
            }
            is FinishRequested -> {
                Timber.i("Finished early")
                calculationState = FinishedEarly
            }
        }
    }

    /**
     * UI may need to picture some graphic events that happen after calculation is finished
     */
    fun startCalculation(): Boolean {
        // already calculating
        if (calcSdkHelper.isActive) {
            return false
        }
        calcSdkHelper.startCalculation(
            object : CalculationFinishCallback {
                override fun invoke(calculationResults: CalculationResults) {
                    Timber.i("Got results in ViewModel")
                    // here storing results in db with timestamps and sorting results
                    // What if finished early at this moment?
                    setCalculationResults(calculationResults)
                }
            }
        )
        // TODO в будущем, когда будут результаты, pScreenSwitcher надо будет обращать в нулл, иначе будет баг и краш
        pScreenSwitcher = PScreenSwitcher(calcSdkHelper)
        return true
    }

    suspend fun enter(pScreen: PScreen) {
        withContext(viewModelScope.coroutineContext) {
            pScreenSwitcher!!.enter()
        }
    }

    suspend fun next() {
        withContext(viewModelScope.coroutineContext) {
            // right time to fetch status of calculationsdk

            Timber.i("Current state: $calculationState")

            when(calculationState!!) {
                is Error -> Unit
                else -> pScreenSwitcher!!.next()
            }
        }
    }

    suspend fun prev() {
        withContext(viewModelScope.coroutineContext) {
            pScreenSwitcher!!.prev()
        }
    }

    fun haveNext(): Boolean {
        return pScreenSwitcher!!.haveNext()
    }

    fun havePrev(): Boolean {
        return pScreenSwitcher!!.havePrevious()
    }

    fun isNecessaryInput(): Boolean {
        return pScreenSwitcher!!.needsInput() && !pScreenSwitcher!!.currentWasValidatedSuccessfully
    }

    /**
     * Returns either normal pScreen or error - this is the only output to UI that is allowed
     */
    fun requestOutput(): Output {
        // если вычисления завершены с ошибкой - возвратить ее
        return if (calculationState != null && calculationState!! is Error) {
            calculationState!!
        } else {
            val screen = pScreenSwitcher!!.current().pScreen
            Screen(screen)
        }
    }

    fun needsAttachingLinks() : Boolean {
        return (pScreenSwitcher!!.current().isLast)
    }
}

sealed class Output
sealed class Error : Output()
object FinishedEarly : Error()
object CalculationNotPossible : Error()
class Screen(val pScreen: PScreen) : Output()

