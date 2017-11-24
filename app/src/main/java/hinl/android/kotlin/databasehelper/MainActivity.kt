package hinl.android.kotlin.databasehelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import hinl.android.kotlin.databasehelper.example.database.DatabaseHelper
import hinl.android.kotlin.databasehelper.example.schema.Example
import hinl.kotlin.database.helper.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dataBase = DatabaseHelper(this@MainActivity)
        createTable(dataBase)
        insertContent(dataBase)
        insertListContent(dataBase)
        val listOfExample = readContent(dataBase)
        val updateExample = listOfExample?.get(0)
        updateExample?.columnOne = "Updated Column One"
        if (updateExample != null) {
            updateContent(dataBase, updateExample)
        }
        val deleteExample = listOfExample?.get(1)
        if (deleteExample != null) {
            deleteContent(dataBase, deleteExample)
        }
    }

    fun createTable(database: DatabaseHelper) {
        database.createTable(Example::class)
    }

    fun insertContent(database: DatabaseHelper) {
        val exampleObject1 = Example(columnOne = "One", columnTwo = 1, columnThree = Date())
        val exampleObject2 = Example(columnOne = "Two", columnTwo = 2, columnThree = Date())
        val exampleObject3 = Example(columnOne = "Three", columnTwo = 3, columnThree = Date())
        val exampleObject4 = Example(columnOne = "Four", columnTwo = 4, columnThree = Date())
        val exampleObject5 = Example(columnOne = "Five", columnTwo = 5, columnThree = Date())

        database.insert(exampleObject1)
        database.insert(exampleObject2)
        database.insert(exampleObject3)
        database.insert(exampleObject4)
        database.insert(exampleObject5)
    }

    fun insertListContent(database: DatabaseHelper) {
        val listOfExample = arrayListOf(
                Example(columnOne = "List One", columnTwo = 1, columnThree = Date()),
                Example(columnOne = "List Two", columnTwo = 2, columnThree = Date()),
                Example(columnOne = "List Three", columnTwo = 3, columnThree = Date()),
                Example(columnOne = "List Four", columnTwo = 4, columnThree = Date()),
                Example(columnOne = "List Five", columnTwo = 5, columnThree = Date())
        )
        database.insert(listOfExample)
    }

    fun readContent(database: DatabaseHelper): List<Example>? = database.get(Example::class)

    fun updateContent(database: DatabaseHelper, exampleObject: Example) {
        database.update(exampleObject)
    }

    fun deleteContent(database: DatabaseHelper, exampleObject: Example) {
        database.delete(exampleObject)
    }
}