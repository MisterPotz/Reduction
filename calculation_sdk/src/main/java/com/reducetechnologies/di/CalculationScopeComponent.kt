package com.reducetechnologies.di

import com.reducetechnologies.calculations.AllReducersOptionsClass
import com.reducetechnologies.calculations.EDMethodsClass
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope
/**
 * Метка для обозначения того, что класс, выдаваемый под ней должен быть сингльтоном на уровне
 * только вычислительного экрана
 */

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class CalculationScope
/**
 * Этот модуль будет создаваться уже в более высоком модуле (от database и выше), где уже будет конкретная
 * реализация GOSTableComponentInterface. А в этих своих классах просто в конструктор вынеси, че
 * им там нужно, чтобы это в модуле им там передавать
 */
@Module
class CalculationModule(val tableMaster : GOSTableComponentInterface ) {
    @Provides
    @CalculationScope
    fun getAllReducerOptionsMethods() : AllReducersOptionsClass {
        return AllReducersOptionsClass(tableHRC = tableMaster.getHRCTable(),
            tableEDData = tableMaster.getEDTable())
    }
    @Provides
    @CalculationScope
    fun getEDMethods() : EDMethodsClass {
        return EDMethodsClass(tableEDData = tableMaster.getEDTable())
    }
}
@Component(modules = [CalculationModule::class])
@CalculationScope
interface CalculationComponent {
    fun getAllReducerOptionsMethods() : AllReducersOptionsClass
    fun getEDMethods() : EDMethodsClass
}
