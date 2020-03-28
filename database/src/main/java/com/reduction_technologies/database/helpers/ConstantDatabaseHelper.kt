package com.reduction_technologies.database.helpers

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.reduction_technologies.database.databases_utils.DatabaseConstantsContract
import com.reduction_technologies.database.databases_utils.DatabaseType
import java.io.File
import java.io.FileOutputStream

/**
 * DataBase helper for data set with pre-defined data. There must be stored no user data.
 * User data is stored in another database.
 * The main purpose of this helper class is to put an asset database into the
 * Android given place for storing databases.
 */
class ConstantDatabaseHelper(val context: Context) : SQLiteOpenHelper(context, database.title, null, database.version) {
// TODO make an additional interface between output of this class and sqlite. this must return a list of commonitems
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
            throw RuntimeException("The ${database.title} database couldn't be installed.", exception)
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

    companion object :
        DatabaseConstantsContract {
        override val database: DatabaseType =
            DatabaseType.Constant
        // ------
        const val ASSETS_PATH = "databases"
        const val PREFERENCES_VERSION = "const_database_versions"
    }
}