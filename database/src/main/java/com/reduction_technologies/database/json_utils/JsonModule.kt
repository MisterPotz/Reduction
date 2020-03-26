package com.reduction_technologies.database.json_utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import javax.inject.Singleton
import dagger.Provides

@Module
class JsonModule {
    @Provides
    @Singleton
    fun gson(): Gson {
        return GsonBuilder().create()
    }
}