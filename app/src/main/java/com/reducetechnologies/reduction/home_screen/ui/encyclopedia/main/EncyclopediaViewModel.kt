package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reduction_technologies.database.helpers.Repository
import javax.inject.Inject

class EncyclopediaViewModel @Inject constructor(repository: Repository): ViewModel() {
    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "Энциклопедия"
    }

    // Categories list that will be loaded from SQL data
    protected val categoriesList : MutableLiveData<List<String>> =
        MutableLiveData(listOf("Category 1", "Category 2", "Category 3"))

//    fun getAllItems() : LiveData()
}