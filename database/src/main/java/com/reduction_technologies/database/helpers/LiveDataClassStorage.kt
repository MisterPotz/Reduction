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
class LiveDataClassStorage<T>{
    private val classHashMap: MutableMap<T, MutableLiveData<out Any>> = mutableMapOf()

    private fun <C : Any> registerTag(type : T) : MutableLiveData<C> {
        if (checkContains(type)) {
            throw IllegalStateException("Already registered type")
        }
        val liveData = MutableLiveData<C>()
        classHashMap[type] = liveData
        return liveData
    }


    fun <C: Any> registerOrReturn(type: T) : MutableLiveData<C> {
        if (checkContains(type)) {
            return getLiveData(type)
        } else {
            return registerTag(type)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <C : Any> getLiveData(type : T) : MutableLiveData<C> {
        if (checkContains(type)) {
            return classHashMap[type]!! as MutableLiveData<C>
        }
        throw IllegalStateException("Cannot find the type")
    }

    private fun checkContains(type: T) : Boolean {
        return classHashMap.containsKey(type)
    }
}