package hinl.android.kotlin.databasehelper.example.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import hinl.kotlin.database.helper.SQLiteDatabaseHelper


class DatabaseHelper(context: Context): SQLiteDatabaseHelper(
        context = context,
        name = "ExampleDatabase.db",
        factory = null,
        version = 1) {

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}