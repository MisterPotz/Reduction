package com.reduction_technologies.database.di

import com.reducetechnologies.di.GOSTableComponentInterface
import com.reducetechnologies.tables_utils.table_contracts.*
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.tables_utils.table_contracts.FatigueTable
import com.reduction_technologies.database.tables_utils.table_contracts.G0Table
import com.reduction_technologies.database.tables_utils.table_contracts.source_datatable.SourceDataTable
import javax.inject.Scope

/**
 * Сингльтон на уровне вычислений
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class CalculationScope

/**
 * Из-за перехода на новый способ добытия табличек, получение их каждоый отдельно через метод
 * не представляется рациональным, так как на каждый запрос в общей сумме уйдет больше времени,
 * чем если сразу запарсить все нужные. Поэтому резонно сделать адаптер к уже определенному интерфейсу,
 * чтобы не лопатить calculation_sdk (да здравствует ООП!)
 */
class GOSTableAdapter(val tableHolder: TableHolder) : GOSTableComponentInterface {
    override fun getFatigue(): FatigueTable = tableHolder.fatigue

    override fun getSourceTable(): SourceDataTable = tableHolder.source_data

    override fun getG0(): G0Table = tableHolder.g_0

    override fun getEDTable(): EDDataTable = tableHolder.EDData

    override fun getHRCTable(): HRCTable = tableHolder.HRC

    override fun getRA40(): RA40Table = tableHolder.ra40

    override fun getSGTTTable(): SGTTTable = tableHolder.SGTT

    override fun getStandartModules(): StandartModulesTable = tableHolder.modules

    override fun getTIP_TipreTable(): Tip_TipreTable = tableHolder.TIP_Tipre
}