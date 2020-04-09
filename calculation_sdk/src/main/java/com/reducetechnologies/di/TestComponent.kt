package com.reducetechnologies.di

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

data class SomeA(val int: Int)

@Module
class SomeModule() {
    @Provides
    @Singleton
    fun someA() : SomeA {
        return SomeA(5)
    }
}

@Component(modules = [SomeModule::class])
@Singleton
interface TestComponent {
    fun produceSome() : SomeA
}