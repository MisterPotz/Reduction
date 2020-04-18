package com.reduction_technologies.database.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.reflect.KClass

/**
 * Stores livedata instances with access by class type.
 * Unique class == unique livedata.
 * When client has new value for live data - it sets it on stored livedata and changes are propgated
 * downstream across app.
 */
class LiveDataClassStorage {
    private val classHashMap: MutableMap<KClass<out Any>, LiveData<out Any>> = mutableMapOf()

    fun <T : Any> registerType(type : KClass<T>) : MutableLiveData<T> {
        val liveData = MutableLiveData<T>()
        classHashMap[type] = liveData
        return liveData
    }
}