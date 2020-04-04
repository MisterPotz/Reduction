package com.reduction_technologies.database.di

import com.reducetechnologies.di.GOSTableComponentInterface
import com.reduction_technologies.database.databases_utils.*
import com.reduction_technologies.database.helpers.Repository
import com.reduction_technologies.database.json_utils.GsonRegister
import com.reduction_technologies.database.tables_utils.GOSTableContract.FATIGUE_CALCULATION_23
import com.reduction_technologies.database.tables_utils.GOSTableContract.G_0
import com.reduction_technologies.database.tables_utils.GOSTableContract.SOURCE_DATA
import com.reduction_technologies.database.tables_utils.table_contracts.FatigueTable
import com.reduction_technologies.database.tables_utils.table_contracts.G0Table
import com.reduction_technologies.database.tables_utils.table_contracts.source_datatable.SourceDataTable
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

/**
 * Сингльтон на уровне вычислений
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class CalculationScope

/**
 * Declares how to deal with dependencies described in:
 * @see GOSTableComponentInterface
 * Module provides its child components with table instances
 */
@Module
class GOSTableModule() {
    @Provides
    @CalculationScope
    fun fatigue(repository: Repository): FatigueTable {
        return obtainTable(repository, FatigueTable, FATIGUE_CALCULATION_23)
    }

    @Provides
    @CalculationScope
    fun source(repository: Repository): SourceDataTable {
        return obtainTable(repository, SourceDataTable, SOURCE_DATA)
    }

    @Provides
    @CalculationScope
    fun g0(repository: Repository): G0Table {
        return obtainTable(repository, G0Table, G_0)
    }
}

/**
 * Чтобы можно было тестить все в calculation_sdk, это либо
 */
@Component(
    dependencies = [DatabaseComponent::class],
    modules = [GOSTableModule::class]
)
@CalculationScope
interface GOSTableComponent : GOSTableComponentInterface {
    // dependencies must automatically come from gostablecomponentinterface
}