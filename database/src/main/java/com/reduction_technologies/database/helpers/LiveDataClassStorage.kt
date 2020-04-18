package com.reduction_technologies.database.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import java.lang.IllegalStateException
import kotlin.reflect.KClass

/**
 * Stores livedata instances with access by class type.
 * Unique class == unique livedata.
 * When client has new value for live data - it sets it on stored livedata and changes are propgated
 * downstream across app.
 */
class LiveDataClassStorage {
    private val classHashMap: MutableMap<KClass<out Any>, MutableLiveData<out Any>> = mutableMapOf()

    fun <T : Any> registerType(type : KClass<T>) : MutableLiveData<T> {
        if (checkContains(type)) {
            throw IllegalStateException("Already registered type")
        }
        val liveData = MutableLiveData<T>()
        classHashMap[type] = liveData
        return liveData
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getLiveData(type : KClass<T>) : MutableLiveData<T> {
        if (checkContains(type)) {
            return classHashMap[type]!! as MutableLiveData<T>
        }
        throw IllegalStateException("Cannot find the type")
    }

    fun <T: Any> checkContains(type: KClass<T>) : Boolean {
        return classHashMap.containsKey(type)
    }
}