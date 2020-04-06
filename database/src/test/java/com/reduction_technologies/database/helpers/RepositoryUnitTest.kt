package com.reduction_technologies.database.helpers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.gson.GsonBuilder
import com.reducetechnologies.tables_utils.table_contracts.*
import com.reduction_technologies.database.databases_utils.*

import com.reduction_technologies.database.tables_utils.*
import com.reduction_technologies.database.tables_utils.table_contracts.*
import com.reduction_technologies.database.tables_utils.table_contracts.source_datatable.*

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.robolectric.RobolectricTestRunner
import com.reduction_technologies.database.di.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Если через Robolectric - то тесты раннятся в сандбоксике
 * Только JUnit4.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest= Config.NONE)
class RepositoryUnitTest {
    lateinit var databaseComponent: DatabaseComponent

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Using dependencies to create component
        databaseComponent = DaggerDatabaseComponent.builder()
            .databaseModule(DatabaseModule(context))
            .build()
    }

    // Performing simple queries
    @org.junit.Test
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

        cursor.moveToPosition(0)
        val item = CursorCommonItemReader.readItem(cursor)

        val expected =
            CommonItem(null, "testing item", Tags.TEST.castString(), "used for test", null)

        assertTrue(expected.compare(item))
    }

    /**
     * Get commonitem from cursor and cast it to a table instance
     */
    @org.junit.Test
    fun obtain_table_from_cursor_straight() {
        val database = databaseComponent.repository().constantDatabaseHelper.readableDatabase

        val table = DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!

        val columns = table.columns.map { it.castString() }.toTypedArray()

        val cursor = database.query(
            table.name, columns,
            "${Columns.TAG} = ?", arrayOf(Tags.TABLE.castString()), null, null, null
        )

        cursor.moveToPosition(0)
        val item = CursorCommonItemReader.readItem(cursor)

        val string = item.additional!!
        val gson = GsonBuilder()
            .create()

        val fatigue = gson.fromJson(string, FatigueTable::class.java)

        assertTrue(fatigue != null)
    }

    @org.junit.Test
    fun obtain_table_from_cursor_via_builder() {
        val repository = databaseComponent.repository()

        val table = DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!

        val cursor = repository.constCursorBuilder<CommonItem>(
            table.name, table.columns.toTypedArray()
        ).buildQuery {
            When(
                Query.Clause(
                    Columns.TAG.castString(), Query.Operations.EQ, Tags.TABLE.castString()
                )
            )
        }.setReader(CursorCommonItemReader).create()

        val item = cursor.getItem(0)

        val string = item.additional!!
        val gson = GsonBuilder()
            .create()

        val fatigue = gson.fromJson(string, FatigueTable::class.java)

        assertTrue(fatigue != null)
    }

    @org.junit.Test
    fun obtain_g0_table_from_cursor_via_builder() {
        val repository = databaseComponent.repository()

        val table = constMainTable()

        val cursor = repository.constCursorBuilder<CommonItem>(
            table.name, table.columns.toTypedArray()
        ).buildQuery {
            When(
                Query.Clause(
                    Columns.TAG.castString(), Query.Operations.EQ, Tags.TABLE.castString()
                )
            )
            and(
                Query.Clause(
                    Columns.TITLE.castString(), Query.Operations.EQ, GOSTableContract.G_0
                )
            )
        }.setReader(CursorCommonItemReader).create()

        // Подобная табличка должна быть только одна во всей таблице
        val item = cursor.getSingle()

        val string = item.additional!!
        val gson = GsonBuilder()
            .create()

        val g0 = gson.fromJson(string, G0Table::class.java)

        val expectedDomain = 3.55f
        val expectedFirst = 17f
        val expectedNull = null
        // Проверка предполагаемых значений
        g0.rows[0].domain.apply {
            assertTrue(expectedDomain == rightSide.num)
        }
        assertTrue(expectedFirst == g0.rows[0].list[0])

        assertTrue(expectedNull == g0.rows[5].list[0])
    }

    @org.junit.Test
    fun obtain_source_table_from_cursor_via_builder() {
        // Сначала получаем репозиторию - класс, хранящий ссылки на базы данных
        val repository = databaseComponent.repository()
        // Определяем таблицу, с которой будем работать (просто ее тип)
        val table = constMainTable()

        val cursor = repository
            // Билдим штуку для поиска по базе данных, указываем название таблицы
            // (не путать с гостовской ячейкой, содержащей таблицу), указываем названия колонок, которые надо отобразить
            .constCursorBuilder<CommonItem>(table.name, table.columns.toTypedArray())
            // теперь строим сам запрос поиска по строчкам
            .buildQuery {
                // первое условие начинается всегда с When
                When(
                    // В нее передается объект условия
                    Query.Clause(
                        // Название колонки, значение из которой будет браться для сравнения с данным
                        Columns.TITLE.castString(),
                        //  Операция сравнения. в данном случае равно
                        Query.Operations.EQ,
                        // Переданное значение для поиска (данное)
                        GOSTableContract.SOURCE_DATA
                    )
                )
                // Даем чувака который знает как читать ячейку указанной таблички
            }.setReader(CursorCommonItemReader)
            // Создаем чувака
            .create()

        val item = cursor.getSingle() // Получаем из чувака запись, которую нашли в табличке
        val gson = SourceDataTable.prepareGson() // готовим парсилку для создания класса таблички
        val sourceTable = gson.fromJson(
            item.additional,
            SourceDataTable::class.java
        ) // парсим json из таблицы и получаем сам гост-табличку
        // Проверяем что табличка не нулевая. Если не выкинуло ошибку и табличка не нуль - тест можно считать пройденным
        assertNotNull(sourceTable)
    }

    @org.junit.Test
    fun obtain_khe_kfe_table_from_cursor_via_builder() {
        val repository = databaseComponent.repository()
        val table = constMainTable()
        val cursor = repository
            .constCursorBuilder<CommonItem>(table.name, table.columns.toTypedArray())
            .buildQuery {
                When(
                    Query.Clause(
                        Columns.TITLE.castString(),
                        Query.Operations.EQ,
                        // Здесь ты пытаешься получить табличку с KHE и KFE - это верно!
                        GOSTableContract.FATIGUE_CALCULATION_23
                    )
                )
            }.setReader(CursorCommonItemReader)
            .create()

        val item = cursor.getSingle()
       /**
       Но вот здесь ты получаешься из additional накастить SOURCETABLE - это другая табличка
        Ты должен здесь использовать класс таблички, релевантной KHE и KFE

        val gson = SourceDataTable.prepareGson()
        val sourceTable = gson.fromJson(
            item.additional,
            FatigueTable::class.java
        )

        На этот класс [FatigueTable] (штука в квадратных скобках кликабельна) уже был юнит тест,
        можно найти по этому классу в модуле database.

        */
       // посмотри на этот класс
        val gson = FatigueTable.prepareGson()
        val fatigue = gson.fromJson(item.additional!!, FatigueTable::class.java)
        //assertNotNull(sourceTable)
        assertTrue(fatigue.rows[0].kHE == 1f)
    }

}