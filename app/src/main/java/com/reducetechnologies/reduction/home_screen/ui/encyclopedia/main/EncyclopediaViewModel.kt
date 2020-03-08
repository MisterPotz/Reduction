package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reducetechnologies.reduction.R

class EncyclopediaViewModel : ViewModel() {
    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "Энциклопедия"
    }

    // Categories list that will be loaded from SQL data
    protected val categoriesList : MutableLiveData<List<String>> = MutableLiveData(listOf("Category 1", "Category 2", "Category 3"))

    fun categoriesListLD() : LiveData<List<String>> {
        return categoriesList
    }
}