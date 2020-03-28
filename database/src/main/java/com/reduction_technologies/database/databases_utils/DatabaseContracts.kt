package com.reduction_technologies.database.databases_utils

/**
 *  Is used for storing different instances to database / table registry
 *  Use this interface on companion objects
 *  @see ConstantDatabaseHelper
 *  @see UserDatabaseHelper
 */
interface DatabaseConstantsContract {
    val database: DatabaseType
}

// Databases that exist in the application and tables within these databases
enum class DatabaseType(
    val title: String,
    val version: Int,
    val tables: Map<DatabaseTable, TableContract>
) {
    Constant(
        title = "mainDatabase", version = 1, tables = mapOf(
            ConstTables.EncyclopediaItems to TableContract(
                name = "EncyclopediaItems",
                columns = listOf(
                    Columns.ID,
                    Columns.TITLE,
                    Columns.TAG,
                    Columns.ABOUT,
                    Columns.ADDITIONAL
                ),
                tags = listOf(
                    Tags.TABLE,
                    Tags.VARIABLE,
                    Tags.TERMIN,
                    Tags.TEST
                )
            )
        )
    ),
    User(
        title = "userDatabase", version = 1, tables = mapOf(
            UserTables.Favorites to TableContract(
                name = "FavoritesItems",
                columns = listOf(
                    Columns.ID,
                    Columns.TITLE,
                    Columns.TAG,
                    Columns.ABOUT,
                    Columns.ADDITIONAL
                ),
                tags = listOf(
                    Tags.TABLE,
                    Tags.VARIABLE,
                    Tags.TERMIN,
                    Tags.TEST
                )
            )
        )
    )
}

data class TableContract(
    val name: String,
    val columns: List<ColumnContract>,
    val tags: List<Tags>
)

interface DatabaseTable

enum class ConstTables : DatabaseTable { EncyclopediaItems }

enum class UserTables : DatabaseTable { Favorites }

interface ColumnContract {
    val item: String
    fun castString() : String = item
}

enum class Columns(override val item: String) :
    ColumnContract {
    ID("_ID"),
    TITLE("TITLE"),
    ABOUT("ABOUT"),
    ADDITIONAL("ADDITIONAL"),
    TAG("TAG")
}

interface TagContract {
    val item: String
    fun castString() : String = item
}

enum class Tags(override val item: String) :
    TagContract {
    TABLE("table"),
    VARIABLE("variable"),
    TERMIN("termin"),
    TEST("test")
}
