package com.reduction_technologies.database.helpers

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.reducetechnologies.tables_utils.TableHolder
import com.reduction_technologies.database.databases_utils.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

/**
 * DataBase helper for data set with pre-defined data. There must be stored no user data.
 * User data is stored in another database.
 * The main purpose of this helper class is to put an asset database into the
 * Android given place for storing databases.
 * To take locales into considerations, different versions of databases are stored.
 * Full list of locles is defined here {@link AppLocale}
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
        val inputStream = context.assets.open(databasePath)

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

    fun getTables(locale: AppLocale): TableHolder {
        val localizedTable= considerLocaleForTableName(locale)
        val cursor = getCommonCursorBuilder(localizedTable, mainTable.columns.toTypedArray())
            .buildQuery {
                When(
                    Query.Clause(
                        Columns.TAG.castString(), Query.Operations.EQ, Tags.TABLE.castString()
                    )
                )
            }
            .setReader(CursorCommonItemReader).create()
        Timber.i("Before extracting list")
        val list = cursor.getList()
        Timber.i("Extracted list")
        return list.extractTableHolder()
    }

    /**
     * Return all entities from main table
     */
    fun getAllItems(locale: AppLocale): List<CommonItem> {
        val localizedTable= considerLocaleForTableName(locale)
        val cursor = getCommonCursorBuilder(localizedTable, mainTable.columns.toTypedArray())
            .setReader(CursorCommonItemReader).create()

        return cursor.getList()
    }

    /**
     * Returns name of localized table
     */
    private fun considerLocaleForTableName(locale: AppLocale) : String {
        return "${mainTable.name}_${locale.name}"
    }

    companion object : DatabaseConstantsContract {
        override val database: DatabaseType =
            DatabaseType.Constant

        const val ASSETS_PATH = "databases"
        val databasePath = "$ASSETS_PATH/${database.title}.db"

        const val PREFERENCES_VERSION = "const_database_versions"
    }
}