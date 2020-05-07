package com.reducetechnologies.di

import com.reducetechnologies.calculations.*
import com.reducetechnologies.calculations_entity.CalculationsEntity
import com.reducetechnologies.tables_utils.table_contracts.*
import com.reducetechnologies.tables_utils.table_contracts.source_datatable.SourceDataTable
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
annotation class CalculationScope

/**
 * Этот модуль будет создаваться уже в более высоком модуле (от database и выше), где уже будет конкретная
 * реализация GOSTableComponentInterface. А в этих своих классах просто в конструктор вынеси, че
 * им там нужно, чтобы это в модуле им там передавать
 */
@Module
class CalculationModule(val tables: GOSTableComponentInterface) {
    @Provides
    @CalculationScope
    fun HRCTable() = tables.getHRCTable()

    @Provides
    @CalculationScope
    fun edDataTable() = tables.getEDTable()


    @Provides
    @CalculationScope
    fun SGTTTable() = tables.getSGTTTable()

    @Provides
    @CalculationScope
    fun fatigueTable() = tables.getFatigue()

    @Provides
    @CalculationScope
    fun RA40Table() = tables.getRA40()

    @Provides
    @CalculationScope
    fun standartModulesTable() = tables.getStandartModules()

    @Provides
    @CalculationScope
    fun sourceTable() = tables.getSourceTable()

    @Provides
    @CalculationScope
    fun getAllReducersOptions(
        hrcTable: HRCTable,
        edDataTable: EDDataTable
    ): AllReducersOptionsClass {
        return AllReducersOptionsClass(
            HRCTable = hrcTable,
            edDataTable = edDataTable
        )
    }

    @Provides
    @CalculationScope
    fun getEDMethods(edDataTable: EDDataTable): EDMethodsClass {
        return EDMethodsClass(edDataTable = edDataTable)
    }

    @Provides
    @CalculationScope
    fun getDOPNMethods(sgttTable: SGTTTable, fatigueTable: FatigueTable): DOPN_MethodsClass {
        return DOPN_MethodsClass(
            tableSGTT = sgttTable,
            tablekHeFe = fatigueTable
        )
    }

    @Provides
    @CalculationScope
    fun getZUC1HMethods(
        rA40Table: RA40Table,
        standartModulesTable: StandartModulesTable
    ): ZUC1HMethodsClass {
        return ZUC1HMethodsClass(RA40 = rA40Table, MStandart = standartModulesTable)
    }

    @Provides
    @CalculationScope
    fun getZUCEPMethods(): ZUCEPMethodsClass {
        return ZUCEPMethodsClass()
    }

    @Provides
    @CalculationScope
    fun getZUC2HMethods(): ZUC2HMethodsClass {
        return ZUC2HMethodsClass()
    }

    @Provides
    @CalculationScope
    fun getZUCFMethods(): ZUCFMethodsClass {
        return ZUCFMethodsClass()
    }

    /*@Provides
    @CalculationScope
    fun getCalculationsEntity(inputData: InputData): CalculationsEntity {
        return CalculationsEntity(inputData = inputData)
    }*/
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

@Subcomponent(modules = [CalculationModule::class])
@CalculationScope
interface CalculationsComponent {
    fun getAllReducersOptions(): AllReducersOptionsClass
    fun getEDMethods(): EDMethodsClass
    fun getDOPNMethods(): DOPN_MethodsClass
    fun getZUC1HMethods(): ZUC1HMethodsClass
    fun getZUCEPMethods(): ZUCEPMethodsClass
    fun getZUC2HMethods(): ZUC2HMethodsClass
    fun getZUCFMethods(): ZUCFMethodsClass
    fun getZUCMethods(): ZUCMethodsClass
    fun getZCREDMethods(): ZCREDMethodsClass
    /*fun getCalculationsEntity(inputData: InputData): CalculationsEntity*/

    fun getHRCTable() : HRCTable
    fun getEdDataTable() : EDDataTable
    fun getSGTTTable() : SGTTTable
    fun getFatigueTable() : FatigueTable
    fun getRa40Table() : RA40Table
    fun getStandardModules() : StandartModulesTable
    fun getSourceTable() : SourceDataTable

    @Subcomponent.Builder
    interface Builder {
        fun build(): CalculationsComponent

        fun calculationModule(module: CalculationModule): Builder
    }
}