package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.helpers.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SharedViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "Энциклопедия"
    }

    // Categories list that will be loaded from SQL data
    protected val categoriesList: MutableLiveData<List<String>> =
        MutableLiveData(listOf("Category 1", "Category 2", "Category 3"))

    fun getAllItems(): LiveData<List<CommonItem>> {
        val mutableLiveData = MutableLiveData<List<CommonItem>>()
        viewModelScope.launch {
            val items = repository.getEncyclopediaItems()
            Timber.v("Obtained items")
            withContext(coroutineContext + Dispatchers.Main) {
                Timber.v("Setting livedata on main thread")
                mutableLiveData.value = items
            }
        }
        return mutableLiveData
    }
}