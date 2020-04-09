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

@Component(modules = [SomeModule::class])
@Singleton
interface TestComponent2 {
    fun produceSome() : SomeA
}

@Component(modules = [SomeModule::class])
@Singleton
interface TestComponent3 {
    fun produceSome() : SomeA
}

@Component(modules = [SomeModule::class])
@Singleton
interface TestComponent4 {
    fun produceSome() : SomeA
}

fun main(string : Array<String>) {
    val `I FUCKING HATE THIS FUCKING STUPID SHITTY DUMB SITUATION GODDAMIT` = DaggerTestComponent.builder()
        .someModule(SomeModule())
        .build()

    val `I FUCKING LOST FUCKING DAMN TIME STUPD SHITTY DUMB AAAAAAAAAAAA` = DaggerTestComponent2.builder()
        .someModule(SomeModule())
        .build()
}