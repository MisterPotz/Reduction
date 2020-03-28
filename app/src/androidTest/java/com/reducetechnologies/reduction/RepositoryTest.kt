package com.reducetechnologies.reduction

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.reducetechnologies.reduction.android.util.App
import com.reduction_technologies.database.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun obtainDatabase() {
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
        val item =
            cursor.let {
                it.moveToPosition(0);
                it.getString(it.getColumnIndex(Columns.TITLE.castString()))
            }
        println(item)
        assertEquals("testing item", item)
    }
}