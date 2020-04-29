package com.reducetechnologies.di

import com.reducetechnologies.command_infrastructure.CalculationSdkBuilder
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

/**
 * Метка для обозначения того, что класс, выдаваемый под ней должен быть сингльтоном на уровне
 * только вычислительного экрана
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class CalculationSdkScope

@Module
class CalculationSdkModule() {
    @Provides
    @CalculationSdkScope
    fun calculationSdkBuilder() : CalculationSdkBuilder {
        return CalculationSdkBuilder()
    }

}

@CalculationSdkScope
@Subcomponent(modules = [CalculationSdkModule::class])
interface CalculationSdkComponent {
    fun getBuilder() : CalculationSdkBuilder

    @Subcomponent.Factory
    interface Factory {
        fun build() : CalculationSdkComponent
    }
}