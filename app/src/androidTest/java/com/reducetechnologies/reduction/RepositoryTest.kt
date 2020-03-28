package com.reducetechnologies.reduction

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.GsonBuilder
import com.reducetechnologies.reduction.android.util.App
import com.reduction_technologies.database.*
import com.reduction_technologies.database.databases_utils.*
import com.reduction_technologies.database.tables_utils.table_contracts.FatigueTable
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    lateinit var databaseComponent: DatabaseComponent
    @Before
    fun setUp() {
        val context = getApplicationContext<App>()
        // Using dependencies to create component
        databaseComponent = DaggerDatabaseComponent.builder()
            .databaseModule(DatabaseModule(context))
            .build()
    }

    // Performing simple queries
    @Test
    fun obtain_commonitem_from_cursor() {
        val database = databaseComponent.repository()
            .constantDatabaseHelper.readableDatabase
        // Working with constant table, with table "EncyclpediaItems"
        val table = DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!
        // casting list of columns into array
        val columns = table.columns.map { it.castString() }.toTypedArray()
        // querrying all items with test tag
        val cursor = database.query(
            table.name, columns,
            "${Columns.TAG} = ?", arrayOf(Tags.TEST.castString()), null, null, null
        )
        assertTrue(cursor.count == 1)

        val item = CursorCommonItemReader.readCursor(cursor, 0)

        val expected =
            CommonItem(null, "testing item", Tags.TEST.castString(), "used for test", null)

        assertTrue(expected.compare(item))
    }

    /**
     * Get commonitem from cursor and cast it to a table instance
     */
    @Test
    fun obtain_table_from_cursor() {
        val database = databaseComponent.repository().constantDatabaseHelper.readableDatabase

        val table = DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!

        val columns = table.columns.map {it.castString()}.toTypedArray()

        val cursor = database.query(
                table.name, columns,
        "${Columns.TAG} = ?", arrayOf(Tags.TABLE.castString()), null, null, null
        )

        val item = CursorCommonItemReader.readCursor(cursor, 0)

        val string = item.additional!!
        val gson = GsonBuilder()
            .create()

        val fatigue = gson.fromJson(string, FatigueTable::class.java)

        assertTrue(fatigue != null)
    }
}