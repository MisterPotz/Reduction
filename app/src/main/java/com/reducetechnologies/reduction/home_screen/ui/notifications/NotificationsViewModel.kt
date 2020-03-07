package com.reducetechnologies.reduction.home_screen.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {
    val text: LiveData<String> = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
}