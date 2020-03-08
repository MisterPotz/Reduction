package com.reducetechnologies.reduction.home_screen.ui.calculation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculationViewModel : ViewModel() {
    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
}