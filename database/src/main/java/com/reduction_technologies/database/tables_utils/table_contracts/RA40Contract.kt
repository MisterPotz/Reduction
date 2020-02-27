package com.reduction_technologies.database.tables_utils.table_contracts

/**
 * Обрати внимание, что Gson переводит класс в json с именами полей такими же, как и в написании
 * самого клсса. То есть у тебя там должно быть не "RA40" в джисоне в дб, а "values". Но "values" не надо,
 * давай просто "list" (в других классах я уже начал так делать, чтобы не было путаницы. Когда просто
 * набор примитивных значений - list, когда ужже набор строк - rows)
 */
data class RA40Table(val list: List<Float>)