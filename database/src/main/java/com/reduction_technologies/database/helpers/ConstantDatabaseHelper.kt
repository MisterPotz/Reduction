package com.reduction_technologies.database.helpers

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.databases_utils.*
import java.io.File
import java.io.FileOutputStream

/**
 * DataBase helper for data set with pre-defined data. There must be stored no user data.
 * User data is stored in another database.
 * The main purpose of this helper class is to put an asset database into the
 * Android given place for storing databases.
 */
internal class ConstantDatabaseHelper(val context: Context) :
    SQLiteOpenHelper(context, database.title, null, database.version),
    CursorBuilder {

    val mainTable = DatabaseType.Constant.tables[ConstTables.EncyclopediaItems]!!

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "${context.packageName}.$PREFERENCES_VERSION",
        Context.MODE_PRIVATE
    )

    private fun installedDatabaseIsOutdated(): Boolean {
        return preferences.getInt(database.title, 0) < database.version
    }

    private fun writeDatabaseVersionInPreferences() {
        preferences.edit().apply {
            putInt(database.title, database.version)
            apply()
        }
    }

    private fun installDatabaseFromAssets() {
        val inputStream = context.assets.open("$ASSETS_PATH/${database.title}.db")

        try {
            val outputFile = File(context.getDatabasePath(database.title).path)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException(
                "The ${database.title} database couldn't be installed.",
                exception
            )
        }
    }

    @Synchronized
    private fun installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            context.deleteDatabase(database.title)
            installDatabaseFromAssets()
            writeDatabaseVersionInPreferences()
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {

        throw RuntimeException("The ${database.title} database is not writable.")
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getReadableDatabase()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Nothing to do
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Nothing to do
    }

    override fun getCommonCursorBuilder(
        tableName: String,
        columns: Array<String>
    ): RCursorAdapterBuilder<CommonItem> {
        return RCursorAdapterBuilder(this, tableName, columns)
    }

    fun getTables(): TableHolder {
        val cursor = getCommonCursorBuilder(mainTable.name, mainTable.columns.toTypedArray())
            .buildQuery {
                When(
                    Query.Clause(
                        Columns.TAG.castString(), Query.Operations.EQ, Tags.TABLE.castString()
                    )
                )
            }
            .setReader(CursorCommonItemReader).create()
        val list = cursor.getList()
        return list.extractTableHolder()
    }

    /**
     * Return all entities from main table
     */
    fun getAllItems(): List<CommonItem> {
        val cursor = getCommonCursorBuilder(mainTable.name, mainTable.columns.toTypedArray())
            .setReader(CursorCommonItemReader).create()

        return cursor.getList()
    }

    companion object : DatabaseConstantsContract {
        override val database: DatabaseType =
            DatabaseType.Constant

        const val ASSETS_PATH = "databases"
        const val PREFERENCES_VERSION = "const_database_versions"
    }
}