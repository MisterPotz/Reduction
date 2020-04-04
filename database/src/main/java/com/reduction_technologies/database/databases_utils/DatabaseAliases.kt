package com.reduction_technologies.database.databases_utils

import com.reduction_technologies.database.helpers.Repository
import com.reduction_technologies.database.json_utils.GsonRegister

fun constMainTable() = DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!
fun userMainTable() = DatabaseType.User.tables[UserTables.Favorites]!!
/**
 * Alias for function for fast conditioning against tables
 */
internal inline fun Query.tableCondition(title: String): Query {
    return When(
        Query.Clause(
            Columns.TAG.castString(), Query.Operations.EQ, Tags.TABLE.castString()
        )
    ).and(
        Query.Clause(Columns.TITLE.castString(), Query.Operations.EQ, title)
    )
}

/**
 * Alias function for retrieving tables from databases.
 * It requires gsonRegister - so each table must have a companion that implements this interaface
 */
internal inline fun <reified T> obtainTable(
    repository: Repository,
    gsonRegister: GsonRegister,
    name: String,
    contract: TableContract = DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!
): T {
    val database = repository.constantDatabaseHelper.readableDatabase

    val cursor = repository.constCursorBuilder<CommonItem>(
        contract.name, contract.columns.toTypedArray()
    ).buildQuery {
        tableCondition(name)
    }.setReader(CursorCommonItemReader).create()

    val item = cursor.getSingle()
    val string = item.additional!!
    val gson = gsonRegister.prepareGson()
    return gson.fromJson(string, T::class.java)
}
