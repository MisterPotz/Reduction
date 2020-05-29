package com.reduction_technologies.database.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.reduction_technologies.database.databases_utils.*
import com.reduction_technologies.database.helpers.ConstantDatabaseHelper.Companion.database
import timber.log.Timber

/**
 * DataBase helper for data set with user-defined data. Only user frequent changing data can be stored here.
 * @see ConstantDatabaseHelper
 */
internal class UserDatabaseHelper(val context: Context) :
    SQLiteOpenHelper(context, database.title, null, database.version),
    CursorBuilder {

    private val SQL_CREATE_ENTRIES =
        CommonItem.createTableWithContract(table)

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $table}"

    override fun onCreate(db: SQLiteDatabase?) {
        Timber.i("Creating user database")
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // dont update anything
    }

    fun insertCommonItem(commonItem: CommonItem) {
        val contentValues = commonItem.getContentValues()
        writableDatabase.insert(table, null, contentValues)
    }

    fun getList() : List<CommonItem> {
        val cursor = getCommonCursorBuilder(table, columns = columns.toTypedArray())
            .setReader(CursorCommonItemReader).create()

        return cursor.getList()
    }

    companion object :
        DatabaseConstantsContract {
        override val database: DatabaseType =
            DatabaseType.User

        val columns = database.tables[UserTables.Favorites]!!.columns
        val table = database.tables[UserTables.Favorites]!!.name
    }

    override fun getCommonCursorBuilder(
        tableName: String,
        columns: Array<String>
    ): RCursorAdapterBuilder<CommonItem> {
        return RCursorAdapterBuilder(this, tableName, columns)
    }
}