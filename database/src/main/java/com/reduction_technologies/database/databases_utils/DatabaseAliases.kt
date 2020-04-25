package com.reduction_technologies.database.databases_utils

import com.reduction_technologies.database.helpers.Repository
import com.reduction_technologies.database.json_utils.GsonRegister

fun constMainTable() = DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!
fun userMainTable() = DatabaseType.User.tables[UserTables.Favorites]!!

