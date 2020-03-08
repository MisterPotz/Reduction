package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EncyclopediaViewModel : ViewModel() {
    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
}