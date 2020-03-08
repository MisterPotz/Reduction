package com.reducetechnologies.reduction.home_screen.ui.favorites.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val text: MutableLiveData<String> = MutableLiveData<String>("empty")
}
