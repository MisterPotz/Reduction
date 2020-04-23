package com.reducetechnologies.di
import com.reducetechnologies.calculations.*
import com.reducetechnologies.tables.RA40
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
class CalculationModule(val tables : GOSTableComponentInterface ) {
    @Provides
    @CalculationScope
    fun getAllReducersOptions() : AllReducersOptionsClass {
        return AllReducersOptionsClass(HRCTable = tables.getHRCTable(),
            edDataTable = tables.getEDTable())
    }
    @Provides
    @CalculationScope
    fun getEDMethods() : EDMethodsClass {
        return EDMethodsClass(edDataTable = tables.getEDTable())
    }
    @Provides
    @CalculationScope
    fun getDOPNMethods() : DOPN_MethodsClass{
        return DOPN_MethodsClass(tableSGTT = tables.getSGTTTable(),
            tablekHeFe = tables.getFatigue())
    }
    @Provides
    @CalculationScope
    fun getZUC1HMethods() : ZUC1HMethodsClass{
        return ZUC1HMethodsClass(RA40 = tables.getRA40(), MStandart = tables.getStandartModules())
    }
    @Provides
    @CalculationScope
    fun getZUCEPMethods() : ZUCEPMethodsClass{
        return ZUCEPMethodsClass()
    }
    @Provides
    @CalculationScope
    fun getZUC2HMethods() : ZUC2HMethodsClass{
        return ZUC2HMethodsClass()
    }
    @Provides
    @CalculationScope
    fun getZUCFMethods() : ZUCFMethodsClass{
        return ZUCFMethodsClass()
    }
    /*@Provides
    @CalculationScope
    fun getZCREDMethods() : ZCREDMethodsClass{
        return ZCREDMethodsClass()
    }*/
    /*@Provides
    @CalculationScope
    fun getZUCMethods() : ZUCMethodsClass{
        return ZUCMethodsClass(dopnMethods = )
    }*/
    // .... и  так далее по аналогии
}
@Component(modules = [CalculationModule::class])
@CalculationScope
interface CalculationsComponent {
    fun getAllReducersOptions() : AllReducersOptionsClass
    fun getEDMethods() : EDMethodsClass
    fun getDOPNMethods() : DOPN_MethodsClass
    fun getZUC1HMethods() : ZUC1HMethodsClass
    fun getZUCEPMethods() : ZUCEPMethodsClass
    fun getZUC2HMethods() : ZUC2HMethodsClass
    fun getZUCFMethods() : ZUCFMethodsClass
    fun getZUCMethods() : ZUCMethodsClass
    fun getZCREDMethods() : ZCREDMethodsClass
}